package cn.youyi.dockerv.http.route.impl;

import cn.youyi.dockerv.docker.command.DockerImages;
import cn.youyi.dockerv.docker.command.DockerLoad;
import cn.youyi.dockerv.docker.command.DockerPull;
import cn.youyi.dockerv.docker.session.DockerSession;
import cn.youyi.dockerv.docker.session.DockerSessionContainer;
import cn.youyi.dockerv.http.context.RoutingContextHelper;
import cn.youyi.dockerv.http.exception.CustomException;
import cn.youyi.dockerv.http.handler.ParamValidationHandler;
import cn.youyi.dockerv.http.paramvalidation.NotBlankParam;
import cn.youyi.dockerv.http.route.MountableRoute;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ImageRoute implements MountableRoute {

  @Override
  public void mount(Router router) {
    router.get("/api/image/list")
      .handler(ParamValidationHandler
        .create()
      .addParam(new NotBlankParam("sessionId")))
      .handler(this::list);
    router.post("/api/image/pull")
      .handler(ParamValidationHandler
        .create()
        .addParam(new NotBlankParam("sessionId"))
        .addParam(new NotBlankParam("image")))
      .handler(this::pull);
    router.post("/api/image/load")
      .handler(ParamValidationHandler
        .create()
        .addParam(new NotBlankParam("sessionId"))
        .addParam(new NotBlankParam("tarFilePath")))
      .handler(this::load);
  }

  /**
   * load image from a tar file
   * @param context
   */
  private void load(RoutingContext context) {
    String sessionId = RoutingContextHelper.getRequestParam(context, "sessionId");
    String tarFilePath = RoutingContextHelper.getRequestParam(context, "tarFilePath");
    try {
      DockerSession session = DockerSessionContainer.getSession(sessionId);
      String command = DockerLoad.Command.create(tarFilePath).addOption("-q").get();
      String out = session.getCmdExecutor().command(command);
      DockerLoad.Parser parser = DockerLoad.Parser.create(out);
      if (parser.success()) {
        RoutingContextHelper.success(context, tarFilePath);
      } else {
        context.fail(new CustomException(out));
      }
    } catch (Exception e) {
      context.fail(e);
    }
  }

  /**
   * pull an image
   * @param context
   */
  private void pull(RoutingContext context) {
    String sessionId = RoutingContextHelper.getRequestParam(context, "sessionId");
    String image = RoutingContextHelper.getRequestParam(context, "image");
    try {
      DockerSession session = DockerSessionContainer.getSession(sessionId);
      String command = DockerPull.Command.create(image).get();
      String out = session.getCmdExecutor().command(command);
      DockerPull.Parser parser = DockerPull.Parser.create(out);
      if (parser.success()) {
        RoutingContextHelper.success(context, image);
      } else {
        context.fail(new CustomException(out));
      }
    } catch (Exception e) {
      context.fail(e);
    }
  }

  /**
   * query image list
   *
   * @param context
   */
  private void list(RoutingContext context) {
    String sessionId = RoutingContextHelper.getRequestParam(context, "sessionId");
    String REPOSITORY = RoutingContextHelper.getRequestParam(context, "REPOSITORY");
    String TAG = RoutingContextHelper.getRequestParam(context, "TAG");
    String IMAGE_ID = RoutingContextHelper.getRequestParam(context, "IMAGE_ID");
    try {
      DockerSession session = DockerSessionContainer.getSession(sessionId);
      String command = DockerImages.Command.create().get();
      String out = session.getCmdExecutor().command(command);
      List<JsonObject> images = DockerImages.Parser.create(out).parse()
        .stream()
        .filter(image -> StringUtils.isBlank(REPOSITORY) || image.getString("REPOSITORY").contains(REPOSITORY))
        .filter(image -> StringUtils.isBlank(TAG) || image.getString("TAG").contains(TAG))
        .filter(image -> StringUtils.isBlank(IMAGE_ID) || image.getString("IMAGE_ID").contains(IMAGE_ID))
        .collect(Collectors.toList());
      RoutingContextHelper.success(context, images);
    } catch (Exception e) {
      context.fail(e);
    }
  }

}

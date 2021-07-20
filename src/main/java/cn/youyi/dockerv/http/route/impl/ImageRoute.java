package cn.youyi.dockerv.http.route.impl;

import cn.youyi.dockerv.docker.command.*;
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
    router.post("/api/image/build")
      .handler(ParamValidationHandler
        .create()
        .addParam(new NotBlankParam("sessionId"))
        .addParam(new NotBlankParam("dockerfileDirPath"))
        .addParam(new NotBlankParam("repository"))
        .addParam(new NotBlankParam("tag")))
      .handler(this::build);
    router.post("/api/image/save")
      .handler(ParamValidationHandler
        .create()
        .addParam(new NotBlankParam("sessionId"))
        .addParam(new NotBlankParam("repository"))
        .addParam(new NotBlankParam("tag"))
        .addParam(new NotBlankParam("savePath")))
      .handler(this::save);
    router.post("/api/image/remove")
      .handler(ParamValidationHandler
        .create()
        .addParam(new NotBlankParam("sessionId"))
        .addParam(new NotBlankParam("imageId")))
      .handler(this::remove);
  }

  /**
   * save an image as a tar file
   *
   * @param context
   */
  private void save(RoutingContext context) {
    String sessionId = RoutingContextHelper.getRequestParam(context, "sessionId");
    String repository = RoutingContextHelper.getRequestParam(context, "repository");
    String tag = RoutingContextHelper.getRequestParam(context, "tag");
    String savePath = RoutingContextHelper.getRequestParam(context, "savePath");
    try {
      DockerSession session = DockerSessionContainer.getSession(sessionId);
      String command = DockerSave.Command.create(repository, tag).addOption("-o " + savePath).get();
      String out = session.getCmdExecutor().command(command);
      DockerSave.Parser parser = DockerSave.Parser.create(out);
      if (parser.success()) {
        RoutingContextHelper.success(context, repository + ":" + tag);
      } else {
        context.fail(new CustomException(out));
      }
    } catch (Exception e) {
      context.fail(e);
    }
  }

  /**
   * build an image from Dockerfile
   *
   * @param context
   */
  private void build(RoutingContext context) {
    String sessionId = RoutingContextHelper.getRequestParam(context, "sessionId");
    String dockerfileDirPath = RoutingContextHelper.getRequestParam(context, "dockerfileDirPath");
    String repository = RoutingContextHelper.getRequestParam(context, "repository");
    String tag = RoutingContextHelper.getRequestParam(context, "tag");
    try {
      DockerSession session = DockerSessionContainer.getSession(sessionId);
      String command = DockerBuild.Command.create(dockerfileDirPath, repository, tag).get();
      String out = session.getCmdExecutor().command(command);
      DockerBuild.Parser parser = DockerBuild.Parser.create(out);
      if (parser.success()) {
        RoutingContextHelper.success(context, repository + ":" + tag);
      } else {
        context.fail(new CustomException(out));
      }
    } catch (Exception e) {
      context.fail(e);
    }
  }

  /**
   * remove an image
   *
   * @param context
   */
  private void remove(RoutingContext context) {
    String sessionId = RoutingContextHelper.getRequestParam(context, "sessionId");
    String imageId = RoutingContextHelper.getRequestParam(context, "imageId");
    try {
      DockerSession session = DockerSessionContainer.getSession(sessionId);
      String command = DockerRmi.Command.create(imageId).get();
      String out = session.getCmdExecutor().command(command);
      DockerRmi.Parser parser = DockerRmi.Parser.create(out, imageId);
      if (parser.success()) {
        RoutingContextHelper.success(context, imageId);
      } else {
        context.fail(new CustomException(out));
      }
    } catch (Exception e) {
      context.fail(e);
    }
  }

  /**
   * load image from a tar file
   *
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
   *
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
      // query images
      List<JsonObject> images = DockerImages.Parser.create(out).parse()
        .stream()
        .filter(image -> StringUtils.isBlank(REPOSITORY) || image.getString("REPOSITORY").contains(REPOSITORY))
        .filter(image -> StringUtils.isBlank(TAG) || image.getString("TAG").contains(TAG))
        .filter(image -> StringUtils.isBlank(IMAGE_ID) || image.getString("IMAGE_ID").contains(IMAGE_ID))
        .collect(Collectors.toList());
      // query containers
      String psCommand = DockerPs.Command.create().addOption("-a").get();
      String psOut = session.getCmdExecutor().command(psCommand);
      List<JsonObject> containers = DockerPs.Parser.create(psOut).parse();
      // count image's containers
      images.forEach(image -> {
        long upCount = containers.stream().filter(container -> (image.getString("REPOSITORY").endsWith(container.getString("IMAGE")) || image.getString("IMAGE_ID").endsWith(container.getString("IMAGE")))
          && container.getString("STATUS").startsWith("Up")).count();
        long exitedCount = containers.stream().filter(container -> (image.getString("REPOSITORY").endsWith(container.getString("IMAGE")) || image.getString("IMAGE_ID").endsWith(container.getString("IMAGE")))
          && container.getString("STATUS").startsWith("Exited")).count();
        image.put("CONTAINERS_TOTAL", upCount + exitedCount);
        image.put("CONTAINERS_UP", upCount);
        image.put("CONTAINERS_EXITED", exitedCount);
      });
      RoutingContextHelper.success(context, images);
    } catch (Exception e) {
      context.fail(e);
    }
  }

}

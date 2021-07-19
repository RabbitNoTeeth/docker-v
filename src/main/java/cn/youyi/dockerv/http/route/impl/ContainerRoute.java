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
import java.util.Optional;
import java.util.stream.Collectors;

public class ContainerRoute implements MountableRoute {

  @Override
  public void mount(Router router) {
    router.get("/api/container/list")
      .handler(ParamValidationHandler
        .create()
        .addParam(new NotBlankParam("sessionId")))
      .handler(this::list);
    router.post("/api/container/run")
      .handler(ParamValidationHandler
        .create()
        .addParam(new NotBlankParam("sessionId"))
        .addParam(new NotBlankParam("name"))
        .addParam(new NotBlankParam("imageId")))
      .handler(this::run);
    router.post("/api/container/start")
      .handler(ParamValidationHandler
        .create()
        .addParam(new NotBlankParam("sessionId"))
        .addParam(new NotBlankParam("containerId")))
      .handler(this::start);
    router.post("/api/container/stop")
      .handler(ParamValidationHandler
        .create()
        .addParam(new NotBlankParam("sessionId"))
        .addParam(new NotBlankParam("containerId")))
      .handler(this::stop);
    router.post("/api/container/remove")
      .handler(ParamValidationHandler
        .create()
        .addParam(new NotBlankParam("sessionId"))
        .addParam(new NotBlankParam("containerId")))
      .handler(this::remove);
  }

  /**
   * run a new container
   *
   * @param context
   */
  private void run(RoutingContext context) {
    String sessionId = RoutingContextHelper.getRequestParam(context, "sessionId");
    String imageId = RoutingContextHelper.getRequestParam(context, "imageId");
    String name = RoutingContextHelper.getRequestParam(context, "name");
    String options = RoutingContextHelper.getRequestParam(context, "options");
    try {
      DockerSession session = DockerSessionContainer.getSession(sessionId);
      DockerRun.Command cmd = DockerRun.Command.create(imageId);
      if (StringUtils.isNotBlank(options)) {
        cmd.addOption(options);
      }
      String command = cmd.addOption("--name " + name).get();
      String out = session.getCmdExecutor().command(command);
      DockerRun.Parser parser = DockerRun.Parser.create(out);
      if (parser.success()) {
        RoutingContextHelper.success(context, out);
      } else {
        context.fail(new CustomException(out));
      }
    } catch (Exception e) {
      context.fail(e);
    }
  }

  /**
   * remove a container
   *
   * @param context
   */
  private void remove(RoutingContext context) {
    String sessionId = RoutingContextHelper.getRequestParam(context, "sessionId");
    String containerId = RoutingContextHelper.getRequestParam(context, "containerId");
    try {
      DockerSession session = DockerSessionContainer.getSession(sessionId);
      String command = DockerRm.Command.create(containerId).get();
      String out = session.getCmdExecutor().command(command);
      DockerRm.Parser parser = DockerRm.Parser.create(out, containerId);
      if (parser.success()) {
        RoutingContextHelper.success(context, containerId);
      } else {
        context.fail(new CustomException(out));
      }
    } catch (Exception e) {
      context.fail(e);
    }
  }

  /**
   * stop a container
   *
   * @param context
   */
  private void stop(RoutingContext context) {
    String sessionId = RoutingContextHelper.getRequestParam(context, "sessionId");
    String containerId = RoutingContextHelper.getRequestParam(context, "containerId");
    try {
      DockerSession session = DockerSessionContainer.getSession(sessionId);
      String command = DockerStop.Command.create(containerId).get();
      String out = session.getCmdExecutor().command(command);
      DockerStop.Parser parser = DockerStop.Parser.create(out, containerId);
      if (parser.success()) {
        RoutingContextHelper.success(context, containerId);
      } else {
        context.fail(new CustomException(out));
      }
    } catch (Exception e) {
      context.fail(e);
    }
  }

  /**
   * start a container
   *
   * @param context
   */
  private void start(RoutingContext context) {
    String sessionId = RoutingContextHelper.getRequestParam(context, "sessionId");
    String containerId = RoutingContextHelper.getRequestParam(context, "containerId");
    try {
      DockerSession session = DockerSessionContainer.getSession(sessionId);
      String command = DockerStart.Command.create(containerId).get();
      String out = session.getCmdExecutor().command(command);
      DockerStart.Parser parser = DockerStart.Parser.create(out, containerId);
      if (parser.success()) {
        RoutingContextHelper.success(context, containerId);
      } else {
        context.fail(new CustomException(out));
      }
    } catch (Exception e) {
      context.fail(e);
    }
  }

  /**
   * query container list
   *
   * @param context
   */
  private void list(RoutingContext context) {
    String sessionId = RoutingContextHelper.getRequestParam(context, "sessionId");
    String NAMES = RoutingContextHelper.getRequestParam(context, "NAMES");
    String CONTAINER_ID = RoutingContextHelper.getRequestParam(context, "CONTAINER_ID");
    String IMAGE = RoutingContextHelper.getRequestParam(context, "IMAGE");
    try {
      DockerSession session = DockerSessionContainer.getSession(sessionId);
      // query containers
      String command = DockerPs.Command.create().addOption("-a").get();
      String out = session.getCmdExecutor().command(command);
      List<JsonObject> containers = DockerPs.Parser.create(out).parse();
      // query images
      String imagesCommand = DockerImages.Command.create().get();
      String imagesCOut = session.getCmdExecutor().command(imagesCommand);
      List<JsonObject> images = DockerImages.Parser.create(imagesCOut).parse();
      // filter containers
      containers = containers
        .stream()
        .peek(container -> {
          String image = container.getString("IMAGE");
          Optional<JsonObject> targetImageOptional = images.stream().filter(i -> image.equals(i.getString("IMAGE_ID")) || i.getString("REPOSITORY").endsWith(image)).findFirst();
          if (targetImageOptional.isPresent()) {
            JsonObject targetImage = targetImageOptional.get();
            container.put("IMAGE", targetImage.getString("REPOSITORY") + ":" + targetImage.getString("TAG"));
            container.put("IMAGE_ID", targetImage.getString("IMAGE_ID"));
          }
        })
        .filter(container -> StringUtils.isBlank(NAMES) || container.getString("NAMES").contains(NAMES))
        .filter(container -> StringUtils.isBlank(CONTAINER_ID) || container.getString("CONTAINER_ID").contains(CONTAINER_ID))
        .filter(container -> StringUtils.isBlank(IMAGE) || container.getString("IMAGE_ID").contains(IMAGE) || container.getString("IMAGE").contains(IMAGE))
        .collect(Collectors.toList());
      RoutingContextHelper.success(context, containers);
    } catch (Exception e) {
      context.fail(e);
    }
  }

}

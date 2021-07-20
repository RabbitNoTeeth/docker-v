package cn.youyi.dockerv.http.route.impl;

import cn.youyi.dockerv.docker.command.DockerCommand;
import cn.youyi.dockerv.docker.command.impl.*;
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
    router.post("/api/container/add")
      .handler(ParamValidationHandler
        .create()
        .addParam(new NotBlankParam("sessionId"))
        .addParam(new NotBlankParam("name"))
        .addParam(new NotBlankParam("imageId"))
        .addParam(new NotBlankParam("directStart")))
      .handler(this::add);
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
  private void add(RoutingContext context) {
    String sessionId = RoutingContextHelper.getRequestParam(context, "sessionId");
    String imageId = RoutingContextHelper.getRequestParam(context, "imageId");
    String name = RoutingContextHelper.getRequestParam(context, "name");
    String options = RoutingContextHelper.getRequestParam(context, "options");
    String exCommand = RoutingContextHelper.getRequestParam(context, "command");
    String directStart = RoutingContextHelper.getRequestParam(context, "directStart");
    try {
      DockerSession session = DockerSessionContainer.getSession(sessionId);
      DockerCommand dockerCommand = "1".equals(directStart) ? DockerRun.create(imageId) : DockerCreate.create(imageId);
      DockerCommand.Command command = dockerCommand.command();
      if (StringUtils.isNotBlank(options)) {
        command.addOption(options);
      }
      if (StringUtils.isNotBlank(exCommand)) {
        command.addCommand(exCommand);
      }
      String commandStr = command.addOption("--name " + name).getStr();
      String out = session.getCmdExecutor().command(commandStr);
      DockerCommand.Parser parser = dockerCommand.parser(out);
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
      DockerCommand dockerCommand = DockerRm.create(containerId);
      DockerCommand.Command command = dockerCommand.command();
      String commandStr = command.getStr();
      String out = session.getCmdExecutor().command(commandStr);
      DockerCommand.Parser parser = dockerCommand.parser(out);
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
      DockerCommand dockerCommand = DockerStop.create(containerId);
      DockerCommand.Command command = dockerCommand.command();
      String commandStr = command.getStr();
      String out = session.getCmdExecutor().command(commandStr);
      DockerCommand.Parser parser = dockerCommand.parser(out);
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
      DockerCommand dockerCommand = DockerStart.create(containerId);
      DockerCommand.Command command = dockerCommand.command();
      String commandStr = command.getStr();
      String out = session.getCmdExecutor().command(commandStr);
      DockerCommand.Parser parser = dockerCommand.parser(out);
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
      DockerCommand dockerCommand = DockerPs.create();
      DockerCommand.Command command = dockerCommand.command();
      String commandStr = command.addOption("-a").getStr();
      String out = session.getCmdExecutor().command(commandStr);
      DockerCommand.Parser parser = dockerCommand.parser(out);
      List<JsonObject> containers = parser.parse();
      // query images
      DockerCommand dockerImagesCommand = DockerImages.create();
      DockerCommand.Command imagesCommand = dockerImagesCommand.command();
      String imagesCommandStr = imagesCommand.getStr();
      String imagesOut = session.getCmdExecutor().command(imagesCommandStr);
      DockerCommand.Parser dockerImagesParser = dockerImagesCommand.parser(imagesOut);
      List<JsonObject> images = dockerImagesParser.parse();
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

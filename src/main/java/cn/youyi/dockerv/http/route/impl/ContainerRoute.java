package cn.youyi.dockerv.http.route.impl;

import cn.youyi.dockerv.docker.command.DockerPs;
import cn.youyi.dockerv.docker.command.DockerRm;
import cn.youyi.dockerv.docker.command.DockerStart;
import cn.youyi.dockerv.docker.command.DockerStop;
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

import java.util.List;

public class ContainerRoute implements MountableRoute {

  @Override
  public void mount(Router router) {
    router.get("/api/container/list")
      .handler(ParamValidationHandler
        .create()
      .addParam(new NotBlankParam("sessionId")))
      .handler(this::list);
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
   * remove a container
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
   * @param routingContext
   */
  private void list(RoutingContext routingContext) {
    String sessionId = RoutingContextHelper.getRequestParam(routingContext, "sessionId");
    try {
      DockerSession session = DockerSessionContainer.getSession(sessionId);
      String command = DockerPs.Command.create().addOption("-a").get();
      String out = session.getCmdExecutor().command(command);
      List<JsonObject> containers = DockerPs.Parser.create(out).parse();
      RoutingContextHelper.success(routingContext, containers);
    } catch (Exception e) {
      routingContext.fail(e);
    }
  }

}

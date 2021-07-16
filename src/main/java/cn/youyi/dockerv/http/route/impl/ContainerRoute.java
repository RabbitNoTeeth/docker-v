package cn.youyi.dockerv.http.route.impl;

import cn.youyi.dockerv.docker.command.DockerPs;
import cn.youyi.dockerv.docker.session.DockerSession;
import cn.youyi.dockerv.docker.session.DockerSessionContainer;
import cn.youyi.dockerv.http.context.RoutingContextHelper;
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

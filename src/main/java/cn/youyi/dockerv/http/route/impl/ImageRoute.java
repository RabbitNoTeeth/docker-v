package cn.youyi.dockerv.http.route.impl;

import cn.youyi.dockerv.docker.command.DockerImages;
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

public class ImageRoute implements MountableRoute {

  @Override
  public void mount(Router router) {
    router.get("/api/image/list")
      .handler(ParamValidationHandler
        .create()
      .addParam(new NotBlankParam("sessionId")))
      .handler(this::list);
  }

  /**
   * query image list
   *
   * @param routingContext
   */
  private void list(RoutingContext routingContext) {
    String sessionId = RoutingContextHelper.getRequestParam(routingContext, "sessionId");
    DockerSession session = DockerSessionContainer.getSession(sessionId);
    if (session == null) {
      routingContext.fail(new CustomException("there is no session with id '" + sessionId + "'"));
    }
    String command = DockerImages.Command.create().get();
    try {
      String out = session.getCmdExecutor().command(command);
      List<JsonObject> images = DockerImages.Parser.create(out).parse();
      RoutingContextHelper.success(routingContext, images);
    } catch (Exception e) {
      routingContext.fail(e);
    }
  }

}

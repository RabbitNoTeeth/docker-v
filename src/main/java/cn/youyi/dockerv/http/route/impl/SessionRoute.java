package cn.youyi.dockerv.http.route.impl;

import cn.youyi.dockerv.docker.session.DockerSession;
import cn.youyi.dockerv.docker.session.DockerSessionContainer;
import cn.youyi.dockerv.http.exception.CustomException;
import cn.youyi.dockerv.http.handler.ParamValidationHandler;
import cn.youyi.dockerv.http.paramvalidation.NotBlankParam;
import cn.youyi.dockerv.http.route.MountableRoute;
import cn.youyi.dockerv.http.context.RoutingContextHelper;
import cn.youyi.dockerv.ssh.SSHConnection;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.stream.Collectors;

public class SessionRoute implements MountableRoute {

  @Override
  public void mount(Router router) {
    router.get("/api/session/list").handler(this::list);
    router
      .post("/api/session/add")
      .handler(ParamValidationHandler
        .create()
        .addParam(new NotBlankParam("os"))
        .addParam(new NotBlankParam("name"))
        .addParam(new NotBlankParam("host")))
      .handler(this::add);
    router
      .post("/api/session/close")
      .handler(ParamValidationHandler
        .create()
        .addParam(new NotBlankParam("sessionId")))
      .handler(this::close);
  }

  /**
   * close a session
   * @param context
   */
  private void close(RoutingContext context) {
    String sessionId = RoutingContextHelper.getRequestParam(context, "sessionId");
    try {
      DockerSessionContainer.removeSession(sessionId);
      RoutingContextHelper.success(context, sessionId);
    } catch (Exception e) {
      context.fail(e);
    }
  }

  /**
   * create session
   *
   * @param routingContext
   */
  private void add(RoutingContext routingContext) {
    String os = RoutingContextHelper.getRequestParam(routingContext, "os");
    try {
      if ("LINUX".equals(os)) {
        String name = RoutingContextHelper.getRequestParam(routingContext, "name");
        String host = RoutingContextHelper.getRequestParam(routingContext, "host");
        String port = RoutingContextHelper.getRequestParam(routingContext, "port");
        String user = RoutingContextHelper.getRequestParam(routingContext, "user");
        String password = RoutingContextHelper.getRequestParam(routingContext, "password");
        SSHConnection sshConnection = new SSHConnection(host, Integer.parseInt(port), user, password);
        DockerSession dockerSession = new DockerSession(name, host, sshConnection);
        DockerSessionContainer.addSession(dockerSession);
        JsonObject res = new JsonObject().put("id", dockerSession.getId()).put("name", dockerSession.getName()).put("host", dockerSession.getHost());
        RoutingContextHelper.success(routingContext, res);
      } else {
        routingContext.fail(new CustomException("the value of param [os] is invalid"));
      }
    } catch (Exception e) {
      routingContext.fail(e);
    }

  }

  /**
   * query session list
   *
   * @param routingContext
   */
  private void list(RoutingContext routingContext) {
    List<DockerSession> sessions = DockerSessionContainer.getSessions();
    List<JsonObject> res = sessions
      .stream()
      .map(session -> new JsonObject().put("id", session.getId()).put("name", session.getName()).put("host", session.getHost()))
      .collect(Collectors.toList());
    RoutingContextHelper.success(routingContext, res);
  }

}

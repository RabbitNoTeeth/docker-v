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
  }

  /**
   * create session
   *
   * @param routingContext
   */
  private void add(RoutingContext routingContext) {
    String os = RoutingContextHelper.getRequestParam(routingContext, "os");
    if ("LINUX".equals(os)) {
      String name = RoutingContextHelper.getRequestParam(routingContext, "name");
      String host = RoutingContextHelper.getRequestParam(routingContext, "host");
      String port = RoutingContextHelper.getRequestParam(routingContext, "port");
      String user = RoutingContextHelper.getRequestParam(routingContext, "user");
      String password = RoutingContextHelper.getRequestParam(routingContext, "password");
      SSHConnection sshConnection = new SSHConnection(host, Integer.parseInt(port), user, password);
      DockerSession dockerSession = new DockerSession(name, host, sshConnection);
      DockerSessionContainer.addSession(dockerSession);
      RoutingContextHelper.success(routingContext, dockerSession.getId());
    } else {
      routingContext.fail(new CustomException("the value of param [os] is invalid"));
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

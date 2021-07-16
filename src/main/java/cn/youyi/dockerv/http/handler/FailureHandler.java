package cn.youyi.dockerv.http.handler;

import cn.youyi.dockerv.docker.session.DockerCmdExecException;
import cn.youyi.dockerv.docker.session.DockerSessionNotExistException;
import cn.youyi.dockerv.http.exception.CustomException;
import cn.youyi.dockerv.http.paramvalidation.ParamValidationException;
import cn.youyi.dockerv.ssh.SSHConnectException;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class FailureHandler implements Handler<RoutingContext> {

  @Override
  public void handle(RoutingContext context) {
    Throwable err = context.failure();
    JsonObject res = new JsonObject()
      .put("success", false)
      .put("data", null);
    if (err instanceof ParamValidationException) {
      res.put("message", err.getMessage());
    } else if (err instanceof SSHConnectException) {
      res.put("message", err.getMessage());
    } else if (err instanceof DockerSessionNotExistException) {
      res.put("message", err.getMessage());
    } else if (err instanceof DockerCmdExecException) {
      res.put("message", err.getMessage());
    } else if (err instanceof CustomException) {
      res.put("message", err.getMessage());
    } else {
      res.put("message", "there are some unexpected error, please check the application log!");
    }
    context.response().setStatusCode(200);
    context.response().end(res.encode());
  }

}

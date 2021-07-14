package cn.youyi.dockerv.http.handler;

import cn.youyi.dockerv.http.paramvalidation.ParamValidationException;
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
    } else {
      res.put("message", "there are some unexpected error, please check the application log!");
    }
    context.response().setStatusCode(200);
    context.response().end(res.encode());
  }

}

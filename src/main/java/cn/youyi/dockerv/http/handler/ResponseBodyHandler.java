package cn.youyi.dockerv.http.handler;

import cn.youyi.dockerv.http.context.RoutingContextHelper;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * response body handle, convert the response data to a special json str
 */
public class ResponseBodyHandler implements Handler<RoutingContext> {

  @Override
  public void handle(RoutingContext ctx) {
    Object successData = RoutingContextHelper.getSuccessData(ctx);
    if (successData != null) {
      // if there is data in the routing context, then warp the data to a special json
      JsonObject data = new JsonObject()
        .put("success", true)
        .put("message", "request complete")
        .put("data", successData);
      ctx.response().write(data.encode());
    }
    ctx.next();
  }

}

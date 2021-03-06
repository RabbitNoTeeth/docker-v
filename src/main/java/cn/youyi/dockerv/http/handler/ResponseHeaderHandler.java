package cn.youyi.dockerv.http.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * response pre-handler, set some headers of response
 */
public class ResponseHeaderHandler implements Handler<RoutingContext> {

  @Override
  public void handle(RoutingContext ctx) {
    ctx.response().putHeader("Content-Type", "application/json;charset=UTF-8");
    ctx.response().setChunked(true);
    ctx.next();
  }

}

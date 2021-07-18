package cn.youyi.dockerv.http.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * response final-handler, end the response
 */
public class ResponseFinalHandler implements Handler<RoutingContext> {

  @Override
  public void handle(RoutingContext ctx) {
    ctx.response().end();
  }

}

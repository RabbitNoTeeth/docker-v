package cn.youyi.dockerv.http.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * handle browser cross
 */
public class CrossHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext ctx) {
        String origin = ctx.request().getHeader("Origin");
        if(origin != null) {
            ctx.response().putHeader("Access-Control-Allow-Origin",origin);
        }

        String requestHeaders = ctx.request().getHeader("Access-Control-Request-Headers");
        if(requestHeaders != null) {
            ctx.response().putHeader("Access-Control-Allow-Headers",requestHeaders);
        }

        ctx.response()
                .putHeader("Access-Control-Allow-Methods","GET,POST,PUT,DELETE")
                .putHeader("Access-Control-Allow-Credentials","true");

        ctx.next();
    }

}

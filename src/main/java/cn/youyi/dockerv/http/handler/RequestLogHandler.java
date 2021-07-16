package cn.youyi.dockerv.http.handler;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * request log
 */
public class RequestLogHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RequestLogHandler.class);

  @Override
  public void handle(RoutingContext ctx) {
    String path = ctx.request().path();
    String method = ctx.request().method().name();
    MultiMap params = ctx.request().params();
    StringBuilder paramsSb = new StringBuilder();
    params.forEach(entry -> {
      paramsSb.append("\t").append(entry).append("\n");
    });
    LOGGER.info(">>> http >>> {}:{}\nparams: {\n{}}", method, path, paramsSb);
    ctx.next();
  }

}

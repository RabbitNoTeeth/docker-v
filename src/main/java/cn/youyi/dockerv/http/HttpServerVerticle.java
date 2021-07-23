package cn.youyi.dockerv.http;

import cn.youyi.dockerv.http.context.HttpServerContext;
import cn.youyi.dockerv.http.handler.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServerVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

  @Override
  public void start() throws Exception {

    Router router = Router.router(this.vertx);

    // set handlers which work before sub route
    router
      .route()
      .handler(BodyHandler.create())
      .handler(new CrossHandler())
      .handler(new ResponseHeaderHandler())
      .handler(new RequestLogHandler());

    // mount sub routes
    HttpServerContext.getRoutes().forEach(route -> {
      route.mount(router);
      LOGGER.info("mounted route [{}] success", route.getClass().getSimpleName());
    });

    // set handlers which work after sub route
    router
      .route()
      .handler(new ResponseBodyHandler())
      .handler(StaticHandler.create("web").setIndexPage("index.html"))
      .handler(new ResponseFinalHandler())
      .failureHandler(new FailureHandler());

    int port = HttpServerContext.getPort();
    this.vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(port, res -> {
        if (res.succeeded()) {
          LOGGER.info("http server running at the port {}", port);
        } else {
          LOGGER.error("http server start failed !", res.cause());
          throw new IllegalStateException(res.cause());
        }
      });

  }

}

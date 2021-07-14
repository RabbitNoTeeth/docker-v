package cn.youyi.dockerv.http;

import cn.youyi.dockerv.http.context.HttpServerContext;
import cn.youyi.dockerv.http.handler.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServerVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

  @Override
  public void start() throws Exception {

    Router router = Router.router(this.vertx);

    // 挂载前置路由处理器
    router
      .route()
      .handler(new CrosHandler())
//      .handler(BodyHandler.create())
      .handler(new ResponseHeaderHandler());

    // 挂载子路由
    HttpServerContext.getRoutes().forEach(route -> {
      route.mount(router);
      LOGGER.info("mounted route [{}] success", route.getClass().getSimpleName());
    });

    // 挂载后置路由处理器
    router
      .route()
      .handler(new ResponseBodyHandler())
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

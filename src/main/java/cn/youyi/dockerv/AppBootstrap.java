package cn.youyi.dockerv;

import io.vertx.core.Vertx;

public class AppBootstrap {

  public static void main(String[] args) {
    Vertx.vertx().createHttpServer().requestHandler(req -> {
      req.response()
        .putHeader("content-type", "text/plain")
        .end("Hello from Vert.x!");
    }).listen(8888, http -> {
      if (http.succeeded()) {
        System.out.println("HTTP server started on port 8888");
      } else {
        System.err.println(http.cause().getMessage());
      }
    });
  }
}

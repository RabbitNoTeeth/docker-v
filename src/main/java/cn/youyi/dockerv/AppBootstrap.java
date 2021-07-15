package cn.youyi.dockerv;

import cn.youyi.dockerv.http.context.HttpServerContext;
import cn.youyi.dockerv.http.route.impl.ContainerRoute;
import cn.youyi.dockerv.http.route.impl.ImageRoute;
import cn.youyi.dockerv.http.route.impl.SessionRoute;
import io.vertx.core.Vertx;

public class AppBootstrap {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    // 添加路由
    HttpServerContext.addRoute(new SessionRoute());
    HttpServerContext.addRoute(new ImageRoute());
    HttpServerContext.addRoute(new ContainerRoute());
    // 部署http服务
    vertx.deployVerticle("cn.youyi.dockerv.http.HttpServerVerticle");
  }
}

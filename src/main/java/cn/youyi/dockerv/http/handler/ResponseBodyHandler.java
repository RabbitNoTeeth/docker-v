package cn.youyi.dockerv.http.handler;

import cn.youyi.dockerv.http.context.RoutingContextHelper;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * 响应后置处理器
 */
public class ResponseBodyHandler implements Handler<RoutingContext> {

  @Override
  public void handle(RoutingContext ctx) {
    Object successData = RoutingContextHelper.getSuccessData(ctx);
    // 如果路由上下文中已经写入了响应数据，那么封装成统一的响应结构返回
    if (successData != null) {
      JsonObject data = new JsonObject()
        .put("success", true)
        .put("message", "request complete")
        .put("data", successData);
      ctx.response().write(data.encode());
    }
    ctx.next();
  }

}

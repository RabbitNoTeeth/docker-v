package cn.youyi.dockerv.http.route;

import io.vertx.ext.web.Router;

public interface MountableRoute {

  void mount(Router router);

}

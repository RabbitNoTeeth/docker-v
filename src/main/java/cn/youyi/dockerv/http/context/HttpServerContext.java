package cn.youyi.dockerv.http.context;

import cn.youyi.dockerv.http.route.MountableRoute;

import java.util.*;

public class HttpServerContext {

  private static int port = 22233;

  private static final List<MountableRoute> MOUNTABLE_ROUTE_SET = new ArrayList<>();

  public static void port(int port_) {
    port = port_;
  }

  public static int getPort() {
    return port;
  }

  public static void addRoute(MountableRoute route) {
    MOUNTABLE_ROUTE_SET.add(route);
  }

  public static List<MountableRoute> getRoutes() {
    return MOUNTABLE_ROUTE_SET;
  }

}

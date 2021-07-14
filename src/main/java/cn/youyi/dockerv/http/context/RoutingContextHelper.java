package cn.youyi.dockerv.http.context;

import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;

public class RoutingContextHelper {

  private static final String RESPONSE_DATA_KEY = "response_data";

  private RoutingContextHelper() {}

  /**
   * write the response data when there is no error
   * @param routingContext routing context
   * @param data           data of writing to the response
   */
  public static void success(RoutingContext routingContext, Object data) {
    routingContext.put(RESPONSE_DATA_KEY, data);
    routingContext.next();
  }

  /**
   * get the response data
   * @param routingContext routing context
   * @return               data of writing to the response
   */
  public static Object getSuccessData(RoutingContext routingContext) {
    return routingContext.get(RESPONSE_DATA_KEY, null);
  }

  /**
   * get the param value
   * @param routingContext routing context
   * @param paramName          name of param
   * @return               value of param
   */
  public static String getRequestParam(RoutingContext routingContext, String paramName) {
    return routingContext.request().getParam(paramName);
  }

  /**
   * get the param map
   * @param routingContext routing context
   * @return               map of param
   */
  public static MultiMap getRequestParamMap(RoutingContext routingContext) {
    return routingContext.request().params();
  }

}

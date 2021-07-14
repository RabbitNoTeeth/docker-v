package cn.youyi.dockerv.http.handler;

import cn.youyi.dockerv.http.context.RoutingContextHelper;
import cn.youyi.dockerv.http.paramvalidation.*;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * validate the request param
 */
public class ParamValidationHandler implements Handler<RoutingContext> {

  private final List<ParamValidation> paramValidationList = new ArrayList<>();

  private ParamValidationHandler() {
  }

  public static ParamValidationHandler create() {
    return new ParamValidationHandler();
  }

  @Override
  public void handle(RoutingContext context) {
    List<String> message = new ArrayList<>();
    for (ParamValidation paramValidation : paramValidationList) {
      String paramName = paramValidation.getParamName();
      String paramValue = RoutingContextHelper.getRequestParam(context, paramName);
      if (paramValidation instanceof NotNullParam) {
        // check whether the paramValue is null
        if (paramValue == null) {
          message.add("param [" + paramName + "] can not be null");
        } else {
          // check whether the paramValue value is valid
          Rule.ValueType valueType = paramValidation.getValueType();
          switch (valueType) {
            case String:
              // there is no need to check String
              break;
            case Int:
              try {
                Integer.parseInt(paramValue);
              } catch (Exception e) {
                message.add("param [" + paramName + "] has an invalid value of '" + paramValue + "'");
              }
              break;
            case Long:
              try {
                Long.parseLong(paramValue);
              } catch (Exception e) {
                message.add("param [" + paramName + "] has an invalid value of '" + paramValue + "'");
              }
              break;
            case Double:
              try {
                Double.parseDouble(paramValue);
              } catch (Exception e) {
                message.add("param [" + paramName + "] has an invalid value of '" + paramValue + "'");
              }
              break;
            case Float:
              try {
                Float.parseFloat(paramValue);
              } catch (Exception e) {
                message.add("param [" + paramName + "] has an invalid value of '" + paramValue + "'");
              }
              break;
            case Array:
              // todo check whether the paramValue value is valid Array
              break;
            case Obj:
              // todo check whether the paramValue value is valid Obj
              break;
            default:
              break;
          }
        }
      } else if (paramValidation instanceof NotBlankParam) {
        if (paramValue == null) {
          message.add("param [" + paramName + "] can not be null");
        } else if (StringUtils.isBlank(paramValue)) {
          message.add("param [" + paramName + "] can not be blank");
        }
      }
    }
    if (message.size() > 0) {
      context.fail(new ParamValidationException(String.join("; ", message)));
    } else {
      context.next();
    }
  }

  /**
   * add param validate item
   *
   * @param paramValidation param validate
   * @return this
   */
  public ParamValidationHandler addParam(ParamValidation paramValidation) {
    paramValidationList.add(paramValidation);
    return this;
  }

}

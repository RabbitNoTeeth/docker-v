package cn.youyi.dockerv.http.paramvalidation;

public class NotNullParam extends ParamValidation{

  public NotNullParam(String paramName, Rule.ValueType valueType) {
    super(paramName, valueType);
  }

}

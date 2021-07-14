package cn.youyi.dockerv.http.paramvalidation;


public class ParamValidation {

  protected final String paramName;
  protected final Rule.ValueType valueType;

  protected ParamValidation(String paramName, Rule.ValueType valueType) {
    this.paramName = paramName;
    this.valueType = valueType;
  }

  public String getParamName() {
    return paramName;
  }

  public Rule.ValueType getValueType() {
    return valueType;
  }

}

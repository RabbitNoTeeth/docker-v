package cn.youyi.dockerv.http.paramvalidation;

public class NotBlankParam extends ParamValidation{

  public NotBlankParam(String paramName) {
    super(paramName, Rule.ValueType.String);
  }

}

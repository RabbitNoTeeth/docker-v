package cn.youyi.dockerv.http.paramvalidation;

public class ParamValidationException extends Exception {

  public ParamValidationException() {
  }

  public ParamValidationException(String message) {
    super(message);
  }

  public ParamValidationException(String message, Throwable t) {
    super(message, t);
  }

}

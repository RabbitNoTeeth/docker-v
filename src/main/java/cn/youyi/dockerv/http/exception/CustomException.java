package cn.youyi.dockerv.http.exception;

public class CustomException extends Exception {

  public CustomException() {
  }

  public CustomException(String message) {
    super(message);
  }

  public CustomException(String message, Throwable t) {
    super(message, t);
  }
}

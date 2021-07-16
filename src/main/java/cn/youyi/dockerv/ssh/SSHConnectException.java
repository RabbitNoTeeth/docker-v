package cn.youyi.dockerv.ssh;

public class SSHConnectException extends Exception {

  public SSHConnectException() {
  }

  public SSHConnectException(String message) {
    super(message);
  }

  public SSHConnectException(String message, Throwable t) {
    super(message, t);
  }
}

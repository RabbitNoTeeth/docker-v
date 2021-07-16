package cn.youyi.dockerv.docker.session;

public class DockerCmdExecException extends Exception {

  public DockerCmdExecException() {
  }

  public DockerCmdExecException(String message) {
    super(message);
  }

  public DockerCmdExecException(Throwable t) {
    super(t);
  }

  public DockerCmdExecException(String message, Throwable t) {
    super(message, t);
  }
}

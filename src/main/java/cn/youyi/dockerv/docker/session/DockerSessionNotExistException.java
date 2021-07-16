package cn.youyi.dockerv.docker.session;

public class DockerSessionNotExistException extends Exception {

  public DockerSessionNotExistException() {
  }

  public DockerSessionNotExistException(String message) {
    super(message);
  }

  public DockerSessionNotExistException(String message, Throwable t) {
    super(message, t);
  }
}

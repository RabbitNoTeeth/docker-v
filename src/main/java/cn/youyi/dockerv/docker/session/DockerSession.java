package cn.youyi.dockerv.docker.session;

public class DockerSession {

  private final String id;

  private final String name;

  private final String host;

  private final DockerCmdExecutor cmdExecutor;

  public DockerSession(String name, String host, DockerCmdExecutor cmdExecutor) {
    this.id = System.nanoTime() + "";
    this.name = name;
    this.host = host;
    this.cmdExecutor = cmdExecutor;
  }

  public void close() {
    this.cmdExecutor.close();
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getHost() {
    return host;
  }

  public DockerCmdExecutor getCmdExecutor() {
    return cmdExecutor;
  }

}

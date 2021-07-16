package cn.youyi.dockerv.docker.session;

public interface DockerCmdExecutor {

  String command(String command) throws DockerCmdExecException;

  void close();

}

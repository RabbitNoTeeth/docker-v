package cn.youyi.dockerv.docker.command.impl;

import cn.youyi.dockerv.docker.command.DockerCommand;
import cn.youyi.dockerv.docker.parser.AbstractDockerOutParser;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DockerBuild implements DockerCommand {

  private final String dockerfileDirPath;
  private final String repository;
  private final String tag;

  private DockerBuild(String dockerfileDirPath, String repository, String tag) {
    this.dockerfileDirPath = dockerfileDirPath;
    this.repository = repository;
    this.tag = tag;
  }

  public static DockerBuild create(String dockerfileDirPath, String repository, String tag) {
    return new DockerBuild(dockerfileDirPath, repository, tag);
  }

  @Override
  public DockerCommand.Command command() {
    return new Command(dockerfileDirPath, repository, tag);
  }

  @Override
  public DockerCommand.Parser parser(String out) {
    return new Parser(out);
  }

  public static class Command implements DockerCommand.Command{
    private final List<String> options = new ArrayList<>();
    private final String dockerfileDirPath;
    private final String repository;
    private final String tag;

    private Command(String dockerfileDirPath, String repository, String tag) {
      this.dockerfileDirPath = dockerfileDirPath;
      this.repository = repository;
      this.tag = tag;
    }

    @Override
    public DockerCommand.Command addOption(String option) {
      options.add(option);
      return this;
    }

    @Override
    public DockerCommand.Command addCommand(String option) {
      return this;
    }

    @Override
    public String getStr() {
      StringBuilder cmdSb = new StringBuilder().append("docker").append(" ").append("build");
      options.forEach(option -> cmdSb.append(" ").append(option));
      cmdSb.append(" ").append("-f").append(" ").append(dockerfileDirPath).append("/Dockerfile");
      cmdSb.append(" ").append("-t").append(" ").append(repository).append(":").append(tag);
      cmdSb.append(" ").append(dockerfileDirPath);
      return cmdSb.toString();
    }
  }

  public static class Parser extends DockerCommand.AbstractParser {

    private Parser(String out) {
      super(out);
    }

    @Override
    public boolean success() {
      return StringUtils.isNotBlank(out) && out.contains("Successfully built");
    }

    @Override
    public List<JsonObject> parse() {
      return parse2JsonObjectList();
    }

  }

}

package cn.youyi.dockerv.docker.command;

import cn.youyi.dockerv.docker.parser.AbstractDockerOutParser;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DockerBuild {

  private DockerBuild() {
  }

  public static class Command {
    private final List<String> options = new ArrayList<>();
    private final String dockerfileDirPath;
    private final String repository;
    private final String tag;

    public Command(String dockerfileDirPath, String repository, String tag) {
      this.dockerfileDirPath = dockerfileDirPath;
      this.repository = repository;
      this.tag = tag;
    }

    public static Command create(String dockerfileDirPath, String repository, String tag) {
      return new Command(dockerfileDirPath, repository, tag);
    }

    public Command addOption(String option) {
      options.add(option);
      return this;
    }

    public String get() {
      StringBuilder cmdSb = new StringBuilder().append("docker").append(" ").append("build");
      options.forEach(option -> cmdSb.append(" ").append(option));
      cmdSb.append(" ").append("-f").append(" ").append(dockerfileDirPath).append("/Dockerfile");
      cmdSb.append(" ").append("-t").append(" ").append(repository).append(":").append(tag);
      cmdSb.append(" ").append(dockerfileDirPath);
      return cmdSb.toString();
    }
  }

  public static class Parser extends AbstractDockerOutParser {

    private Parser(String out) {
      super(out);
    }

    public static Parser create(String out) {
      return new Parser(out);
    }

    @Override
    public boolean success() {
      return StringUtils.isNotBlank(out) && out.contains("Successfully built");
    }

    public List<JsonObject> parse() {
      return parse2JsonObjectList();
    }

  }

}

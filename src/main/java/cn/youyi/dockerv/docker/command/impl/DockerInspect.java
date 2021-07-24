package cn.youyi.dockerv.docker.command.impl;

import cn.youyi.dockerv.docker.command.DockerCommand;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DockerInspect implements DockerCommand{

  private final String dockerObject;

  private DockerInspect(String dockerObject) {
    this.dockerObject = dockerObject;
  }

  public static DockerInspect create(String dockerObject) {
    return new DockerInspect(dockerObject);
  }

  @Override
  public DockerCommand.Command command() {
    return new Command(dockerObject);
  }

  @Override
  public DockerCommand.Parser parser(String out) {
    return new Parser(out);
  }

  public static class Command implements DockerCommand.Command {
    private final List<String> options = new ArrayList<>();
    private final String dockerObject;

    private Command(String dockerObject) {
      this.dockerObject = dockerObject;
    }

    @Override
    public DockerCommand.Command addOption(String option) {
      options.add(option);
      return this;
    }

    @Override
    public DockerCommand.Command addCommand(String command) {
      return this;
    }

    @Override
    public String getStr() {
      StringBuilder cmdSb = new StringBuilder().append("docker").append(" ").append("inspect");
      options.forEach(option -> cmdSb.append(" ").append(option));
      cmdSb.append(" ").append(dockerObject);
      return cmdSb.toString();
    }
  }

  public static class Parser extends AbstractParser {

    private Parser(String out) {
      super(out);
    }

    @Override
    public boolean success() {
      return StringUtils.isNotBlank(out);
    }

    @Override
    public List<JsonObject> parse() {
      return parse2JsonObjectList();
    }

  }

}

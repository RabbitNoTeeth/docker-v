package cn.youyi.dockerv.docker.command.impl;

import cn.youyi.dockerv.docker.command.DockerCommand;
import cn.youyi.dockerv.docker.parser.AbstractDockerOutParser;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DockerStop implements DockerCommand{

  private final String[] containers;

  private DockerStop(String... containers) {
    this.containers = containers;
  }

  public static DockerStop create(String... containers) {
    return new DockerStop(containers);
  }

  @Override
  public DockerCommand.Command command() {
    return new Command(containers);
  }

  @Override
  public DockerCommand.Parser parser(String out) {
    return new Parser(out, containers);
  }

  public static class Command implements DockerCommand.Command {
    private final List<String> options = new ArrayList<>();
    private final String[] containers;

    private Command(String... containers) {
      this.containers = containers;
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
      StringBuilder cmdSb = new StringBuilder().append("docker").append(" ").append("stop");
      options.forEach(option -> cmdSb.append(" ").append(option));
      Arrays.stream(containers).forEach(image -> cmdSb.append(" ").append(image));
      return cmdSb.toString();
    }
  }

  public static class Parser extends DockerCommand.AbstractParser {

    private final String[] containers;

    private Parser(String out, String... containers) {
      super(out);
      this.containers = containers;
    }

    @Override
    public boolean success() {
      return StringUtils.isNotBlank(out) && out.contains(containers[0]);
    }

    @Override
    public List<JsonObject> parse() {
      return parse2JsonObjectList();
    }

  }

}

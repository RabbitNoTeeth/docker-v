package cn.youyi.dockerv.docker.command;

import cn.youyi.dockerv.docker.parser.AbstractDockerOutParser;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DockerStart {

  private DockerStart() {
  }

  public static class Command {
    private final List<String> options = new ArrayList<>();
    private final String[] containers;

    private Command(String... containers) {
      this.containers = containers;
    }

    public static Command create(String... containers) {
      return new Command(containers);
    }

    public Command addOption(String option) {
      options.add(option);
      return this;
    }

    public String get() {
      StringBuilder cmdSb = new StringBuilder().append("docker").append(" ").append("start");
      options.forEach(option -> cmdSb.append(" ").append(option));
      Arrays.stream(containers).forEach(image -> cmdSb.append(" ").append(image));
      return cmdSb.toString();
    }
  }

  public static class Parser extends AbstractDockerOutParser {

    private final String[] containers;

    private Parser(String out, String... containers) {
      super(out);
      this.containers = containers;
    }

    public static Parser create(String out, String... containers) {
      return new Parser(out, containers);
    }

    @Override
    public boolean success() {
      return StringUtils.isNotBlank(out) && out.contains(containers[0]);
    }

    public List<JsonObject> parse() {
      return parse2JsonObjectList();
    }

  }

}

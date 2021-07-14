package cn.youyi.dockerv.docker.command;

import cn.youyi.dockerv.docker.parser.AbstractDockerOutParser;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DockerImages {

  private DockerImages() {
  }

  public static class Command {
    private List<String> options = new ArrayList<>();

    public static Command create() {
      return new Command();
    }

    public Command addOption(String option) {
      options.add(option);
      return this;
    }

    public String get() {
      StringBuilder cmdSb = new StringBuilder().append("docker").append(" ").append("images");
      options.forEach(option -> cmdSb.append(" ").append(option));
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
    protected boolean isValid() {
      return StringUtils.isNotBlank(out) && out.startsWith("REPOSITORY");
    }

    public List<JsonObject> parse() {
      return parse2JsonObjectList();
    }

  }

}

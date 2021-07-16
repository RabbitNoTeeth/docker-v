package cn.youyi.dockerv.docker.command;

import cn.youyi.dockerv.docker.parser.AbstractDockerOutParser;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DockerPull {

  private DockerPull() {
  }

  public static class Command {
    private List<String> options = new ArrayList<>();
    private final String image;

    private Command(String image) {
      this.image = image;
    }

    public static Command create(String image) {
      return new Command(image);
    }

    public Command addOption(String option) {
      options.add(option);
      return this;
    }

    public String get() {
      StringBuilder cmdSb = new StringBuilder().append("docker").append(" ").append("pull");
      options.forEach(option -> cmdSb.append(" ").append(option));
      cmdSb.append(" ").append(image);
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
      return StringUtils.isNotBlank(out) && out.contains("Downloaded newer image for");
    }

    public List<JsonObject> parse() {
      return parse2JsonObjectList();
    }

  }

}

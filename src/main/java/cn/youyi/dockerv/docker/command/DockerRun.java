package cn.youyi.dockerv.docker.command;

import cn.youyi.dockerv.docker.parser.AbstractDockerOutParser;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DockerRun {

  private DockerRun() {
  }

  public static class Command {
    private final List<String> options = new ArrayList<>();
    private final String imageId;

    public Command(String imageId) {
      this.imageId = imageId;
    }

    public static Command create(String imageId) {
      return new Command(imageId);
    }

    public Command addOption(String option) {
      options.add(option);
      return this;
    }

    public String get() {
      StringBuilder cmdSb = new StringBuilder().append("docker").append(" ").append("run");
      options.forEach(option -> cmdSb.append(" ").append(option));
      cmdSb.append(" ").append(imageId);
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
      return StringUtils.isNotBlank(out);
    }

    public List<JsonObject> parse() {
      return parse2JsonObjectList();
    }

  }

}

package cn.youyi.dockerv.docker.command;

import cn.youyi.dockerv.docker.parser.AbstractDockerOutParser;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DockerRmi {

  private DockerRmi() {
  }

  public static class Command {
    private final List<String> options = new ArrayList<>();
    private final String[] images;

    private Command(String... images) {
      this.images = images;
    }

    public static Command create(String... images) {
      return new Command(images);
    }

    public Command addOption(String option) {
      options.add(option);
      return this;
    }

    public String get() {
      StringBuilder cmdSb = new StringBuilder().append("docker").append(" ").append("rmi");
      options.forEach(option -> cmdSb.append(" ").append(option));
      Arrays.stream(images).forEach(image -> cmdSb.append(" ").append(image));
      return cmdSb.toString();
    }
  }

  public static class Parser extends AbstractDockerOutParser {

    private final String[] images;

    private Parser(String out, String... images) {
      super(out);
      this.images = images;
    }

    public static Parser create(String out, String... images) {
      return new Parser(out, images);
    }

    @Override
    public boolean success() {
      return StringUtils.isNotBlank(out) && out.contains(images[0]);
    }

    public List<JsonObject> parse() {
      return parse2JsonObjectList();
    }

  }

}

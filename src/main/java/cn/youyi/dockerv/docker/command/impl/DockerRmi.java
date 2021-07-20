package cn.youyi.dockerv.docker.command.impl;

import cn.youyi.dockerv.docker.command.DockerCommand;
import cn.youyi.dockerv.docker.parser.AbstractDockerOutParser;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DockerRmi implements DockerCommand{

  private final String[] images;

  private DockerRmi(String... images) {
    this.images = images;
  }

  public static DockerRmi create(String... images) {
    return new DockerRmi(images);
  }

  @Override
  public DockerCommand.Command command() {
    return new Command(images);
  }

  @Override
  public DockerCommand.Parser parser(String out) {
    return new Parser(out, images);
  }

  public static class Command implements DockerCommand.Command {
    private final List<String> options = new ArrayList<>();
    private final String[] images;

    private Command(String... images) {
      this.images = images;
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
      StringBuilder cmdSb = new StringBuilder().append("docker").append(" ").append("rmi");
      options.forEach(option -> cmdSb.append(" ").append(option));
      Arrays.stream(images).forEach(image -> cmdSb.append(" ").append(image));
      return cmdSb.toString();
    }
  }

  public static class Parser extends DockerCommand.AbstractParser {

    private final String[] images;

    private Parser(String out, String... images) {
      super(out);
      this.images = images;
    }

    @Override
    public boolean success() {
      return StringUtils.isNotBlank(out) && out.contains(images[0]);
    }

    @Override
    public List<JsonObject> parse() {
      return parse2JsonObjectList();
    }

  }

}

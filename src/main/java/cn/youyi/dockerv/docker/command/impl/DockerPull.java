package cn.youyi.dockerv.docker.command.impl;

import cn.youyi.dockerv.docker.command.DockerCommand;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DockerPull implements DockerCommand{

  private final String image;

  private DockerPull(String image) {
    this.image = image;
  }

  public static DockerPull create(String image) {
    return new DockerPull(image);
  }

  @Override
  public DockerCommand.Command command() {
    return new Command(image);
  }

  @Override
  public DockerCommand.Parser parser(String out) {
    return new Parser(out);
  }

  public static class Command implements DockerCommand.Command {
    private List<String> options = new ArrayList<>();
    private final String image;

    private Command(String image) {
      this.image = image;
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
      StringBuilder cmdSb = new StringBuilder().append("docker").append(" ").append("pull");
      options.forEach(option -> cmdSb.append(" ").append(option));
      cmdSb.append(" ").append(image);
      return cmdSb.toString();
    }
  }

  public static class Parser extends DockerCommand.AbstractParser {

    private Parser(String out) {
      super(out);
    }

    @Override
    public boolean success() {
      return StringUtils.isNotBlank(out) && out.contains("Downloaded newer image for");
    }

    @Override
    public List<JsonObject> parse() {
      return parse2JsonObjectList();
    }

  }

}

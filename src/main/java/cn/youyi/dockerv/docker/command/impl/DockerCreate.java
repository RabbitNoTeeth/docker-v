package cn.youyi.dockerv.docker.command.impl;

import cn.youyi.dockerv.docker.command.DockerCommand;
import cn.youyi.dockerv.docker.parser.AbstractDockerOutParser;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DockerCreate implements DockerCommand{

  private final String imageId;

  private DockerCreate(String imageId) {
    this.imageId = imageId;
  }

  public static DockerCreate create(String imageId) {
    return new DockerCreate(imageId);
  }

  @Override
  public DockerCommand.Command command() {
    return new Command(imageId);
  }

  @Override
  public DockerCommand.Parser parser(String out) {
    return new Parser(out);
  }

  public static class Command implements DockerCommand.Command {
    private final List<String> options = new ArrayList<>();
    private final List<String> commands = new ArrayList<>();
    private final String imageId;

    private Command(String imageId) {
      this.imageId = imageId;
    }

    @Override
    public DockerCommand.Command addOption(String option) {
      options.add(option);
      return this;
    }

    @Override
    public DockerCommand.Command addCommand(String command) {
      commands.add(command);
      return this;
    }

    @Override
    public String getStr() {
      StringBuilder cmdSb = new StringBuilder().append("docker").append(" ").append("create");
      options.forEach(option -> cmdSb.append(" ").append(option));
      cmdSb.append(" ").append(imageId);
      if (commands.size() > 0) {
        String command = String.join(" && ", commands);
        cmdSb.append(" ").append("sh -c '").append(command).append("'");
      }
      return cmdSb.toString();
    }

  }

  public static class Parser extends DockerCommand.AbstractParser {

    private Parser(String out) {
      super(out);
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

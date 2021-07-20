package cn.youyi.dockerv.docker.command.impl;

import cn.youyi.dockerv.docker.command.DockerCommand;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DockerLoad implements DockerCommand{

  private final String tarFilePath;

  private DockerLoad(String tarFilePath) {
    this.tarFilePath = tarFilePath;
  }

  public static DockerLoad create(String tarFilePath) {
    return new DockerLoad(tarFilePath);
  }

  @Override
  public DockerCommand.Command command() {
    return new Command(tarFilePath);
  }

  @Override
  public DockerCommand.Parser parser(String out) {
    return new Parser(out);
  }

  public static class Command implements DockerCommand.Command {
    private List<String> options = new ArrayList<>();
    private final String tarFilePath;

    private Command(String tarFilePath) {
      this.tarFilePath = tarFilePath;
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
      StringBuilder cmdSb = new StringBuilder().append("docker").append(" ").append("load").append(" ").append("-i");
      cmdSb.append(" ").append(tarFilePath);
      options.forEach(option -> cmdSb.append(" ").append(option));
      return cmdSb.toString();
    }
  }

  public static class Parser extends DockerCommand.AbstractParser {

    private Parser(String out) {
      super(out);
    }

    @Override
    public boolean success() {
      return StringUtils.isNotBlank(out) && out.contains("Loaded image");
    }

    @Override
    public List<JsonObject> parse() {
      return parse2JsonObjectList();
    }

  }

}

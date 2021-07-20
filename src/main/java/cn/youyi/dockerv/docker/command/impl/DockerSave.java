package cn.youyi.dockerv.docker.command.impl;

import cn.youyi.dockerv.docker.command.DockerCommand;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DockerSave implements DockerCommand{

  private final String repository;
  private final String tag;

  private DockerSave(String repository, String tag) {
    this.repository = repository;
    this.tag = tag;
  }

  public static DockerSave create(String repository, String tag) {
    return new DockerSave(repository, tag);
  }

  @Override
  public DockerCommand.Command command() {
    return new Command(repository, tag);
  }

  @Override
  public DockerCommand.Parser parser(String out) {
    return new Parser(out);
  }

  public static class Command implements DockerCommand.Command {
    private final List<String> options = new ArrayList<>();
    private final String repository;
    private final String tag;

    private Command(String repository, String tag) {
      this.repository = repository;
      this.tag = tag;
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
      StringBuilder cmdSb = new StringBuilder().append("docker").append(" ").append("save");
      options.forEach(option -> cmdSb.append(" ").append(option));
      cmdSb.append(" ").append(repository).append(":").append(tag);
      return cmdSb.toString();
    }
  }

  public static class Parser extends DockerCommand.AbstractParser {

    private Parser(String out) {
      super(out);
    }

    @Override
    public boolean success() {
      return StringUtils.isBlank(out);
    }

    @Override
    public List<JsonObject> parse() {
      return parse2JsonObjectList();
    }

  }

}

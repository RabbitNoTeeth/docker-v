package cn.youyi.dockerv.docker.command;

import cn.youyi.dockerv.docker.parser.AbstractDockerOutParser;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DockerSave {

  private DockerSave() {
  }

  public static class Command {
    private final List<String> options = new ArrayList<>();
    private final String repository;
    private final String tag;

    private Command(String repository, String tag) {
      this.repository = repository;
      this.tag = tag;
    }

    public static Command create(String repository, String tag) {
      return new Command(repository, tag);
    }

    public Command addOption(String option) {
      options.add(option);
      return this;
    }

    public String get() {
      StringBuilder cmdSb = new StringBuilder().append("docker").append(" ").append("save");
      options.forEach(option -> cmdSb.append(" ").append(option));
      cmdSb.append(" ").append(repository).append(":").append(tag);
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
      return StringUtils.isBlank(out);
    }

    public List<JsonObject> parse() {
      return parse2JsonObjectList();
    }

  }

}

package cn.youyi.dockerv.docker.parser;

import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractDockerOutParser {

  protected final String out;

  protected AbstractDockerOutParser(String out) {
    this.out = out;
  }

  private List<String> parseLine(String line) {
    return Arrays.stream(line.split("[ ]{2,}")).filter(StringUtils::isNotBlank).collect(Collectors.toList());
  }

  protected List<JsonObject> parse2JsonObjectList() {
    if (!isValid()) {
      throw new IllegalArgumentException("out is invalid");
    }
    List<JsonObject> res = new ArrayList<>();
    List<String> lines = Arrays.stream(out.split("\n")).filter(StringUtils::isNotBlank).collect(Collectors.toList());
    if (lines.size() > 0) {
      String titleLine = lines.get(0);
      List<String> titles = parseLine(titleLine);
      for (int i = 1; i < lines.size(); i++) {
        String contentLine = lines.get(i);
        List<String> contents = parseLine(contentLine);
        JsonObject jsonObject = new JsonObject();
        for (int j = 0; j < titles.size(); j++) {
          String title = titles.get(j);
          String content = j < contents.size() ? contents.get(j) : null;
          jsonObject.put(title.replaceAll(" ", "_"), content);
        }
        res.add(jsonObject);
      }
    }
    return res;
  }

  protected abstract boolean isValid();

}

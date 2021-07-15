package cn.youyi.dockerv.docker.parser;

import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractDockerOutParser {

  protected final String out;

  protected AbstractDockerOutParser(String out) {
    this.out = out;
  }

  private LinkedHashMap<Integer, String> parseLine(String line) {
    LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
    Arrays.stream(line.split("[ ]{2,}"))
      .filter(StringUtils::isNotBlank)
      .forEach(s -> map.put(line.indexOf(s), s));
    return map;
  }

  protected List<JsonObject> parse2JsonObjectList() {
    if (!isValid()) {
      throw new IllegalArgumentException("out is invalid");
    }
    List<JsonObject> res = new ArrayList<>();
    List<String> lines = Arrays.stream(out.split("\n")).filter(StringUtils::isNotBlank).collect(Collectors.toList());
    if (lines.size() > 0) {
      String titleLine = lines.get(0);
      LinkedHashMap<Integer, String> titleMap = parseLine(titleLine);
      for (int i = 1; i < lines.size(); i++) {
        String contentLine = lines.get(i);
        LinkedHashMap<Integer, String> contentMap = parseLine(contentLine);
        JsonObject jsonObject = new JsonObject();
        titleMap.forEach((k, v) -> {
          String content = contentMap.getOrDefault(k, null);
          jsonObject.put(v.replaceAll(" ", "_"), content);
        });
        res.add(jsonObject);
      }
    }
    return res;
  }

  protected abstract boolean isValid();

}
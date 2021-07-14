package cn.youyi.dockerv.docker.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DockerSessionContainer {

  private static final Logger LOGGER = LoggerFactory.getLogger(DockerSessionContainer.class);

  private static final Map<Long, DockerSession> SESSION_MAP = new ConcurrentHashMap<>();

  private DockerSessionContainer() {}

  /**
   * add a session to the container
   * @param session session
   */
  public static void addSession(DockerSession session) {
    SESSION_MAP.put(session.getId(), session);
    LOGGER.info("a new session[name:{}, host:{}] established! container size: {}", session.getName(), session.getHost(), SESSION_MAP.size());
  }

  /**
   * get a session by sessionId
   * @param sessionId id of session
   * @return          target session
   */
  public static DockerSession getSession(long sessionId) {
    return SESSION_MAP.get(sessionId);
  }

  /**
   * remove a session by sessionId
   * @param sessionId id of session
   * @return          target session
   */
  public static DockerSession removeSession(long sessionId) {
    DockerSession session = SESSION_MAP.remove(sessionId);
    LOGGER.info("a session[name:{}, host:{}] removed! container size: {}", session.getName(), session.getHost(), SESSION_MAP.size());
    return session;
  }

}

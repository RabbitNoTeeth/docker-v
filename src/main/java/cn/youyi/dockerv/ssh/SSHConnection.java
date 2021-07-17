package cn.youyi.dockerv.ssh;

import cn.youyi.dockerv.docker.session.DockerCmdExecException;
import cn.youyi.dockerv.docker.session.DockerCmdExecutor;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * connection of ssh
 */
public class SSHConnection implements DockerCmdExecutor {

  private static final Logger LOGGER = LoggerFactory.getLogger(SSHConnection.class);

  private final String host;

  private final int port;

  private final String user;

  private final String password;

  private final Session session;

  public SSHConnection(String host, int port, String user, String password) throws SSHConnectException {
    this.host = host;
    this.port = port;
    this.user = user;
    this.password = password;
    // connect ssh
    try {
      JSch jsch = new JSch();
      this.session = jsch.getSession(user, host, port);
      this.session.setConfig("StrictHostKeyChecking", "no");
      this.session.setPassword(password);
      this.session.connect();
    } catch (Exception e) {
      LOGGER.error("ssh {}:{} connect failed", host, port, e);
      throw new SSHConnectException("can not connect to the ssh " + host + ":" + port, e);
    }
  }

  /**
   * close the ssh session
   */
  @Override
  public void close() {
    if (session.isConnected()) {
      session.disconnect();
    }
  }

  /**
   * exec the docker command
   *
   * @param command docker command
   * @return os output
   */
  @Override
  public String command(String command) throws DockerCmdExecException {
    ChannelExec channelExec = null;
    try {
      channelExec = (ChannelExec) session.openChannel("exec");
      InputStream stdStream = channelExec.getInputStream();
      InputStream errStream = channelExec.getErrStream();
      channelExec.setCommand(command);
      channelExec.connect();
      String error = IOUtils.toString(errStream, StandardCharsets.UTF_8);
      if (StringUtils.isNotBlank(error)) {
        throw new DockerCmdExecException(error);
      }
      return IOUtils.toString(stdStream, StandardCharsets.UTF_8);
    } catch (Exception e) {
      if (e instanceof DockerCmdExecException) {
        throw (DockerCmdExecException) e;
      }
      throw new DockerCmdExecException(e);
    } finally {
      if (channelExec != null && !channelExec.isClosed()) {
        channelExec.disconnect();
      }
    }
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }

}

package net;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author tnishida
 */
public class PolicyFileServer implements Runnable {

  public static final String policyFileRequest = "<policy-file-request/>";
  private static final String policyFileFormat = "<cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"%s\"/></cross-domain-policy>\0";
  private int port;
  private String policyFileText;
  private byte[] policyFileData;

  public PolicyFileServer(int port, int... toports) {
    this.port = port;
    this.policyFileText = getPolicyFileText(toports);
    try {
      this.policyFileData = policyFileText.getBytes("UTF-8");
    } catch (UnsupportedEncodingException ex) {
    }
  }

  public static void start(int port, int... toports) {
    PolicyFileServer server = new PolicyFileServer(port, toports);
    Thread thread = new Thread(server);
    thread.start();
  }

  public static String getPolicyFileText(int... toports) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < toports.length; i++) {
      builder.append(toports[i]);
      if (i < toports.length - 1) {
        builder.append(",");
      }
    }

    return String.format(policyFileFormat, builder.toString());
  }

  @Override
  public void run() {
    try {
      System.out.println("Starting policy file server at: " + port);
      ServerSocket ss = new ServerSocket(port);
      while (true) {
        Socket socket = ss.accept();
        PolicyFileSender sender = new PolicyFileSender(socket);
        new Thread(sender).start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  class PolicyFileSender implements Runnable {

    private Socket socket;

    public PolicyFileSender(Socket socket) {
      this.socket = socket;
    }

    @Override
    public void run() {
      try (InputStreamReader reader = new InputStreamReader(socket.getInputStream());
              OutputStream os = socket.getOutputStream()) {
        char[] buffer = new char[32];
        int offset = 0;
        do{
          offset += reader.read(buffer, offset, buffer.length - offset);
        }while(offset < buffer.length && buffer[offset] != '\0') ;
        String s = new String(buffer);

        if (s.startsWith(policyFileRequest)) {
          System.out.println("Policy file requested.");
          os.write(policyFileData);
          os.flush();
          System.out.println("Policy file sent.");
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}

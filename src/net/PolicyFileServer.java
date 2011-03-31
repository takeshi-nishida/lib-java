package net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import net.chat.Constants;
import util.StringUtilities;

/**
 *
 * @author tnishida
 */
public class PolicyFileServer implements Runnable, StringListener {

    public static final String policyFileRequest = "<policy-file-request/>";
    private static final String policyFileFormat = "<cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"%s\"/></cross-domain-policy>";

    private int port;
    private String policyFileText;

    public PolicyFileServer(int port, int ... toports){
        this.port = port;
        this.policyFileText = getPolicyFileText(toports);
    }

    public static void start(int port, int ... toports) {
        PolicyFileServer server = new PolicyFileServer(port, toports);
        Thread thread = new Thread(server);
        thread.start();
    }

    public static String getPolicyFileText(int... toports){
        return String.format(policyFileFormat, StringUtilities.join(",", toports)) + "u0000";
    }

    public void run() {
        try {
            ServerSocket ss = new ServerSocket(port);
            while (true) {
                Socket socket = ss.accept();
                StringSession session = new StringSession(this, socket);
                session.setCharsetName(Constants.defaultCharset);
                session.startup();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void received(StringSession session, String s) {
        if(s.startsWith(policyFileRequest)){
            session.send(policyFileText);
        }
    }

    public void cleaned(StringSession session, boolean abnormally) {
        // do nothing
    }
}

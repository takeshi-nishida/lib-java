/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.chat.server;

import net.chat.Constants;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import net.PolicyFileServer;
import net.StringListener;
import net.StringSession;

/**
 *
 * @author tnishida
 */
public class Server implements Runnable {

    private int port;
    private ServerModel model;
    private StringHandler stringHandler;
    private Map<String, Command> commands;

    public Server(int port, ServerModel model) {
        this.port = port;
        this.model = model;
        stringHandler = new StringHandler();
        initCommands();
    }

    private void initCommands() {
        commands = new HashMap<String, Command>();
        for (Command command : model.getCommands()) {
            commands.put(command.getCommandName(), command);
        }
    }

    public void run() {
        try {
            System.out.println("Staring chat server at port:" + port);
            ServerSocket ss = new ServerSocket(port);

            while (true) {
                Socket socket = ss.accept();
                StringSession session = new StringSession(stringHandler, socket);
                session.setCharsetName(Constants.defaultCharset);
                session.startup();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processCommand(StringSession session, String commandName, String args) {
        Command command = commands.get(commandName);
        if (command != null) {
            command.process(session, args);
        }
    }

    class StringHandler implements StringListener {

        public void received(StringSession session, String s) {
            if (s.startsWith(Constants.commandPrefix)) {
                int i = s.indexOf(Constants.separator);
                if (i > 0) {
                    processCommand(session, s.substring(1, i), s.substring(i + 1, s.length()));
                } else {
                    processCommand(session, s.substring(1), null);
                }
            } else if (model.isLoggedIn(session)) {
                String[] array = s.split(Constants.endHeader, 2);
                if (array.length == 2) {
                    model.processLine(session, array[0], array[1]);
                }
            } else if (s.startsWith(PolicyFileServer.policyFileRequest)) {
                session.send(PolicyFileServer.getPolicyFileText(port));
            } else {
                model.processLogin(session, s.replaceAll(Constants.separator, ""));
            }
        }

        public void cleaned(StringSession session, boolean abnormally) {
            model.cleaned(session);
        }
    }
}

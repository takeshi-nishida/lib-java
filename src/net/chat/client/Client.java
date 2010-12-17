/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.chat.client;

import net.chat.Constants;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import net.StringListener;
import net.StringSession;

/**
 *
 * @author tnishida
 */
public class Client{
  private StringSession session;
  private boolean connected;
  private ClientModel model;
  private Map<String, ProtocolEvent> events;

  public Client(ClientModel model){
    connected = false;
    this.model = model;
    initEvents();
  }

  private void initEvents(){
    events = new HashMap<String, ProtocolEvent>();
    for(ProtocolEvent event : model.getEvents()){
      events.put(event.getEventName(), event);
    }
  }

  public void setEvent(ProtocolEvent event){
  }

  public synchronized void connect(String host, int port) throws UnknownHostException, IOException{
    if(!connected){
      Socket socket = new Socket(host, port);
      session = new StringSession(new ClientStringListener(), socket);
      session.setCharsetName(Constants.defaultCharset);
      session.startup();
      connected = true;
    }
  }

  public synchronized void disconnect(){
    if(connected){
      connected = false;
      if(session != null){
        session.cleanup();
        session = null;
      }
    }
  }

  public synchronized void sendLine(String s){
    if(s == null || s.length() == 0 || s.length() > 1000){
      return;
    }
    if(connected){
      session.sendln(s);
    }
  }

  private void processLine(String s){
    String[] array = s.split(Constants.endHeader, 2);
    if(array.length == 2){
      model.processLine(array[0], array[1]);
    }
  }

  private void processEvent(String eventName, String args){
    ProtocolEvent event = events.get(eventName);
    if(event != null){
      event.process(args);
    }
  }

  class ClientStringListener implements StringListener{
    public void received(StringSession session, String s){
      if(s.startsWith(Constants.eventPrefix)){
        int i = s.indexOf(" ");
        if(i > 0){
          processEvent(s.substring(1, i), s.substring(i + 1, s.length()));
        } else{
          processEvent(s.substring(1), null);
        }
      } else{
        processLine(s);
      }
    }

    public void cleaned(StringSession session, boolean abnormally){
      disconnect();
    }
  }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.chat.client;

/**
 *
 * @author tnishida
 */
public interface ProtocolEvent{
  public String getEventName();
  public void process(String args);
}

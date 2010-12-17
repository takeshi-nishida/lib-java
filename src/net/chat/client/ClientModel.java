/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.chat.client;

import java.util.Set;

/**
 *
 * @author tnishida
 */
public interface ClientModel {
  public Set<ProtocolEvent> getEvents();
  public void processLine(String header, String message);
}

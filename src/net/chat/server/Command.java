/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.chat.server;

import net.StringSession;

/**
 *
 * @author tnishida
 */
public interface Command{
  public String getCommandName();
  public void process(StringSession session, String args);
}

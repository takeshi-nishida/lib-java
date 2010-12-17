/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.chat.server;

import java.util.Set;
import net.StringSession;

/**
 *
 * @author tnishida
 */
public interface ServerModel {
  public Set<Command> getCommands();
  public boolean isLoggedIn(StringSession session);
  public void processLogin(StringSession session, String s);
  public void processLine(StringSession session, String header, String message);
  public void cleaned(StringSession session);
}

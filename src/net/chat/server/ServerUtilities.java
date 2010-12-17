/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.chat.server;

import net.chat.Constants;

/**
 *
 * @author tnishida
 */
public class ServerUtilities {
  public static String buildEventLine(String eventName, Object ... args){
    StringBuilder builder = new StringBuilder(Constants.eventPrefix);
    builder.append(eventName);
    for(Object arg : args){
      builder.append(Constants.separator);
      builder.append(arg);
    }
    builder.append("\n");
    return builder.toString();
  }
  
  public static String buildLogLine(String message, Object ... args){
    StringBuilder builder = new StringBuilder();
    boolean first = true;
    for(Object arg : args){
      if(first){ first = false; }
      else{ builder.append(Constants.separator); }
      builder.append(arg);
    }
    builder.append(Constants.endHeader);
    builder.append(message);
    builder.append("\n");
    return builder.toString();
  }
}

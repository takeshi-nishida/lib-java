/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author tnishida
 */
public class HTTPManager{
  private DocumentBuilder builder;

  public HTTPManager(){
    try{
      builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    } catch(ParserConfigurationException ex){
      ex.printStackTrace();
    }
  }

  public Document getDocumentOf(URL url){
    try{
      URLConnection connection = url.openConnection();
      connection.connect();
      return builder.parse(connection.getInputStream());
    } catch(SAXException ex){
      ex.printStackTrace();
    } catch(IOException ex){
      ex.printStackTrace();
    }
    return null;
  }

  public String getTextOf(URL url){
    try{
      URLConnection connection = url.openConnection();
      connection.connect();
      return readAll(connection.getInputStream());
    } catch(IOException ex){
      ex.printStackTrace();
    }
    return null;
  }

  public Document postToAndGetDocumentOf(URL url, String postString){
    try{
      URLConnection connection = url.openConnection();
      connection.setDoOutput(true);
      write(connection.getOutputStream(), postString);
      connection.connect();
      return builder.parse(connection.getInputStream());
    } catch(SAXException ex){
      ex.printStackTrace();
    } catch(IOException ex){
      ex.printStackTrace();
    }
    return null;
  }

  public String postToAndGetTextOf(URL url, String postString){
    try{
      URLConnection connection = url.openConnection();
      connection.setDoOutput(true);
      write(connection.getOutputStream(), postString);
      connection.connect();
      return readAll(connection.getInputStream());
    } catch(IOException ex){
      ex.printStackTrace();
    }
    return null;
  }
  
  // <editor-fold defaultstate="collapsed" desc="Private methods">
  private String readAll(InputStream is) throws IOException{
    BufferedReader br = new BufferedReader(new InputStreamReader(is));
    String s;
    StringBuilder temp = new StringBuilder();
    while((s = br.readLine()) != null){
      temp.append(s);
    }
    return temp.toString();
  }
  
  private void write(OutputStream os, String text){
    PrintStream ps = new PrintStream(os);
    ps.print(text);
    ps.close();
  }
  // </editor-fold>
}

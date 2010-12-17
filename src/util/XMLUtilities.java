/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.StringWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;

/**
 *
 * @author tnishida
 */
public class XMLUtilities{
  public static void printNode(Node node){
    try{
      StringWriter writer = new StringWriter();

      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.transform(new DOMSource(node), new StreamResult(writer));

      System.out.println(writer.toString());
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }
}

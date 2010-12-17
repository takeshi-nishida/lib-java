/*
 * GeomUtilities.java
 *
 * Created on 2007/01/07, 20:06
 *
 */

package util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
//import ui.Constants;

public class GeomUtilities{
  public static BasicStroke mainStroke = new BasicStroke(2.5f);
  public static BasicStroke borderStroke = new BasicStroke(3.5f);
  
  public static void drawBorderedShape(Graphics2D g2D, Shape s, Color borderColor, Color color){
    Color oldColor = g2D.getColor();
    Stroke oldStroke = g2D.getStroke();
    
    g2D.setColor(borderColor);
    g2D.setStroke(borderStroke);
    g2D.draw(s);
    g2D.setColor(color);
    g2D.setStroke(mainStroke);
    g2D.draw(s);
    
    g2D.setColor(oldColor);
    g2D.setStroke(oldStroke);
  }
  
  public static void drawCircleAtCenter(Graphics2D g2D, Point center, int radius){
    g2D.drawOval(center.x - radius, center.y - radius, radius * 2, radius * 2);
  }
  
  public static void fillCircleAtCenter(Graphics2D g2D, Point center, int radius){
    g2D.fillOval(center.x - radius, center.y - radius, radius * 2, radius * 2);
  }
  
  public static Point dividingPoint(Point a, Point b, double r){
    return new Point((int) (a.x + (b.x - a.x) * r), (int) (a.y + (b.y - a.y) * r));
  }
  
  public static Point dividingPointByLength(Point a, Point b, double l){
    return dividingPoint(a, b, l / a.distance(b));
  }
  
  public static Point dividingPointByLength(Point a, Point b, double l, boolean internal){
    double r = l / a.distance(b);
    if(internal){ r = Math.min(Math.max(0d, r), 1.0d); }
    return dividingPoint(a, b, r);
  }
}
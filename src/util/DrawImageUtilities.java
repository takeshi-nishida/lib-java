/*
 * DragImageUtilities.java
 *
 * Created on 2007/11/27, 18:38
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package util;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 *
 * @author tnishida
 */
public class DrawImageUtilities {

  public static void drawImageCenter(Graphics2D g2D, BufferedImage img, Point p){
    g2D.drawImage(img, p.x - img.getWidth() / 2, p.y - img.getHeight() / 2, img.getWidth(), img.getHeight(), null);
  }
}

package util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import javax.swing.SwingConstants;

public class DrawStringUtilities{
  public static void drawString(Graphics2D g, String s, Dimension d, int halign, int valign){
    drawString(g, s, new Rectangle(0, 0, d.width, d.height), halign, valign);
  }
  
  public static void drawString(Graphics2D g, String s, Rectangle r, int halign, int valign){
    FontMetrics metrics = g.getFontMetrics();

    int x = r.x, y = r.y;
    int sw = metrics.stringWidth(s), sh = metrics.getHeight(), descent = metrics.getMaxDescent();

    switch(halign){
    case SwingConstants.CENTER:
      x += (r.width - sw) / 2; break;
    case SwingConstants.RIGHT:
      x += r.width - sw; break;
    case SwingConstants.LEFT:
    default:
    }

    switch(valign){
    case SwingConstants.CENTER:
      y += r.height / 2 + descent; break;
    case SwingConstants.BOTTOM:
      y += r.height - descent; break;
    case SwingConstants.TOP:
      y += sh - descent;
    default:
    }

    g.drawString(s, x, y);
  }

  public static void drawStringInBalloon(Graphics2D g, String s, Point p, boolean right,
                                         int inset, Color textColor, Color baloonColor){
    FontMetrics metrics = g.getFontMetrics();
    int boxWidth = metrics.stringWidth(s) + inset * 2;
    int boxHeight = metrics.getHeight() + inset * 2;
    int boxLeft = right ? p.x + inset * 2 : p.x - inset * 2 - boxWidth;
    int boxTop = p.y - inset - boxHeight / 2;
    int[] xs = { p.x, right ? boxLeft : boxLeft + boxWidth, right ? boxLeft : boxLeft + boxWidth };
    int[] ys = { p.y, p.y, p.y - inset * 2 };

    Color old = g.getColor();

    g.setColor(baloonColor);
    g.fillPolygon(xs, ys, 3);
    g.fillRoundRect(boxLeft, boxTop, boxWidth, boxHeight, inset, inset);

    g.setColor(textColor);
    g.drawString(s, boxLeft + inset, boxTop + boxHeight - inset - metrics.getMaxDescent());

    g.setColor(old);
  }

  public static void drawBorderedString(Graphics2D g2D, String s, int x, int y, Font font, Color border, Color inside){
    GlyphVector gv = font.createGlyphVector(g2D.getFontRenderContext(), s);
    Shape shape = gv.getOutline(x, y);

    Color old = g2D.getColor();
    g2D.setColor(border);
    g2D.draw(shape);
    g2D.setColor(inside);
    g2D.fill(shape);
    g2D.setColor(old);
  }
}

/*
 * MathUtilities.java
 *
 * Created on 2007/01/05, 21:36
 *
 */

package util;

import java.awt.Dimension;
import java.awt.Point;

public class MathUtilities {
  private static int[] fibBuffer;
  
  static{
    fibBuffer = new int[10];
    fibBuffer[0] = 1;
    fibBuffer[1] = 2;
    for(int i = 2; i < fibBuffer.length; i++){
      fibBuffer[i] = fibBuffer[i - 2] + fibBuffer[i - 1];
    }
  }
  
  public static int fib(int n){
    return (n < fibBuffer.length && n >= 0) ? fibBuffer[n] : fibBuffer[fibBuffer.length - 1];
  }
  
  public static Point randomPointInRect(int x, int y, int w, int h){
    return new Point((int) (x + w * Math.random()), (int) (y + h * Math.random()));
  }
  
  public static Point randomPointInSize(int w, int h){
    return randomPointInRect(0, 0, w, h);
  }
  
  public static Point randomPointInSize(Dimension size){
    return randomPointInRect(0, 0, size.width, size.height);
  }
  
  public static Point randomPointInCircle(int x, int y, int r){
    double d = r * Math.random();
    double angle = 2 * Math.PI * Math.random() ;
    return new Point((int) (x + d * Math.cos(angle)), (int) (y + d * Math.sin(angle)));
  }
}

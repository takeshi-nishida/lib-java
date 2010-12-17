package util;

import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.awt.geom.AffineTransform;
import java.io.*;
import javax.imageio.*;
import javax.imageio.stream.*;
import java.util.Iterator;

public class ImageUtilities{
  public static double getFittingScaleFactor(BufferedImage image, int w, int h){
    int sw  = image.getWidth(), sh = image.getHeight();
    double sx = ((double) w) / sw, sy = ((double) h) / sh;
    return sx < sy ? sx : sy;
  }
  
  public static BufferedImage getResizedImage(BufferedImage source, double s){
    int rw = (int) (source.getWidth() * s), rh = (int) (source.getHeight() * s);
    
    try{
      int resultType = source.getType();
      if(resultType == BufferedImage.TYPE_CUSTOM) resultType = BufferedImage.TYPE_3BYTE_BGR;
      BufferedImage result = new BufferedImage(rw, rh, resultType);
      AffineTransformOp atOp =
          new AffineTransformOp(AffineTransform.getScaleInstance(s, s), AffineTransformOp.TYPE_BILINEAR);
      atOp.filter(source, result);
      
      return result;
    } catch(ImagingOpException e){ e.printStackTrace(); return null; }
  }
  
  public static byte[] getImageBytes(BufferedImage image){
    try{
      Iterator iter = ImageIO.getImageWritersByMIMEType("image/jpeg");
      if(!iter.hasNext()) return null;
      
      ImageWriter writer = (ImageWriter) iter.next();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
      writer.setOutput(ios);
      writer.write(image);
      writer.dispose();
      return baos.toByteArray();
    }catch(IOException ioe){ ioe.printStackTrace(); }
    return null;
  }
  
  public static byte[] getReducedImageBytes(byte[] source, int maxPixels){
    try{
      ImageInputStream iis =
          ImageIO.createImageInputStream(new ByteArrayInputStream(source));
      Iterator iter = ImageIO.getImageReaders(iis);
      
      if(!iter.hasNext()) return null;
      
      ImageReader reader = (ImageReader) iter.next();
      reader.setInput(iis);
      BufferedImage simage = reader.read(0);
      reader.dispose();
      
      if(simage.getWidth() * simage.getHeight() < maxPixels) return source;
      
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageOutputStream ios =  ImageIO.createImageOutputStream(baos);
      ImageWriter writer = ImageIO.getImageWriter(reader);
      writer.setOutput(ios);
      
      double s = getFittingScaleFactor(simage, 640, 480); // for now
      BufferedImage rimage = getResizedImage(simage, s);
      if(rimage == null) return null;
      writer.write(rimage);
      writer.dispose();
      
      return baos.toByteArray();
    } catch(IOException ioe){ ioe.printStackTrace(); }
    return null;
  }
  
  public static BufferedImage loadImageFromResource(String name){
    try{ return ImageIO.read(ImageUtilities.class.getResourceAsStream("/" + name)); }
    catch(IOException e){ e.printStackTrace(); }
    return null;
  }
  
  public static TexturePaint getTexturePaintFromResource(String name){
    try{
      BufferedImage image = loadImageFromResource(name);
      return new TexturePaint(image, new Rectangle(0, 0, image.getWidth(), image.getHeight()));
    }catch(Exception e){ e.printStackTrace(); }
    return null;
  }
}
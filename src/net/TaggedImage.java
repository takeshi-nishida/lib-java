package net;

import static util.ImageUtilities.getReducedImageBytes;

import java.io.*;

public abstract class TaggedImage implements TaggedData{
  protected final int maxPixels = 800 * 600;
  protected boolean reduce;

  public TaggedImage(boolean reduce){ this.reduce = reduce; }
  
  public abstract String getTagString();
  public abstract byte[] getData();
  public abstract void flush(File directory);

  public static TaggedData createTaggedImage(File f, boolean reduce){
    return new TaggedImageFile(f, reduce);
  }
  public static TaggedData createTaggedImage(String s, byte[] bs, boolean reduce){
    return new TaggedImageBytes(s, bs, reduce);
  }
}

class TaggedImageFile extends TaggedImage{
  private File f;

  public TaggedImageFile(File f, boolean reduce){
    super(reduce);
    this.f = f;
  }

  public String getTagString(){ return f.getName(); }

  public byte[] getData(){
    try{
      DataInputStream is = new DataInputStream(new FileInputStream(f));
      byte[] buffer = new byte[(int) f.length()];
      is.readFully(buffer);
      if(reduce) return getReducedImageBytes(buffer, maxPixels);
      else return buffer;
    }catch(IOException ioe){ ioe.printStackTrace(); }
    return null;
  }

  public void flush(File directory){ return; }
}

class TaggedImageBytes extends TaggedImage{
  private String name;
  private byte[] data, reduced;

  public TaggedImageBytes(String s, byte[] bs, boolean reduce){
    super(reduce);
    name = s;
    data = bs;
    reduced = null;
  }

  public String getTagString(){ return name; }
  public byte[] getData(){
    if(reduce){
      if(reduced == null) reduced = getReducedImageBytes(data, maxPixels);
      return reduced;
    }
    return data;
  }

  public void flush(File directory){
    try{
      File f = new File(directory, name);

      BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
      byte[] d = getData();
      bos.write(d, 0, d.length);
      bos.close();
    }catch(IOException ioe){ ioe.printStackTrace(); }
  }
}

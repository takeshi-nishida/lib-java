package net;

public interface TaggedData{
  public String getTagString();
  public byte[] getData();
  public void flush(java.io.File directory);
}

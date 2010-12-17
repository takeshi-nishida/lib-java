package net;

public interface StringListener{
  public void received(StringSession session, String s);
  public void cleaned(StringSession session, boolean abnormally);
}

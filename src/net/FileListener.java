package net;

public interface FileListener{
  public void receivedFile(FileSession session, String head, byte[] body);
  public void receivedCommand(FileSession session, String command);
  public void cleaned(FileSession session, boolean abnormally);
}

package net;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class FileSession extends Thread{
  private static final int limit = 1024 * 1024;
  private static final int bufferSize = 1024 * 16;

  private FileListener listener;
  private Socket socket;
  private FileSendThread sndThread;
  private boolean cleaned;

  public FileSession(FileListener listener, Socket socket){
    this.listener = listener;
    this.socket = socket;
    sndThread = new FileSendThread();
    cleaned = false;
  }

  public void startup(){
    start();
    sndThread.start();
  }
  
  public InetAddress getInetAddress(){
    return socket.getInetAddress();
  }

  public void cleanup(){ cleanup(true); }

  public synchronized void cleanup(boolean normally){
    if(cleaned) return;
    System.out.println("Cleaning up file session.");

    sndThread.quit();
    sndThread = null;
    try{ socket.close(); }
    catch(Exception e){ e.printStackTrace(); }
    socket = null;

    cleaned = true;
    listener.cleaned(this, !normally);
    System.out.println("Cleaned up file session.");
  }

  /* Receive thread implementation */
  public void run(){
    DataInputStream dis = null;
    try{
      dis = new DataInputStream(socket.getInputStream());
      while(true){
        String command = dis.readUTF();
        if(command.startsWith("/")){
          if(command.length() > 1) listener.receivedCommand(this, command.substring(1));
        }
        else{
          int size = dis.readInt();
          if(size > limit){
            cleanup(false);
            break;
          }
          byte[] buffer = new byte[size];
          dis.readFully(buffer, 0, size);
          listener.receivedFile(this, command, buffer);
        }
      }
    }
    catch(SocketException e){ e.printStackTrace(); }
    catch(EOFException e){ e.printStackTrace(); }
    catch(IOException ioe){ ioe.printStackTrace(); }
    finally{ cleanup(); }
    
    if(dis != null){
      try{ dis.close(); } catch(IOException ioe){ ioe.printStackTrace(); }
      dis = null;
    }
  }

  public void append(TaggedData d){ sndThread.append(d); }

  public class FileSendThread extends Thread{
    private LinkedList<TaggedData> queue;
    private DataOutputStream dos;
    private Object sendLock;
    private boolean quit;

    public FileSendThread(){
      queue = new LinkedList<TaggedData>();
      sendLock = new Object();
      quit = false;
    }

    public void quit(){
      synchronized(sendLock){
        quit = true;
        sendLock.notify();
      }
    }

    public void append(TaggedData data){
      synchronized(sendLock){
        queue.addFirst(data);
        sendLock.notify();
      }
    }

    private void send(TaggedData data){
      String name = data.getTagString();
      if(name.startsWith("/")){
        try{
          dos.writeUTF(name);
          dos.flush();
        }catch(Exception e){ e.printStackTrace(); }
        return;
      }

      byte[] buffer = data.getData();
      if(buffer == null) return;
      int size = buffer.length;

      // for now
      if(size > limit) return;
      System.out.println("FileSendThread.send: " + data.getTagString() + " " + size);

      try{
        dos.writeUTF(name);
        dos.writeInt(size);
        dos.write(buffer, 0, size);
        dos.flush();
      }catch(Exception e){ e.printStackTrace(); }
    }

    /* Send thread implementation */
    public void run(){
      TaggedData d;
      try{
        dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        while(true){
          synchronized(sendLock){
            if(queue.size() <= 0) sendLock.wait();
            try{ d = queue.removeLast(); }
            catch(NoSuchElementException e){ d = null; }
          }
          if(quit){
            cleanup();
            break;
          }
          if(d != null) send(d);
        }
      }
      catch(InterruptedException ie){ ie.printStackTrace(); cleanup(false); } // for now
      catch(IOException ioe){ ioe.printStackTrace(); cleanup(false); }
      finally{ cleanup(false); }
      
      if(dos != null){
        try{ dos.close(); } catch(IOException ioe){ ioe.printStackTrace(); }
        dos = null;
      }
    }
  }
}

package net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class StringSession extends Thread {

    private StringListener listener;
    private Socket socket;
    private SendThread sndThread;
    private boolean cleaned;
    private String charsetName;

    public StringSession(StringListener listener, Socket socket) {
        this.listener = listener;
        this.socket = socket;
        sndThread = new SendThread();
        cleaned = false;
        charsetName = null;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

    public InetAddress getInetAddress() {
        return socket.getInetAddress();
    }

    @Override
    public String toString() {
        InetAddress address = socket.getInetAddress();
        return address == null ? "unconnected" : address.getHostAddress();
    }

    public void startup() {
        start();
        sndThread.start();
    }

    public void cleanup() {
        cleanup(true);
    }

    public synchronized void cleanup(boolean normally) {
        if (cleaned) {
            return;
        }
        System.out.println("Cleaning up connection.");

        sndThread.quit();
        sndThread = null;
        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        socket = null;

        cleaned = true;

        listener.cleaned(this, !normally);
        System.out.println("Cleaned up connection.");
    }

    /* Receive thread implementation */
    @Override
    public void run() {
        BufferedReader br = null;
        if (charsetName == null) {
            System.out.println("Warning: charset not specified.");
        }

        try {
            InputStream is = socket.getInputStream();
            InputStreamReader isr = charsetName == null
                    ? new InputStreamReader(is) : new InputStreamReader(is, charsetName);
            br = new BufferedReader(isr);
            System.out.println("Start receive thread.");

            while (true) {
                String s = br.readLine();
                System.out.println(">StringSession.run:" + s);
                if (s == null) {
                    break;
                }
                if (s.length() <= 0) {
                    continue;
                }

                listener.received(this, s);
            }
        } catch (SocketException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            cleanup(false);
        }

        if (br != null) {
            try {
                br.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            br = null;
        }
    }

    public void send(String s) {
        sndThread.append(s);
    }

    public void sendln(String s) {
        sndThread.append(s + "\n");
    }

    class SendThread extends Thread {

        private StringBuffer sendBuf;
        private BufferedWriter bw;
        private final Object sendLock;
        private boolean isEmpty;
        private boolean quit;

        public SendThread() {
            sendBuf = new StringBuffer();
            sendLock = new Object();
            isEmpty = true;
            quit = false;
        }

        public void quit() {
            synchronized (sendLock) {
                quit = true;
                sendLock.notify();
            }
        }

        public void append(String s) { // "¥r¥n" を追加した文字列を引数とすること
            synchronized (sendLock) {
                sendBuf.append(s);
                if (isEmpty) {
                    sendLock.notify();
                }
                isEmpty = false;
            }
        }

        /* Send thread implemantation */
        @Override
        public void run() {
            String s;
            if (charsetName == null) {
                System.out.println("Warning: charset not specified.");
            }

            try {
                OutputStream os = socket.getOutputStream();
                OutputStreamWriter osw = charsetName == null
                        ? new OutputStreamWriter(os) : new OutputStreamWriter(os, charsetName);
                bw = new BufferedWriter(osw);

                while (true) {
                    synchronized (sendLock) {
                        if (isEmpty) {
                            sendLock.wait();
                        }
                        isEmpty = false;
                    }
                    if (quit) {
                        cleanup();
                        break;
                    }
                    synchronized (sendLock) {
                        s = sendBuf.toString();
                        sendBuf = new StringBuffer();
                        isEmpty = true;
                    }
                    System.out.print(">StringSession.SendThread.run:" + s);
                    bw.write(s);
                    bw.flush(); // for now
                }
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                cleanup(false);
            }

            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                bw = null;
            }
        }
    }
}

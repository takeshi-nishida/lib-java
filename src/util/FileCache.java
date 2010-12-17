/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 *
 * @author tnishida
 */
public class FileCache {

    private File dir;
    private final Map<String, byte[]> nameToBytes;

    public FileCache(File dir) {
        this.dir = dir;
//        nameToBytes = new WeakHashMap<String, byte[]>();
        nameToBytes = new CacheMap<String, byte[]>(8);
    }

    public void putBytes(String name, byte[] data) {
        try {
            File f = new File(dir, name);
            FileOutputStream os = new FileOutputStream(f);
            os.write(data, 0, data.length);
            os.flush();
            os.close();
            synchronized (nameToBytes) {
                nameToBytes.put(name, data);
            }
        } catch (IOException ex) {
        }
    }

    public void putStream(String name, InputStream is) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buf = new byte[256];
            int len = 0;
            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
            }
            os.flush();
            os.close();
            putBytes(name, os.toByteArray());
        } catch (IOException ex) {
        }
    }

    public byte[] getBytes(String name) {
        synchronized (nameToBytes) {
            byte[] result = nameToBytes.get(name);
            if (result == null) {
                File f = new File(dir, name);
                try {
                    FileInputStream is = new FileInputStream(f);
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    byte[] buf = new byte[256];
                    int len = 0;
                    while ((len = is.read(buf)) > 0) {
                        os.write(buf, 0, len);
                    }
                    os.flush();
                    os.close();
                    result = os.toByteArray();
                    putBytes(name, result);
                } catch (IOException ex) {
                }
            }
            return result;
        }
    }

    class CacheMap<K, V> extends LinkedHashMap<K, V>{
        private int maxSize;

        public CacheMap(int maxSize){
            this.maxSize = maxSize;
        }
        protected boolean removeEldestEntry(){
            return size() > maxSize;
        }
    }
}
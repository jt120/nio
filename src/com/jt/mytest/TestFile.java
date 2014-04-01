package com.jt.mytest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.junit.Test;

/**
 * @author Liu Ze
 * 
 *         Create Date Apr 1, 2014 6:02:42 PM
 */

public class TestFile {

    @Test
    public void testRead1() {
        String path = "d:/test/1.txt";
        FileInputStream fis = null;
        FileChannel fc = null;
        try {
            fis = new FileInputStream(new File(path));
            fc = fis.getChannel();
            ByteBuffer bb = ByteBuffer.allocate((int) fc.size());
            fc.read(bb);
            bb.rewind();
            while (bb.hasRemaining()) {
                System.out.print((char) bb.get());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Test
    public void testRead2() {
        String path = "d:/test/1.txt";
        FileInputStream fis = null;
        FileChannel fc = null;
        try {
            fis = new FileInputStream(new File(path));
            fc = fis.getChannel();
            ByteBuffer bb = ByteBuffer.allocate(1024);
            while((fc.read(bb))!=-1) {
                bb.flip();
                while (bb.hasRemaining()) {
                    System.out.print((char) bb.get());
                }
                bb.clear();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Test
    public void testWrite() {
        String path = "d:/test/1.txt";
        FileOutputStream fos = null;
        FileChannel fc = null;
        try {
            fos = new FileOutputStream(new File(path));
            fc = fos.getChannel();
            String words = "hello today\nhello yestoday\n";
            ByteBuffer bb = ByteBuffer.allocate(1024);
            for(int i=0;i<words.length();i++) {
                bb.put((byte) words.charAt(i));
            }
            bb.flip();
            fc.write(bb);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

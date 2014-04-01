package com.jt.mytest;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import org.junit.Test;

/**
 * @author Liu Ze
 *
 * Create Date Apr 1, 2014 8:04:06 PM
 */

public class TestChar {
    
    @Test
    public void test01() throws Exception {
        FileInputStream fis = null;
        FileChannel fc = null;
        try {
            Charset cs = Charset.forName("utf-8");
            System.out.println(cs);
            CharsetDecoder decoder = cs.newDecoder();
            CharsetEncoder encoder = cs.newEncoder();
            
            fis = new FileInputStream(new File("d:/test/1.txt"));
            fc = fis.getChannel();
            ByteBuffer in = ByteBuffer.allocate((int) fc.size());
            
            fc.read(in);
            //把pos置为0，才能decode
            in.flip();
            CharBuffer cbuffer = decoder.decode(in);
            while(cbuffer.hasRemaining()) {
                System.out.print(cbuffer.get());
            }
            cbuffer.flip();
            ByteBuffer bb = encoder.encode(cbuffer);
            while(bb.hasRemaining()) {
                System.out.print((char)bb.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fis.close();
            fc.close();
        }
    }

}

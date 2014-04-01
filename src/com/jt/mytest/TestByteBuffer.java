package com.jt.mytest;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author Liu Ze
 *
 * Create Date Apr 1, 2014 10:44:25 AM
 */

public class TestByteBuffer {
    
    public static void main(String[] args) {
        ByteBuffer buff = ByteBuffer.allocate(100);
        int n = 10;
        buff.put((byte) n);
        buff.put((byte) 'H');
        short a = buff.order(ByteOrder.BIG_ENDIAN).getShort();
        System.out.println(a);
        buff.clear();
        System.out.println(buff.get());
        int n2 = 10;
        System.out.println((byte)n2);
        
        System.out.println(buff.isDirect());
        System.out.println(buff.isReadOnly());
        
        System.out.println(buff.hasArray());
        System.out.println();
    }

}

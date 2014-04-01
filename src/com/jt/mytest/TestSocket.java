package com.jt.mytest;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;

import org.junit.Test;

/**
 * @author Liu Ze
 *
 * Create Date Apr 1, 2014 7:49:42 PM
 */

public class TestSocket {
    
    @Test
    public void test01() throws Exception {
        InetSocketAddress add = new InetSocketAddress(InetAddress.getByName("127.0.0.1"),22);
        System.out.println(add.getHostName());
    }
    
    @Test
    public void test02() throws Exception {
        URL url = new URL("http://127.0.0.1:80");
        URLConnection conn = url.openConnection();
        System.out.println(conn);
        conn = null;
    }

}

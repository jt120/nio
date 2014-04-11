package com.jt.mytest.one;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class SelectorTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			Selector selector = Selector.open();
			ServerSocketChannel channel = ServerSocketChannel.open();
			channel.configureBlocking(false);
			SelectionKey key = channel.register(selector, SelectionKey.OP_ACCEPT);
			while(true) {
				
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}

}

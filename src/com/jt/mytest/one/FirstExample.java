package com.jt.mytest.one;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FirstExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RandomAccessFile file = null;
		
		try {
			file = new RandomAccessFile(new File("d:/test/hello.txt"), "rw");
			FileChannel fChannel = file.getChannel();
			ByteBuffer buff = ByteBuffer.allocate(64);
			int r = fChannel.read(buff);
			while(r != -1) {
				buff.flip();
				System.out.println(buff.toString());
				while(buff.hasRemaining()) {
					System.out.print((char) buff.get());
				}
				buff.clear();
				r = fChannel.read(buff);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(file!=null)
				file.close();
				file = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}

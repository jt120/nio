package com.jt.mytest.one;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class CopyChannel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			RandomAccessFile rFile1 = new RandomAccessFile(new File("d:/test/hello.txt"), "r");
			RandomAccessFile rFile2 = new RandomAccessFile(new File("d:/test/you.txt"), "rw");
			FileChannel fc1 = rFile1.getChannel();
			FileChannel fc2 = rFile2.getChannel();
			
			long size = fc1.size();
			fc2.transferFrom(fc1, 0, size);
			
			rFile1.close();
			rFile2.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}

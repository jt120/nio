
package com.ronsoft.books.nio.buffers;

import java.nio.CharBuffer;

public class BufferWrap
{
	public static void main (String [] argv)
		throws Exception
	{
		CharBuffer cb = CharBuffer.allocate (100);

		cb.put ("This is a test String");
		/**
		 * 与slice区别
		 * slice是复制
		 * flip是写入前的操作，把limit设置为pos，通常与compact一起用
		 */
		cb.flip();

		System.out.println ("hasArray() = " + cb.hasArray());

		char [] carray = cb.array();

		System.out.print ("array=");

		for (int i = 0; i < carray.length; i++) {
			System.out.print (carray [i]);
		}

		System.out.println ("");
		System.out.flush();
	}
}

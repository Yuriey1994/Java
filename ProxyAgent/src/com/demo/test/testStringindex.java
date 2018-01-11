package com.demo.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class testStringindex {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			SocketChannel sc = SocketChannel.open();
			sc.connect(new InetSocketAddress("www.google.com", 443));
			System.out.println(sc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//new InetSocketAddress("sp1.baidu.com", 443).getAddress());
	}

}

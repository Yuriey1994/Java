package com.yuriey.proxy;

import java.nio.ByteBuffer;

public class StartProxyAgent {
	private static boolean flag =false;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Listener.getInstance().start();
		RequestParser.getInstance().start();

		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.err.println(">>>Sender�߳�����" + M.senderMap.size());
					System.err.println(">>>Request����" + M.reqMap.size());
				}
			}
		}.start();
	}
}

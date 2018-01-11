package com.demo.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class Test {
	private void a() {
		synchronized(this) {
			
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Map<String,Integer> map = new HashMap<String,Integer>();
		map.put("A", 1);
		map.put("B", 2);
		Set keySet = map.keySet();
		Iterator it = keySet.iterator();
		String[] s = "abcde".split(System.getProperty("line.separator") + System.getProperty("line.separator"));
		String s1 = "123456\r\n\r\n";
		int x = s1.indexOf("\r\n\r\n");
		System.out.print(s1.substring(x+4).length());
		//it.next();
		//it.remove();
		//map.remove("A");
		//System.out.println(keySet.size());
		new Thread() {
			public void run() {
				System.out.println("1 run");
				synchronized(map) {
					System.out.println("1 synchronized");
					while(true) {
						System.out.println("1 map size" + map.size());
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
			}
		}.start();
		new Thread() {
			public void run() {
				System.out.println("2 run");
				//synchronized(map) {
					System.out.println("2 synchronized");
					while(true) {
						System.out.println("2 map size" + map.size());
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				//}
				
			}
		}.start();
	}

}

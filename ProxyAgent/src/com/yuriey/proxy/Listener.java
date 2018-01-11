package com.yuriey.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Listener extends Thread {
	private static Listener proxy;
	private Selector selector;
	private ServerSocketChannel ssc;
	private final String TAG = "Proxy";
	private Listener() {
		
	}
	public static synchronized Listener getInstance() {
		if(proxy == null) {
			proxy = new Listener();
		}
		return proxy;
	}
	private void listening() {
		try {
			Debug.log(TAG, "listen on " + ssc.getLocalAddress());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Set selectedKeys;
		while(true) {
			int num = 0;
			try {
				num = selector.select();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(num == 0) {
			    Debug.log(TAG, "*******************selected error!*********************");
			    continue;
			}
	        selectedKeys = selector.selectedKeys();
	        Iterator iterator = selectedKeys.iterator();
			while (iterator.hasNext()) {
	            SelectionKey key = (SelectionKey) iterator.next();
	            try {
		            if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
		            	ServerSocketChannel _ssc = (ServerSocketChannel) key.channel();
		            	try {
							SocketChannel _sc = _ssc.accept();
							_sc.configureBlocking(false);
							SelectionKey _key = _sc.register(selector, SelectionKey.OP_READ);
							Debug.log(TAG, "accept :" + _sc);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
		            } else if((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
		            	//System.out.println("ReceiveAgent read :" + key.channel());
		            	ByteBuffer bb = ByteBuffer.allocate(4096);
		            	SocketChannel _sc = (SocketChannel) key.channel();
		            	int len = -1;
		            	try {
							len = _sc.read(bb);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            	if(len > 0) {
		            		bb.flip();
							byte[] b = new byte[bb.limit() - bb.position()];
							bb.get(b,bb.position(),bb.limit());
							synchronized (M.reqMap) {
								Request _req = M.reqMap.get(key);
								if(_req != null) {
									if(_req.parsed) {
										if(_req.headersMap.get("Protocol").indexOf("CONNECT ") == 0) {
											synchronized (M.senderMap) {
												Sender sender = M.senderMap.get(key);
												if(sender != null) {
													sender.send(bb);
												}
											}	
										} else {
											//满足这个条件说明解析后的request还未发送
											if(!_req.getContent().equals(new String(b))) {
												_req = new Request(key);
												_req.addContent(b);
												M.reqMap.put(key, _req);
											}
//											_req = new Request(key);
//											_req.addContent(b);
//											M.reqMap.put(key, _req);
										}
									}
									_req.addContent(b);
								} else {
									_req = new Request(key);
									_req.addContent(b);
									M.reqMap.put(key, _req);
									
								}
							}
							Debug.log(TAG, "Proxy read from channel:" + _sc);
							//Debug.log(TAG, "Proxy read content: " + new String(b));
		            	} else {
		            		Debug.log(TAG, "Proxy closed a socket :" + _sc);
		            		synchronized (M.senderMap) {
		            			if(M.senderMap.containsKey(key)) {
		            				if(!M.senderMap.get(key).isStop())M.senderMap.get(key).stopRun();
				            		M.senderMap.remove(key);
		            			}
							}
		            		synchronized (M.reqMap) {
		            			if(M.reqMap.containsKey(key)) {
				            		M.senderMap.remove(key);
		            			}
							}
		            		//Client.getInstance().map.get(key).stopRun();
		            		//Client.getInstance().map.remove(key);
		            		key.cancel();
		            		try {
								_sc.socket().close();
								_sc.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		            	}
		            	
		            } else if((key.readyOps() & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE) {
		            	Debug.log(TAG, "OP_WRITE");
		            	key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
		            }
		            iterator.remove();
	            } catch(Exception e) {
	            	e.printStackTrace();
	            	synchronized (M.senderMap) {
            			if(M.senderMap.containsKey(key)) {
            				if(!M.senderMap.get(key).isStop())M.senderMap.get(key).stopRun();
		            		M.senderMap.remove(key);
            			}
					}
            		synchronized (M.reqMap) {
            			if(M.reqMap.containsKey(key)) {
		            		M.senderMap.remove(key);
            			}
					}
            		key.cancel();
            		try {
            			((SocketChannel)key.channel()).socket().close();
            			((SocketChannel)key.channel()).close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	            }
			}
			selectedKeys.clear();
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			selector = Selector.open();
			ssc = ServerSocketChannel.open();
	        ssc.configureBlocking(false);
	        //ServerSocket ss = ssc.socket();
	        InetSocketAddress address = new InetSocketAddress(M.port);
	        ssc.bind(address);
	        //ss.bind(address);
	        SelectionKey key = ssc.register(selector, SelectionKey.OP_ACCEPT);
	        listening();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	public void destroy() {
		proxy = null;
	}
}

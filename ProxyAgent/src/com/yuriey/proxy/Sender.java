package com.yuriey.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class Sender extends Thread {
	private Request req;
	private SelectionKey mKey;
	private boolean initStat = false;
	private Selector selector;
	private boolean stopFlag = false;
	private List<ByteBuffer> bbList = new ArrayList<ByteBuffer>();
	private SocketChannel sc;
	private final String TAG = "Sender";
	public Sender(Request req) {
		this.req = req;
	}
	public boolean isStop() {
		return stopFlag;
	}
	public void stopRun() {
		Debug.log(TAG, "stopping...");
		stopFlag = true;	
		try {
			if(selector != null) {
				selector.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(mKey != null && mKey.isValid()) {
			mKey.cancel();
		}
		if(req.getMatchKey().isValid()) {
			req.getMatchKey().cancel();
		}
		try {
			if(sc != null) {
				sc.socket().close();
				sc.close();
			}
			req.getMatchKey().cancel();
			((SocketChannel) req.getMatchKey().channel()).socket().close();
			req.getMatchKey().channel().close();
			synchronized (M.reqMap) {
				if(M.reqMap.containsKey(req.getMatchKey())) {
					M.reqMap.remove(req.getMatchKey());
				}
			}
			synchronized (M.senderMap) {
    			if(M.senderMap.containsKey(req.getMatchKey())) {
            		M.senderMap.remove(req.getMatchKey());
    			}
			}
			bbList.clear();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void send(ByteBuffer bb) {
		if(stopFlag) {
			return;
		}
		bb.flip();
		try {
			sc.write(bb);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class DataHandler extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(!stopFlag) {
				while(bbList.isEmpty() || !initStat) {
					if(stopFlag) {
						return;
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				synchronized (bbList) {
					ByteBuffer bb = bbList.get(0);
					bbList.remove(0);
					
//					mKey.attach(bb);
//					mKey.interestOps(mKey.interestOps() | SelectionKey.OP_WRITE);

					try {
						bb.flip();
//						byte[] b = new byte[bb.limit() - bb.position()];
//						bb.get(b,bb.position(),bb.limit());
//						System.err.println("SendAgent write content: length " + b.length + new String(b));
//						bb.flip();
						while(((SocketChannel) mKey.channel()).write(bb) == 0) {
							try {
								Thread.sleep(5);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						stopRun();
					}
				}
			}
		}
		
	}
	
	private void listening() {
		Set selectedKeys;
		while(!stopFlag) {
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
	            SelectionKey _key = (SelectionKey) iterator.next();
	            SocketChannel _sc = (SocketChannel) _key.channel();
	            if((_key.readyOps() & SelectionKey.OP_CONNECT) == SelectionKey.OP_CONNECT) {
	            	try {
						while(!_sc.finishConnect());
						initStat= true;
		            	Debug.log(TAG, "a socket conneted..matched matchKey channel:" + req.getMatchKey().channel());
		            	String protocol = req.headersMap.get("Protocol");
		            	if(protocol.indexOf("CONNECT ") == 0) {
		            		Debug.log(TAG, "CONNECT request");
		            		String wStr = protocol.substring(protocol.lastIndexOf(" ")) + " 200 Connection established" + System.getProperty("line.separator") + "Proxy-agent: YProxy" + System.getProperty("line.separator") + System.getProperty("line.separator");
		            		ByteBuffer bb = ByteBuffer.allocate(wStr.length());
		            		bb.put(wStr.getBytes());
		            		bb.flip();
		            		try {
								while(((SocketChannel) req.getMatchKey().channel()).write(bb) == 0) {
									try {
										Thread.sleep(5);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								stopRun();
							}
		            	} else {
		            		ByteBuffer bb = ByteBuffer.allocate(req.getParsedContent().length());
		            		bb.put(req.getParsedContent().getBytes());
		            		bb.flip();
		            		try {
								while(_sc.write(bb) == 0) {
									try {
										Thread.sleep(5);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								synchronized (M.reqMap) {
									if(M.reqMap.containsKey(req.getMatchKey())) {
										M.reqMap.remove(req.getMatchKey());
									}
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								stopRun();
							}
		            	}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						stopRun();
					}
	            	_key.interestOps((_key.interestOps() | SelectionKey.OP_READ) & ~SelectionKey.OP_CONNECT);
	            } else if((_key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
	            	ByteBuffer bb = ByteBuffer.allocate(4096);
	            	int len = -1;
	            	try {
						len = _sc.read(bb);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            	if(len > 0) {
	            		//bb.flip();
            			try {
            				bb.flip();
    						//byte[] b = new byte[bb.limit() - bb.position()];
    						//bb.get(b,bb.position(),bb.limit());
    						//System.err.println("SendAgent read content: length " + b.length + new String(b));
            				
            				//bb.flip();
							while(((SocketChannel) req.getMatchKey().channel()).write(bb) == 0) {
								try {
									Thread.sleep(5);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							stopRun();
						}
	            	} else {
	            		Debug.log(TAG, "closed a socket :" + _sc);
	            		stopRun();
	            	}
	            	
	            } else if((_key.readyOps() & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE) {
	            	Debug.log(TAG, "OP_WRITE");
	            	_key.interestOps(_key.interestOps() & ~SelectionKey.OP_WRITE);
	            }
	            iterator.remove();
			}
			selectedKeys.clear();
		}
		Debug.log(TAG, "stop success,matched matchKey channel:" +  req.getMatchKey().channel());
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			selector = Selector.open();
			sc = SocketChannel.open();
			sc.configureBlocking(false);
			mKey = sc.register(selector,  SelectionKey.OP_CONNECT);
			//sc.connect(new InetSocketAddress("39.108.50.248", 3128));
			//sc.connect(new InetSocketAddress("172.13.32.122", 808));
			String host = req.headersMap.get("Host");
			String hostName = req.headersMap.get("Host");
			int port = 80;
			if(host.indexOf(":") != -1) {
				port = Integer.parseInt(host.substring(host.indexOf(":") + 1));
				hostName = host.substring(0, host.indexOf(":"));
			}
			Debug.log(TAG, "host:" + hostName + ":" + port);
			sc.connect(new InetSocketAddress(hostName, port));
			//new DataHandler().start();
			listening();
		} catch(Exception e) {
			e.printStackTrace();
			stopRun();
		}
		Thread current = Thread.currentThread();
		System.err.print("A Sender stopped, Thread infomation : ");
        System.err.print("priority:"+current.getPriority() + " ");  
        System.err.print("name:"+current.getName() + " ");  
        System.err.print("activeCount:"+current.activeCount() + " ");  
        System.err.print("id:"+current.getId() + " ");  
        System.err.println(current.toString());
	}
}


package com.yuriey.proxy;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RequestParser extends Thread {
	private final String TAG = "RequestParser";
	private static RequestParser parser;
	private boolean stopFlag = false;
	private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
	private RequestParser() {
		
	}
	public static synchronized RequestParser getInstance() {
		if(parser == null) {
			parser = new RequestParser();
		}
		return parser;
	}
	public void stopRun() {
		stopFlag = true;
	}
	class Parser implements Runnable {
		private Request request;
		
		public Parser(Request request) {
			this.request = request;
		}
		private boolean checkRequest() {
			if(request.parsed) {
				return false;
			}
			Debug.log(TAG, "checkRequest()");
			//Debug.log(TAG, "checkRequest()-->request content:" + request.getContent() + "request parsedContent:" + request.getParsedContent());
			String lineSeparator = System.getProperty("line.separator");
			int index = request.getContent().indexOf(lineSeparator + lineSeparator);
			if(index != -1) {
				String headerStr = request.getContent().substring(0, index);
				String bodyStr = request.getContent().substring(index + 2*lineSeparator.length());
				String[] headers = headerStr.split(lineSeparator);
				for(int i = 0;i < headers.length;i++) {
					if(i == 0) {
						request.headersMap.put("Protocol", headers[i]);
					} else {
						String[] headerLine = headers[i].split(": ");
						request.headersMap.put(headerLine[0], headerLine[1]);
					}
				}
				String contentLenStr = request.headersMap.get("Content-Length");
				if(contentLenStr != null) {
					int contentLen = Integer.parseInt(contentLenStr);
					if(contentLen == bodyStr.length()) {
						request.headersMap.put("Body", bodyStr);
						return true;
					}
				}
				return true;
			}
			return false;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Debug.log(TAG, "a child parse thread begin");
			if(checkRequest()) {
				Debug.log(TAG, "@@@parsing...");
				String protocol = request.headersMap.get("Protocol");
				if(!protocol.substring(0, protocol.indexOf(" ")).equals(M.METHODS[M.TYPE_CONNECT])) {
					request.headersMap.put("Connection", request.headersMap.get("Proxy-Connection"));
					request.headersMap.remove("Proxy-Connection");
					request.headersMap.put("Protocol" ,protocol.replaceFirst("http://", "").replaceFirst(request.headersMap.get("Host"), ""));
					Iterator it = request.headersMap.entrySet().iterator();
					request.addParsedContent(request.headersMap.get("Protocol") + System.getProperty("line.separator"));
					while(it.hasNext()) {
						Map.Entry<String, String> entry = (Entry<String, String>)it.next();
						String key = entry.getKey();
						String value = entry.getValue();
						if(!key.equals("Protocol") && !key.equals("Body")) {
							request.addParsedContent(key + ": " + value + System.getProperty("line.separator"));
						}
					}
					request.addParsedContent(System.getProperty("line.separator") + request.headersMap.get("Body"));
				}
				Debug.log(TAG, "parsedContent:" + request.getParsedContent());
				request.parsed = true;
				Sender sender = new Sender(request);
				sender.start();
				//M.allSender.add(sender);
				synchronized (M.senderMap) {
					M.senderMap.put(request.getMatchKey(), sender);
				}
			}
			Debug.log(TAG, "a child parse thread end");
		}
		
	}
	private void parse() {
		Iterator iterator = M.reqMap.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry<SelectionKey, Request> entry = (Entry<SelectionKey, Request>)iterator.next();
			Request req = entry.getValue();
			if(!req.parsed) {
				fixedThreadPool.execute(new Parser(req));
			}	
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!stopFlag) {
			//Debug.log(TAG, "parse main thread while once");
			synchronized (M.reqMap) {
				if(!M.reqMap.isEmpty()) {
					parse();
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

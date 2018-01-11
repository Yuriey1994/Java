package com.yuriey.proxy;

import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Map;

public class Request {
	private String content;
	public boolean parsed = false;
	private String parsedContent;
	private SelectionKey mkey;
	public Map<String, String> headersMap = new HashMap<String, String>();
	//private SelectionKey
	public Request(SelectionKey key) {
		content = new String();
		parsedContent = new String();
		this.mkey = key;
	}
	public SelectionKey getMatchKey() {
		return mkey;
	}
	public String getContent() {
		return content;
	}
	public String getParsedContent() {
		return parsedContent;
	}
	public void addContent(byte[] b) {
		content = content + new String(b);
	}
	public void addContent(String str) {
		content = content + str;
	}
	public void addParsedContent(byte[] b) {
		parsedContent = parsedContent + new String(b);
	}
	public void addParsedContent(String str) {
		parsedContent = parsedContent + str;
	}
}

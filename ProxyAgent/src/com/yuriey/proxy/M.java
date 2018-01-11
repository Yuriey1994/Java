package com.yuriey.proxy;

import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class M {
    public static final int port = 808;
	public static final int TYPE_GET = 0;
	public static final int TYPE_POST = 1;
	public static final int TYPE_CONNECT = 2;
	public static final int TYPE_PUT = 3;
	public static final int TYPE_DELETE = 4;
	public static final int TYPE_HEAD = 5;
	public static final int TYPE_OPTION = 6;
	public static final String[] METHODS = {"GET", "POST", "CONNECT", "PUT", "DELETE", "HEAD", "OPTION"};
	public static Map<SelectionKey, Request> reqMap = new HashMap<SelectionKey, Request>();
	public static Map<SelectionKey, Sender> senderMap = new HashMap<SelectionKey, Sender>();
	public static List<Sender> allSender= new ArrayList<Sender>();
}

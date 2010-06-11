package com.tah.im;

import java.util.HashMap;
import java.util.Map;

public class onlineUsersSingleton{

	private static Map<String, userInfo> _instance = new HashMap<String, userInfo>();
	private onlineUsersSingleton(){
	//	_instance = new IMNotifier();

	}
	public static Map<String, userInfo> getInstance(){
		return _instance;
	}

}
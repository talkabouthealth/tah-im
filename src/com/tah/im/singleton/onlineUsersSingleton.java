package com.tah.im.singleton;

import java.util.HashMap;
import java.util.Map;

import com.tah.im.userInfo;

public class onlineUsersSingleton{
	private static onlineUsersSingleton _onlineUsersSingleton = new onlineUsersSingleton();
	private Map<String, userInfo> monlineUsersSingleton = new HashMap<String, userInfo>();

	private onlineUsersSingleton(){
	//	_instance = new IMNotifier();
		System.out.println("OnlineUsers Singleton");
	}
	public static onlineUsersSingleton getInstance(){
		return _onlineUsersSingleton;
	}
	public void removeOnlineUser(String _mail){
		monlineUsersSingleton.remove(_mail);	
	}
	public void addOnlineUser(String _mail, userInfo _uInfo){
		monlineUsersSingleton.put(_mail, _uInfo);	
	}
	public userInfo getOnlineUser(String _mail){
		return monlineUsersSingleton.get(_mail);
	}
	public Map<String, userInfo> getOnlineUserMap(){
		return monlineUsersSingleton;
	}
}
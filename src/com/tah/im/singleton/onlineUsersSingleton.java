package com.tah.im.singleton;

import java.util.HashMap;
import java.util.Map;

import com.tah.im.UserInfo;

public class OnlineUsersSingleton {
	private static OnlineUsersSingleton _onlineUsersSingleton = new OnlineUsersSingleton();
	private Map<String, UserInfo> onlineUsersMap = new HashMap<String, UserInfo>();

	private OnlineUsersSingleton() {}

	public static OnlineUsersSingleton getInstance() {
		return _onlineUsersSingleton;
	}

	public void removeOnlineUser(String email) {
		onlineUsersMap.remove(email);
	}

	public void addOnlineUser(String email, UserInfo userInfo) {
		onlineUsersMap.put(email, userInfo);
	}

	public UserInfo getOnlineUser(String email) {
		return onlineUsersMap.get(email);
	}
	
	public boolean isUserOnline(String email) {
		return onlineUsersMap.get(email) != null;
	}

	public Map<String, UserInfo> getOnlineUserMap() {
		return onlineUsersMap;
	}

	public void printAll() {
		System.out.println("--------- Online Users -------------");
		for (String user : onlineUsersMap.keySet()) {
			System.out.println(user + " : "+onlineUsersMap.get(user));
		}
	}
}
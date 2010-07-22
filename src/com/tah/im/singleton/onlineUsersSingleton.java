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

	public void removeOnlineUser(String uid) {
		onlineUsersMap.remove(uid);
	}

	public void addOnlineUser(String uid, UserInfo userInfo) {
		onlineUsersMap.put(uid, userInfo);
	}

	public UserInfo getOnlineUser(String uid) {
		return onlineUsersMap.get(uid);
	}
	
	public boolean isUserOnline(String uid) {
		return onlineUsersMap.get(uid) != null;
	}

	public Map<String, UserInfo> getOnlineUserMap() {
		return onlineUsersMap;
	}

	public void printAll() {
		System.out.println("--------- Online Users -------------");
		for (String userId : onlineUsersMap.keySet()) {
			System.out.println(userId + " : "+onlineUsersMap.get(userId));
		}
	}
}
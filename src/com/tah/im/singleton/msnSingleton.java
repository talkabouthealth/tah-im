package com.tah.im.singleton;

import improject.IMSession;
import com.tah.im.IMNotifier;
import com.tah.im.IMNotifierMSN;


public class msnSingleton{
	private static msnSingleton _msnSingleton = new msnSingleton();
	private IMNotifierMSN mmsnSingleton = new IMNotifierMSN();

	private msnSingleton(){
	//	_instance = new IMNotifier();
		System.out.println("MSN Singleton");
	}
	public static msnSingleton getInstance(){
		return _msnSingleton;
	}
	public boolean isUserOnline(String _mail) throws Exception{
		return mmsnSingleton.isUserOnline(_mail);
	}
	public IMSession getSession(){
		return mmsnSingleton.getSession();
	}
	public String getMainAcc(){
		return mmsnSingleton.getMainAcc();
	}
	public boolean Broadcast(final String[] mail_list, String[] UID, String _tid) throws Exception{
		return mmsnSingleton.Broadcast(mail_list, UID, _tid);
	}
	
	public void addContact(String contactEmail) throws Exception {
		mmsnSingleton.addContact(contactEmail);
	}
}
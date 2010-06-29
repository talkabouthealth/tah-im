package com.tah.im.singleton;

import improject.IMSession;
import com.tah.im.IMNotifier;


public class googleSingleton{
	private static googleSingleton _googleSingleton = new googleSingleton();
	private IMNotifier moGoogleSingleton = new IMNotifier();

	private googleSingleton(){
	//	_instance = new IMNotifier();
		System.out.println("Google Singleton");

	}
	public static googleSingleton getInstance(){
		return _googleSingleton;
	}
	public boolean isUserOnline(String _mail) throws Exception{
		return moGoogleSingleton.isUserOnline(_mail);
	}
	public IMSession getSession(){
		return moGoogleSingleton.getSession();
	}
	public String getMainAcc(){
		return moGoogleSingleton.getMainAcc();
	}
	public boolean Broadcast(final String[] mail_list, int[] UID, int _tid) throws Exception{
		return moGoogleSingleton.Broadcast(mail_list, UID, _tid);
	}
	
	public void addContact(String contactEmail) throws Exception {
		moGoogleSingleton.addContact(contactEmail);
	}

}
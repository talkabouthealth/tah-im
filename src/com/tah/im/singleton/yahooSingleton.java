package com.tah.im.singleton;

import improject.IMSession;
import com.tah.im.IMNotifier;
import com.tah.im.IMNotifierYahoo;


public class yahooSingleton{
	private static yahooSingleton _yahooSingleton = new yahooSingleton();
	private IMNotifierYahoo myahooSingleton = new IMNotifierYahoo();

	private yahooSingleton(){
	//	_instance = new IMNotifier();
		System.out.println("Yahoo Singleton");
	}
	public static yahooSingleton getInstance(){
		return _yahooSingleton;
	}
	public boolean isUserOnline(String _mail) throws Exception{
		return myahooSingleton.isUserOnline(_mail);
	}
	public IMSession getSession(){
		return myahooSingleton.getSession();
	}
	public String getMainAcc(){
		return myahooSingleton.getMainAcc();
	}
	public boolean Broadcast(final String[] mail_list, int[] UID, int _tid) throws Exception{
		return myahooSingleton.Broadcast(mail_list, UID, _tid);
	}
}
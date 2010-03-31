package com.tah.im;

import improject.IMException;
import improject.IMSession;
import improject.IMSession.IMService;

public class OnlineTest {
	public static void IsOnline(final String userAccount) {
		final IMSession session = new IMSession();
		final String MainAccount = "testIM5566@gmail.com";
		final String MainPasswd = "hu183183";
		
		//login main account
		session.addLogin(IMService.GOOGLE, MainAccount, MainPasswd);
		
		try { 
			session.connect();
		} catch (IMException e) {
			e.printStackTrace();
		}
		
		while (true) {
	        try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
		
	}

}

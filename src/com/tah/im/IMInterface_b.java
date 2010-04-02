package com.tah.im;

import improject.IMException;
import improject.IMSession;
import improject.Message;
//import improject.MessageListener;
import improject.IMSession.IMService;


public class IMInterface_b {
	public static void Broadcast(final String[][] Data_list) {
		final IMSession session = new IMSession();
		final String MainAccount = "testIM5566@gmail.com";
		final String MainPasswd = "hu183183";
		
		//login main account
		session.addLogin(IMService.GOOGLE, MainAccount, MainPasswd);
	
		System.out.println("Broadcast...\n");
		
		//setting Broadcast Message
		Message bMessage = new Message();
		bMessage.setImService(IMService.GOOGLE);
		bMessage.setBody("No Link Now. Try it later.");  //default Message
		bMessage.setFrom(MainAccount);
		//bMessage.setTo(); SetTo in the loop		
			
		try { 
			session.connect();
		} catch (IMException e) {
			e.printStackTrace();
		}	
		
		//Wait for connecting
		try { 
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Sending Message
		try {
				for(int j = 0; j < Data_list[0].length; j++){
					if(session.isOnline(MainAccount, Data_list[0][j])){
						bMessage.setTo(Data_list[0][j]);
						//System.out.println(Data_list[0][j]);
						bMessage.setBody("Please use the link: " + Data_list[1][j]);
						session.sendMessage(bMessage);
					}
				}
		} catch (IMException e) {
			e.printStackTrace();
		}	

	}
}
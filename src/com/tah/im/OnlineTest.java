package com.tah.im;

import improject.IMException;
import improject.IMSession;
import improject.Message;
import improject.MessageListener;
import improject.IMSession.IMService;

public class OnlineTest {
	public static void IsOnline(final String userAccount) {
		final IMSession session = new IMSession();
		final String MainAccount = "testIM5566@gmail.com";
		final String MainPasswd = "hu183183";
		
		//login main account
		session.addLogin(IMService.GOOGLE, MainAccount, MainPasswd);
		//*
		session.addMessageListener(new MessageListener() {

			@Override
			public void messageReceived(Message message) {
				  // Print received message
				System.out.println("Message received:");
				System.out.println(message);
						
		          // Discover & print user "online?" status
				try {
					System.out.println("User is online? " + session.isOnline(message.getTo(), message.getFrom()));
				} catch (IMException e) {
					e.printStackTrace();
				}
				
				// Send reply to the same service/user it came from
				System.out.println("Sending reply...\n");

				Message replyMessage = new Message();
				replyMessage.setImService(message.getImService());
				replyMessage.setBody("Hi!");
				replyMessage.setFrom(message.getTo());
				replyMessage.setTo(message.getFrom());

				try {
					session.sendMessage(replyMessage);
				} catch (IMException e) {
					e.printStackTrace();
				}	
			}
		});
		//*/
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

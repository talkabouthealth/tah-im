package com.tah.im;

import improject.IMException;
import improject.IMSession;
import improject.Message;
import improject.MessageListener;
import improject.IMSession.IMService;


/**
 * Example of using the common IM API
 *
 */
public class echo {	
  public static void main(String[] args) {		
	final IMSession session = new IMSession();
		
	// Add login information of required user(s)
	session.addLogin(IMService.YAHOO, "YahooID", "YahooPass");
	session.addLogin(IMService.MSN, "MSNID", "MSNPass");
	session.addLogin(IMService.GOOGLE, "GTalkID", "GTalkPass");
	session.addLogin(IMService.SKYPE, "SkypeID", "SkypePass");
		
	//add message listener(s) for all service
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
		
	// Connect and login to all added user accounts
	try { 
		session.connect();
	} catch (IMException e) {
		e.printStackTrace();
	}
		
	// Wait in the loop for incoming messages; 
	// incoming messages will trigger above 
	// MessageListener function
	while (true) {
        try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
  }
}
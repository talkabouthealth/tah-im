package com.tah.im;

import improject.IMException;
import improject.IMSession;
import improject.Message;
import improject.MessageListener;
import improject.IMSession.IMService;

import java.sql.SQLException;



public class IMNotifier {

	private IMSession session;
	private String MainAccount;
	private String MainPasswd;
	
	//Constructor: login when creating the IMInterface
	public IMNotifier(){
		//login by this account		
		this.MainAccount = "talkabouthealth.com@gmail.com";
		this.MainPasswd = "CarrotCake917";
		
		this.session = new IMSession();
		
		session.addLogin(IMService.GOOGLE, MainAccount, MainPasswd);
		
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
		
		//connect 
		try { 
			session.connect();
		} catch (IMException e) {
			e.printStackTrace();
		}			
		
		// Create Thread to keep the Main Account Online
		// incoming Message will trigger Listener
		Thread thread = new Thread(new Runnable() {
            public void run() { 
                while(true) { 
                	 try {
         				Thread.sleep(5000);
         			} catch (InterruptedException e) {
         				e.printStackTrace();
         			}
                } 
            }        
        }); 
		thread.start();		
	}

	public boolean Broadcast(final String[] mail_list, int[] UID) throws Exception {
		
		int topic_id=0; //temp topic
		int count =0; //counts of successful sending
		
		String url = "http://talkabouthealth.com/chat?uid=";
		String des = "Please join the disscusion of the topic. Use the link: ";
		
		System.out.println("Broadcast...\n");
		
		//setting Broadcast Message
		Message bMessage = new Message();
		bMessage.setImService(IMService.GOOGLE);
		bMessage.setBody("No link. Try it later.");  //default message
		bMessage.setFrom(this.MainAccount);
		
		try { 
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Sending Message
		try {
				//Link to DB
				SQL_CON SQL_Conn = new SQL_CON();
				
				for(int i = 0; i < mail_list.length; i++){
					if(session.isOnline(MainAccount, mail_list[i])){
						System.out.println(mail_list[i] + " is online. Send messages to it");
						bMessage.setTo(mail_list[i]);
						bMessage.setBody(des + url + UID[i]);
						session.sendMessage(bMessage);
						
						count++;
						//record in DB
						try {
							SQL_Conn.InsertToNoti(UID[i],topic_id,1);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					else{
						try {
							SQL_Conn.InsertToNoti(UID[i],topic_id,0);
						} catch (SQLException e){
							e.printStackTrace();
						}
					}
				}
								
				SQL_Conn.CloseLink();  //close DB
				
		} catch (IMException e) {
			e.printStackTrace();
		}
		
		if(count < mail_list.length) return false;
		else return true;				
			
	}//end of Broadcast
	
}

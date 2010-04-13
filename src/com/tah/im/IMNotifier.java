package com.tah.im;

import java.sql.SQLException;

import com.tah.matcher.SQL_CON;

import improject.IMException;
import improject.IMSession;
import improject.Message;
import improject.MessageListener;
import improject.IMSession.IMService;


public class IMNotifier {

	private IMSession session;
	private String MainAccount;
	private String MainPasswd;
	
	//Constructor: login when creating the IMInterface
	public IMNotifier(){
		//login by this account		
		this.MainAccount = "testIM5566@gmail.com";
		this.MainPasswd = "hu183183";
		
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

	public void Broadcast(final String[][] Data_list, int[] UID) throws Exception {
		
		int topic_id=0; //temp topic 
		
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
				
				for(int i = 0; i < Data_list[0].length; i++){
					if(session.isOnline(MainAccount, Data_list[0][i])){
						System.out.println(Data_list[0][i] + " is online. Send messages to it");
						bMessage.setTo(Data_list[0][i]);
						bMessage.setBody("Please use the link: " + Data_list[1][i]);
						session.sendMessage(bMessage);
						
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
				
				SQL_Conn.CloseLink();
				
		} catch (IMException e) {
			e.printStackTrace();
		}				
			
	}//end of Broadcast
}

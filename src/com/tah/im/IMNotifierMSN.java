package com.tah.im;

import improject.IMException;
import improject.IMSession;
import improject.Message;
import improject.MessageListener;
import improject.UserListener;
import improject.IMSession.IMService;


import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.tah.im.singleton.onlineUsersSingleton;



public class IMNotifierMSN  {

	private IMSession session;
	private String MainAccount;
	private String MainPasswd;
	private List<String> onlineUsers;
	private onlineUsersSingleton onlineUserInfo = onlineUsersSingleton.getInstance();
	//Constructor: login when creating the IMInterface
	public IMNotifierMSN(){
		//login by this account		
		this.MainAccount = "talkabouthealth.com@live.com";
		this.MainPasswd = "CarrotCake917";
		
		this.session = new IMSession();
		
		session.addLogin(IMService.MSN, MainAccount, MainPasswd);
		
		//add message listener(s) for all service
		session.addMessageListener(new MessageListener() {
			  @Override
			  // Automatically reply incoming message.
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
					String chatroomUrl;
					chatroomUrl = " http://talkabouthealth.com/talk12 ";	
						
					Message replyMessage = new Message();
					replyMessage.setImService(message.getImService());
					replyMessage.setBody("Thank you for starting a conversation. Click on this link to start the conversation: " + chatroomUrl);
					replyMessage.setFrom(message.getTo());
					replyMessage.setTo(message.getFrom());
		
					try {
						session.sendMessage(replyMessage);
					} catch (IMException e) {
						e.printStackTrace();
					}
			  }
		});
		// Automatically update onlineuserlist when users change their status.
		session.addUserListener (new UserListener () {

			@Override
			public void statusChanged(String user, String newStatus) {
				// TODO Auto-generated method stub
				String userMail = user;
				// If user changes status to ONLINE
				if(newStatus.equals("ONLINE")){
					// If userMail is NOTn onlineuserlist
					if(!onlineUserInfo.getOnlineUserMap().containsKey(userMail)){
						try {
							// Get user information from Database (talkmi.talkers)
							userInfo _user = new userInfo(userMail);
							System.out.println(userMail + "(" + _user.getUname() + ") is now ONLINE");
							// Check if user is our member.
							if(_user.isExist(userMail)){
								// Add user into online user list.
								onlineUserInfo.addOnlineUser(userMail, _user);		
								System.out.println(userMail + "(" + onlineUserInfo.getOnlineUser(userMail).getUname() + ") is added in to online user list");
							} else{
								System.out.println(userMail + "(" + userMail + ") does not exist.");
							}
							
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
	
					}

				}
				// If user changes status to OFFLINE
				else{
					// Check if user is in the onlineuserlist
					if(onlineUserInfo.getOnlineUserMap().containsKey(userMail)){
						// Remove from onlinuserlist
						onlineUserInfo.removeOnlineUser(userMail);
						System.out.println(userMail + " is removed from list");
					}
				}
				// Create Iterator to print out all online users.
				Collection collection = onlineUserInfo.getOnlineUserMap().values();
				Iterator iterator = collection.iterator();
				java.util.Date date= new java.util.Date();
				String period;
				System.out.println("************************All online user list*************************");
				while(iterator.hasNext()){
					userInfo uI = (userInfo) iterator.next();							
					System.out.println(uI.getUname() + " is online");
				}
				System.out.println("**********************************************************************");
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
		try { 
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public boolean isUserOnline(String email) throws Exception{

		if(this.session.isOnline(MainAccount, email )){
			System.out.println( email + " is online");
			return true;
		} else {
			return false;
		}
	}
	public IMSession getSession(){
		return session;
	}
	public String getMainAcc(){
		return MainAccount;
	}
	
	public boolean Broadcast(final String[] mail_list, int[] UID, int _tid) throws Exception {
		
		int count =0; //counts of successful sending
		
		String url = "http://talkabouthealth.com/chat?uid=";
		String des = "Please join the disscusion of the topic. Use the link: ";
		
		System.out.println("Broadcast...\n");
		
		//setting Broadcast Message
		Message bMessage = new Message();
		bMessage.setImService(IMService.MSN);
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
							SQL_Conn.InsertToNoti(UID[i],_tid,1);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					else{
						try {
							SQL_Conn.InsertToNoti(UID[i],_tid,0);
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

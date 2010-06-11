package com.tah.im;

import improject.IMException;
import improject.IMSession;
import improject.Message;
import improject.MessageListener;
import improject.UserListener;
import improject.IMSession.IMService;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class IMNotifier {

	private IMSession session;
	private String MainAccount;
	private String MainPasswd;
	private Map<String, userInfo> onlineUserInfo = onlineUsersSingleton.getInstance();
	private static IMNotifier _instance = new IMNotifier();
	//Constructor: login when creating the IMInterface
	private IMNotifier(){
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
		
		session.addUserListener(new UserListener(){

			@Override
			public void statusChanged(String user, String newStatus){
				// TODO Auto-generated method stub
				int end = user.indexOf("/");
				String userMail = user.substring(0, end);

				if(newStatus.equals("available")){
					if(!onlineUserInfo.containsKey(userMail)){
						try {
							userInfo _user = new userInfo(userMail);
							System.out.println(_user.getUname() + " is onw ONLINE");
							if(_user.getUname() != null){
								onlineUserInfo.put(userMail, _user);
								System.out.println(userMail + " is added in to online user list");						
							}

						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
	
					}

				}
				else{
					
					onlineUserInfo.remove(userMail);
					System.out.println(userMail + " is removed from list");
				}
		
				try {
					List<String> onlineUsers = session.getOnlineContacts(MainAccount);
					System.out.println("size of online Users " + onlineUsers.size());
					System.out.println("size of onlineUserInfo " + onlineUserInfo.size());
					System.out.println("================Start===================");
					for(int i = 0; i < onlineUsers.size(); i++){	
						if(onlineUserInfo.containsKey(onlineUsers.get(i))){
							int _uid = onlineUserInfo.get(onlineUsers.get(i)).getUid();
							System.out.println(onlineUserInfo.get(onlineUsers.get(i)).getUname() + " has IM acc. of " + onlineUserInfo.get(onlineUsers.get(i)).getEmail());
							System.out.println(onlineUsers.get(i) + " is " + onlineUserInfo.get(onlineUsers.get(i)).getGender());
							try {
								System.out.println(onlineUsers.get(i) + " was last notificated on " + onlineUserInfo.get(onlineUsers.get(i)).getlastNotiTime(_uid));
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							System.out.println(onlineUsers.get(i) + " has been notified " + onlineUserInfo.get(onlineUsers.get(i)).getTimesBeenNoti() + " times in the past 24 hours.");
						}

					}
					System.out.println("================End===================");
					} catch (IMException e) {
						// TODO Auto-generated catch block
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
		try { 
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static IMNotifier getInstance(){
		if(_instance == null){
			_instance = new IMNotifier();
		}
		return _instance;
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

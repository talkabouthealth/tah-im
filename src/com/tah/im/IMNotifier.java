package com.tah.im;

import improject.IMException;
import improject.IMSession;
import improject.LoginInfo;
import improject.Message;
import improject.MessageListener;
import improject.UserListener;
import improject.IMSession.IMService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import sun.security.jca.GetInstance;

import com.tah.im.singleton.OnlineUsersSingleton;

//TODO: make good logging?
public class IMNotifier {
	
	private static IMNotifier instance;

	//TODO: get this from constructor?
	LoginInfo[] loginInfoArray = new LoginInfo[] {
//		new LoginInfo(IMService.GOOGLE, "talkabouthealth.com@gmail.com", "CarrotCake917"),
//		new LoginInfo(IMService.MSN, "talkabouthealth.com@live.com", "CarrotCake917"),
//		new LoginInfo(IMService.YAHOO, "talkabouthealth@ymail.com", "CarrotCake917"),
		
		new LoginInfo(IMService.GOOGLE, "talkabouthealth.com.test@gmail.com", "CarrotCake917"),
		new LoginInfo(IMService.MSN, "talkabouthealth.com.test@hotmail.com", "CarrotCake917"),
		new LoginInfo(IMService.YAHOO, "talkabouthealthtest@ymail.com", "CarrotCake917"),
	};
	Map<IMService, LoginInfo> loginInfoMap;
	
	private IMSession session;
	private OnlineUsersSingleton onlineUserInfo = OnlineUsersSingleton.getInstance();
	
	public static IMNotifier getInstance() {
		if (instance == null) {
			instance = new IMNotifier();
		}
		return instance;
	}

	private IMNotifier() {
		session = new IMSession();
		
		loginInfoMap = new HashMap<IMSession.IMService, LoginInfo>();
		for (LoginInfo loginInfo : loginInfoArray) {
			session.addLogin(loginInfo.getImService(), loginInfo.getUser(), loginInfo.getPassword());
			loginInfoMap.put(loginInfo.getImService(), loginInfo);
		}
		
		// add message listener(s) for all service
		session.addMessageListener(new MessageListener() {
			@Override
			// Automatically reply incoming message.
			public void messageReceived(Message message) {
				System.out.println("Message received:\n" + message);

				// Send reply to the same service/user it came from
				System.out.println("Sending reply...");
				
				// Create msg content
				String chatRoomURL = "http://talkabouthealth.com/talk12";
				Message replyMessage = new Message();
				replyMessage.setImService(message.getImService());
				replyMessage
						.setBody("Thank you for starting a conversation. Click on this link to start the conversation: "
								+ chatRoomURL);
				replyMessage.setFrom(message.getTo());
				replyMessage.setTo(message.getFrom());
				
				try {
					session.sendMessage(replyMessage);
				} catch (IMException e) {
					//TODO: handle errors correctly?
					e.printStackTrace();
				}
			}
		});
		
		// Automatically update onlineuserlist when users change their status.
		session.addUserListener(new UserListener() {

			@Override
			public void statusChanged(String user, String newStatus) {
				UserInfo userInfo = getUserInfo(user);
				
				// If user changes status to ONLINE
				if (newStatus.equalsIgnoreCase("available") || newStatus.equalsIgnoreCase("online")) {
					if (!onlineUserInfo.isUserOnline(userInfo.getUid())) {
						try {
							// Get user information from Database
							System.out.println(userInfo + " is now ONLINE");

							// Check if user is our member.
							if (userInfo.isExist()) {
								// Add user into online user list.
								onlineUserInfo.addOnlineUser(userInfo.getUid(), userInfo);
								System.out.println(userInfo + " is added in to online user list");
							} else {
								System.out.println(userInfo + " does not exist.");
							}
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
				// If user changes status to OFFLINE
				else {
					if (onlineUserInfo.isUserOnline(userInfo.getUid())) {
						onlineUserInfo.removeOnlineUser(userInfo.getUid());
						System.out.println(userInfo + " is removed from list");
					}
				}
				onlineUserInfo.printAll();
			}

		});
		
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
	
	//convert user (format of IMService) to UserInfo with data form TAH db
	private UserInfo getUserInfo(String user) {
		if (user == null) {
			return null;
		}
		
		//'YahooIM', 'WindowLive', 'GoogleTalk'
		String imService = null;
		String imUsername = null;
		if (user.contains("@gmail")) {
			//Google service
			imService = "GoogleTalk";
			imUsername = user;
			int end = user.indexOf("@");
			if (end != -1) {
				imUsername.substring(0, end);
			}
		}
		else if (user.contains("@live") || user.contains("@hotmail")) {
			imService = "WindowLive";
			imUsername = user;
			int end = user.indexOf("@");
			if (end != -1) {
				imUsername.substring(0, end);
			}
		}
		else {
			//for now default - Yahoo
			imService = "YahooIM";
			imUsername = user;
		}
		
		UserInfo userInfo = DBUtil.getUserByIm(imService, imUsername);
		return userInfo;
	}
	
	public void addContact(String imService, String imUsername) throws Exception {
		//TODO finish it?
//		session.addContact(MainAccount, imUsername);
	}
	
	public boolean Broadcast(final String[] mail_list, String[] UID, String _tid) throws Exception {
		
		int count =0; //counts of successful sending
		
		String url = "http://talkabouthealth.com/chat?uid=";
		String des = "Please join the disscusion of the topic. Use the link: ";
		
		System.out.println("Broadcast...\n");
		
		//setting Broadcast Message
		Message bMessage = new Message();
		bMessage.setImService(IMService.GOOGLE);
		bMessage.setBody("No link. Try it later.");  //default message
		
		//TODO: finish this!
		bMessage.setFrom(null);
		
		try { 
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Sending Message
		try {
				for(int i = 0; i < mail_list.length; i++){
					//TODO: finish this!
					if(session.isOnline("Main Account", mail_list[i])){
						System.out.println(mail_list[i] + " is online. Send messages to it");
						bMessage.setTo(mail_list[i]);
						bMessage.setBody(des + url + UID[i]);
						session.sendMessage(bMessage);
						
						count++;
						//record in DB
						try {
							DBUtil.saveNotification(UID[i], _tid, 1);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					else {
						try {
							DBUtil.saveNotification(UID[i], _tid, 0);
						} catch (Exception e){
							e.printStackTrace();
						}
					}
				}
								
		} catch (IMException e) {
			e.printStackTrace();
		}
		
		if(count < mail_list.length) return false;
		else return true;				
			
	}//end of Broadcast
	
}

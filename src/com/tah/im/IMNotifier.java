package com.tah.im;

import improject.IMException;
import improject.IMSession;
import improject.IMSession.IMService;
import improject.LoginInfo;
import improject.Message;
import improject.MessageListener;
import improject.UserListener;

import java.util.HashMap;
import java.util.Map;

import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.tah.im.singleton.OnlineUsersSingleton;

//TODO: make good logging?
public class IMNotifier {
	
	public static final String TALK_URL = "http://talkabouthealth.com:9000/talk/";
	public static final String SIGNUP_URL = "http://www.talkabouthealth.com:9000/signup";
	
	private static IMNotifier instance;

	private Map<IMService, LoginInfo> loginInfoMap;
	private IMSession session;
	private OnlineUsersSingleton onlineUserInfo = OnlineUsersSingleton.getInstance();
	
	/**
	 * We use it for 2 step process of starting topic via IM.
	 * Stores first received message - topic (map value) from particular user (map key)
	 * TODO: maybe user timeout map? We don't need very old messages.
	 */
	private Map<String, String> receivedMessages = new HashMap<String, String>();
	
	public static void init(LoginInfo[] loginInfoArray) {
		if (instance == null) {
			instance = new IMNotifier(loginInfoArray);
		}
	}
	
	public static IMNotifier getInstance() {
		if (instance == null) {
			throw new AssertionError("IMNotifier isn't initialized");
		}
		return instance;
	}

	private IMNotifier(LoginInfo[] loginInfoArray) {
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

				// Create topic with given topic name
				UserInfo userInfo = getUserInfo(message.getFrom());
				
				String reply = null;
				if (!userInfo.isExist()) {
					//if no such user - reply with registration message
					//TODO: url to Notifications/Accounts page?
					reply = "Hi, thank you for the message. Please register here: "+SIGNUP_URL;
				}
				else {
					String previousMessage = receivedMessages.get(message.getFrom());
					if (previousMessage == null) {
						reply = "Hi, thank you for the message. "+
						 	"Would you like to start the new conversation: \""+message.getBody()+"\"? "+
						 	"Reply 'yes' to start the new conversation. " +
						 	"To contact us, please send an email to support@talkabouthealth.com.";
						
						receivedMessages.put(message.getFrom(), message.getBody());
					}
					else {
						//second step - create topic if 'yes'
						if (message.getBody().equals("yes")) {
							int tid = DBUtil.createTopic(userInfo.getUid(), previousMessage);
							reply = "Thank you. Click on this link to join the conversation: " + TALK_URL + tid;
						}
						else {
							reply = "Conversation isn't created. Please write again to create a new one.";
						}
						
						receivedMessages.remove(message.getFrom());
					}
				}
				
				// Create msg content
				Message replyMessage = new Message();
				replyMessage.setImService(message.getImService());
				replyMessage.setBody(reply);
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
								System.out.println(userInfo + " isn't in TAH database!");
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
		
		String imService = null;
		String imUsername = null;
		if (user.contains("@gmail")) {
			//Google service
			imService = "GoogleTalk";
			imUsername = removeService(user);
		}
		else if (user.contains("@live") || user.contains("@hotmail")) {
			imService = "WindowLive";
			imUsername = removeService(user);
		}
		else {
			//for now default - Yahoo
			imService = "YahooIM";
			imUsername = user;
		}
		
		//TODO: user can enter full id and we won't find it in db?
		UserInfo userInfo = DBUtil.getUserByIm(imService, imUsername);
		return userInfo;
	}
	
	private String removeService(String imUsername) {
		int end = imUsername.indexOf("@");
		if (end != -1) {
			imUsername = imUsername.substring(0, end);
		}
		return imUsername;
	}

	//TODO move it to enum?
	public IMService getIMServiceByName(String imService) {
		//'YahooIM', 'WindowLive', 'GoogleTalk'
		if ("GoogleTalk".equals(imService)) {
			return IMService.GOOGLE;
		}
		else if ("WindowLive".equals(imService)) {
			return IMService.MSN;
		}
		else if ("YahooIM".equals(imService)) {
			return IMService.YAHOO;
		}
		else {
			throw new IllegalArgumentException("Bad IM Service name");
		}
	}
	
	public void addContact(String imServiceName, String imUsername) throws Exception {
		IMService imService = getIMServiceByName(imServiceName);
		imUsername = prepareUsername(imUsername, imService);
		
		session.addContact(loginInfoMap.get(imService).getUser(), imUsername);
	}
	
	private String prepareUsername(String imUsername, IMService imService) {
		if (imUsername.contains("@")) {
			//Yahoo doesn't need "@yahoo.com"
			if (imService == IMService.YAHOO) {
				imUsername = removeService(imUsername);
			}
		}
		else {
			if (imService == IMService.GOOGLE) {
				imUsername += "@gmail.com";
			}
			if (imService == IMService.MSN) {
				//TODO: hotmail or live?
				imUsername += "@hotmail.com";
			}
		}
		
		return imUsername;
	}

	public IMSession getSession() {
		return session;
	}

	public void broadcast(String[] uidArray, String topicId) throws Exception {
		System.out.println("Broadcast...\n");
		
		DBObject topicDBObject = DBUtil.getTopicById(topicId);
		if (topicDBObject == null) {
			System.err.println("Bad topicId for broadcasting: "+topicId);
			return;
		}
		
		Message notificationMessage = new Message();
		String url = TALK_URL+topicDBObject.get("tid");
		
		String authorUserName = (String)((DBRef)topicDBObject.get("uid")).fetch().get("uname");
		String text = authorUserName+" is requesting support for: " +
				"\""+topicDBObject.get("topic")+"\". Click here to help: "+url;
		notificationMessage.setBody(text);
		
		//sending message
		for(int i = 0; i < uidArray.length; i++) {
			try {
				UserInfo userInfo = DBUtil.getUserById(uidArray[i]);
				
				//from - get account according to user's IM Service
				IMService imService = getIMServiceByName(userInfo.getImService());
				notificationMessage.setImService(imService);
				notificationMessage.setFrom(loginInfoMap.get(imService).getUser());
				
				//to - fix IM Username
				String imUsername = prepareUsername(userInfo.getImUsername(), imService);
				notificationMessage.setTo(imUsername);
				
				session.sendMessage(notificationMessage);
				
				DBUtil.saveNotification(uidArray[i], topicId);
			} catch (IMException e) {
				e.printStackTrace();
			}
		}
	}
}

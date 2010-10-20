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
import com.tah.im.model.IMAccount;
import com.tah.im.model.Notification;
import com.tah.im.model.Notification.NotificationType;
import com.tah.im.model.UserInfo;
import com.tah.im.singleton.OnlineUsersSingleton;

public class IMNotifier {
	
	public static final String TAH_URL = "http://talkabouthealth.com:9000/";
	public static final String TALK_URL = TAH_URL+"talk/";
	public static final String SIGNUP_URL = TAH_URL+"signup";
	
	private static IMNotifier instance;
	
	private Map<IMService, LoginInfo> loginInfoMap;
	private IMSession session;
	private OnlineUsersSingleton onlineUserInfo = OnlineUsersSingleton.getInstance();
	
	private MessageHandler messageHandler;
	
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
		messageHandler = new MessageHandler(this);
		
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
				messageHandler.handle(message);
			}
		});
		
		// Automatically update onlineuserlist when users change their status.
		session.addUserListener(new UserListener() {

			@Override
			public void statusChanged(String user, String newStatus) {
				UserInfo userInfo = IMUtil.getUserInfo(user);
				
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
	
	public void addContact(String imServiceName, String imUsername) throws Exception {
		IMService imService = IMUtil.getIMServiceByName(imServiceName);
		imUsername = IMUtil.prepareUsername(imUsername, imService);
		
		session.addContact(loginInfoMap.get(imService).getUser(), imUsername);
	}
	
	public IMSession getSession() {
		return session;
	}

	public String broadcast(String[] uidArray, String convoId) throws Exception {
		System.out.println("Broadcast...\n");
		
		DBObject convoDBObject = DBUtil.getConvoById(convoId);
		if (convoDBObject == null) {
			System.err.println("Bad topicId for broadcasting: "+convoId);
			return null;
		}
		
		Message notificationMessage = new Message();
		String talkURL = TALK_URL+convoDBObject.get("tid");
		String convoURL = TAH_URL+convoDBObject.get("main_url");
		
		DBObject authorDBObject = ((DBRef)convoDBObject.get("uid")).fetch();
		String authorId = authorDBObject.get("_id").toString();
		String authorUserName = (String)authorDBObject.get("uname");
		String convoTitle = (String)convoDBObject.get("topic");
		
		StringBuilder text = new StringBuilder();
		String convoType = (String)convoDBObject.get("type");
		if ("CONVERSATION".equals(convoType)) {
//			murray requests a live talk for:
//			"new talk to test im".
//			Click here to join the talk: http://talkabouthealth.com:9000/talk/83
//			To answer, reply to this message.
			text.append(authorUserName+" requests a live talk for:\n");
			text.append("\""+convoTitle+"\"\n");
			text.append("Click here to join the talk: "+talkURL+" \n");
			text.append("To answer, reply to this message.");
		}
		else {
//			murray asked the question:
//			"new question to test im"
//			To answer, reply to this message or click on this link: http://talkabouthealth.com/new-question-to-test-im
			text.append(authorUserName+" asked the question:\n");
			text.append("\""+convoTitle+"\"\n");
			text.append("To answer, reply to this message or click on this link:\n");
			text.append(convoURL);
		}
		notificationMessage.setBody(text.toString());
		
		//sending message
		for(int i = 0; i < uidArray.length; i++) {
			//do not send notification to author of the event
			if (authorId.equals(uidArray[i])) {
				continue;
			}
			try {
				UserInfo userInfo = DBUtil.getUserById(uidArray[i]);
				
				for (IMAccount imAccount : userInfo.getImAccounts()) {
					//from - get account according to user's IM Service
					IMService imService = IMUtil.getIMServiceByName(imAccount.getService());
					notificationMessage.setImService(imService);
					notificationMessage.setFrom(loginInfoMap.get(imService).getUser());
					
					//to - fix IM Username
					String imUsername = IMUtil.prepareUsername(imAccount.getUserName(), imService);
					notificationMessage.setTo(imUsername);
					
					Notification notification = new Notification(NotificationType.CONVO);
					notification.setRelatedId(convoId);
					notification.setUserName(authorUserName);
					notification.setText(text.toString());
					messageHandler.saveNotification(imAccount, notification);
					
					System.out.println("Sending message to... "+notificationMessage.getTo());
					session.sendMessage(notificationMessage);
				}
				
				DBUtil.saveNotification(uidArray[i], convoId);
			} catch (IMException e) {
				e.printStackTrace();
			}
		}
		
		return notificationMessage.getBody();
	}
	
	public void answerNotify(String[] uidArray, String fromTalker, String convoId, 
			String parentId, String answerId, String answerText) {
		DBObject convoDBObject = DBUtil.getConvoById(convoId);
		if (convoDBObject == null) {
			System.err.println("Bad convoId for notification: "+convoId);
			return;
		}
		
		Message notificationMessage = new Message();
		String authorUserName = fromTalker;
		
		StringBuilder text = new StringBuilder();
		if (parentId == null) {
//			@mn_jones answered this request:
//				"here is an answer to the request"
//				To respond to @mn_jones, reply to this message.
			text.append("@"+authorUserName+" answered this request:\n");
			text.append("\""+answerText+"\"\n");
			text.append("To respond to @"+authorUserName+", reply to this message.");
		}
		else {
//			@murrayjones replied:
//				"reply to test from mn_jones".
//				To respond to @murrayjones, reply with your message.
			text.append("@"+authorUserName+" replied:\n");
			text.append("\""+answerText+"\"\n");
			text.append("To respond to @"+authorUserName+", reply with your message.");
		}
		notificationMessage.setBody(text.toString());
		
		for(int i = 0; i < uidArray.length; i++) {
			try {
				UserInfo userInfo = DBUtil.getUserById(uidArray[i]);
				//do not notify author of the answer/reply
				if (authorUserName.equals(userInfo.getUname())) {
					continue;
				}
				
				for (IMAccount imAccount : userInfo.getImAccounts()) {
					//from - get account according to user's IM Service
					IMService imService = IMUtil.getIMServiceByName(imAccount.getService());
					notificationMessage.setImService(imService);
					notificationMessage.setFrom(loginInfoMap.get(imService).getUser());
					
					//to - fix IM Username
					String imUsername = IMUtil.prepareUsername(imAccount.getUserName(), imService);
					notificationMessage.setTo(imUsername);
					
					Notification notification = new Notification(NotificationType.ANSWER);
					if (parentId == null) {
						notification.setRelatedId(answerId);
					}
					else {
						notification.setRelatedId(parentId);
					}
					notification.setUserName(authorUserName);
					notification.setText(text.toString());
					messageHandler.saveNotification(imAccount, notification);
					
					session.sendMessage(notificationMessage);
				}
				
				DBUtil.saveNotification(uidArray[i], convoId);
			} catch (IMException e) {
				e.printStackTrace();
			}
		}

	}
}

package com.tah.im;

import improject.IMException;
import improject.Message;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.tah.im.model.IMAccount;
import com.tah.im.model.Notification;
import com.tah.im.model.UserInfo;
import com.tah.im.model.UserMessage;
import com.tah.im.model.Notification.NotificationType;
import com.tah.im.model.UserMessage.MessageCommand;

public class MessageHandler {
	
	//timeout when we need to reply automatially or ask user confirmation
	//in minutes
	private static final int REPLY_TIMEOUT = 10;
	
	/**
	 * Previous messages (not commands) of a user and 
	 * previous notifications to this user (new convos, replies, etc.)
	 */
	private Map<IMAccount, UserMessage> messages = new HashMap<IMAccount, UserMessage>();
	private Map<IMAccount, Notification> notifications = new HashMap<IMAccount, Notification>();
	
	private IMNotifier imNotifier;
	
	public MessageHandler(IMNotifier imNotifier) {
		super();
		this.imNotifier = imNotifier;
	}

	public void handle(Message message) {
		System.out.println("Message received:\n" + message);

		String reply = null;
		try {
			reply = prepareReply(message);
		}
		catch (Exception e) {
			e.printStackTrace();
			reply = "Sorry, unknown error.";
		}
		
		
		// Create msg content
		Message replyMessage = new Message();
		replyMessage.setImService(message.getImService());
		replyMessage.setBody(reply);
		replyMessage.setFrom(message.getTo());
		replyMessage.setTo(message.getFrom());
		
		try {
			imNotifier.getSession().sendMessage(replyMessage);
		} catch (IMException e) {
			//TODO: handle errors correctly?
			e.printStackTrace();
		}
	}
	
	private String prepareReply(Message message) {
		// Create topic with given topic name
		UserInfo userInfo = IMUtil.getUserInfo(message.getFrom());
		if (!userInfo.isExist()) {
			//if no such user - reply with registration message
			//TODO: url to Notifications/Accounts page?
			return "Hi, thank you for the message. Please register here: "+IMNotifier.SIGNUP_URL;
		}
		
		String reply = null;
		String body = message.getBody();
		
		UserMessage previousMessage = messages.get(userInfo.getCurrentIMAccount());
		Notification previousNotification = notifications.get(userInfo.getCurrentIMAccount());
		
		UserMessage imMessage = parseMessage(body);
		if (imMessage.getCommand() == MessageCommand.NO_COMMAND) {
			messages.put(userInfo.getCurrentIMAccount(), imMessage);
		}
		
		switch (imMessage.getCommand()) {
		case START_CONVO:
		case START_QUESTION:
			String newConvo = imMessage.getParam();
			if (newConvo == null && previousMessage != null) {
				newConvo = previousMessage.getParam();
			}
			
			String tid = APIUtil.createConvo(userInfo.getUid(), newConvo);
			if (tid != null) {
				if (imMessage.getCommand() == MessageCommand.START_CONVO) {
					return "Thank you. Click on this link to start the live conversation: " + IMNotifier.TALK_URL + tid;
				}
				else {
					return "Thank you. We will find members to answer your question and send you the Answers via IM and email.";
				}
			}
			else {
				//TODO: send error
				return "Error.";
			}
			
		case REPLY:
			String newReply = imMessage.getParam();
			if (newReply == null && previousMessage != null) {
				newReply = previousMessage.getParam();
			}
			
			if (previousNotification != null && newReply != null) {
				return "Thank you. Your last message was <"+previousNotification.getText()+">.\n" +
						"Would you like to reply to this message with \""+newReply+"\"? " +
						"Reply with 'yes' to send the message, or @exit to start over.";
			}
			
			break;
			
		case YES:
			//confirm sending of @reply to message
			if (previousNotification != null && previousMessage != null) {
				return saveAnswer(previousNotification, userInfo, previousMessage.getParam());
			}
			break;
			
		case EXIT:
			return "Starting over. Send us a message to begin again. Advanced users may use the following commands: " +
					"@start <conversation topic>, @question <question>, @reply <reply>. For example, \"@question " +
					"What are some things you wish people had said or done?\".";
			
		case NO_COMMAND:
			//if < 10 mins - reply!
			Calendar limitTime = Calendar.getInstance();
			limitTime.add(Calendar.SECOND, -REPLY_TIMEOUT);
			if (previousNotification != null 
					&& previousNotification.getTime().compareTo(limitTime.getTime()) > 0) {
				return saveAnswer(previousNotification, userInfo, imMessage.getParam());
			}
			else {
				//if not - ask!!
				return reply = "Hi, thank you for the message: \""+imMessage.getParam()+"\".\n"+
				 	"Would you like to start a new conversation, ask a question, " +
				 	"or reply to your last message? "+
				 	"Type \"@start\" to start a live conversation, " +
				 	"\"@question\" to ask a question, or \"@reply\" to reply.";
			}
		}
		
		return reply;
	}
	
	private String saveAnswer(Notification previousNotification, UserInfo userInfo, String text) {
		switch (previousNotification.getType()) {
		case CONVO:
			//NotificationType.CONVO
			String id = APIUtil.createAnswer(previousNotification.getRelatedId(), 
						userInfo.getUid(), "", text);
			return "Thank you, your answer was added.";
		case ANSWER:
			id = APIUtil.createAnswer("", 
					userInfo.getUid(), previousNotification.getRelatedId(), text);
			return "Thank you, your reply was sent to "+previousNotification.getUserName()+".";
		}
		
		return null;
	}

	private UserMessage parseMessage(String body) {
		UserMessage message = new UserMessage();
		
		//"yes" command doesn't have "@" prefix, so we add it manully
		//this allows unified command parsing
		if (body != null && body.equals("yes")) {
			body = "@yes";
		}
		
		for (MessageCommand command : MessageCommand.values()) {
			if (body.startsWith("@"+command.getCommandText())) {
				message.setCommand(command);
				message.setParam(command.parseParam(body));
				break;
			}
		}
		
		if (message.getCommand() == null) {
			message.setCommand(MessageCommand.NO_COMMAND);
			message.setParam(body);
		}
		
		return message;
	}

	public void saveNotification(IMAccount imAccount, Notification notification) {
		notifications.put(imAccount, notification);
	}

}

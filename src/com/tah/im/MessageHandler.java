package com.tah.im;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sun.misc.GC.LatencyRequest;

import com.tah.im.model.IMAccount;
import com.tah.im.model.Notification;
import com.tah.im.model.UserMessage;
import com.tah.im.model.UserMessage.MessageCommand;
import com.tah.im.model.UserInfo;

import improject.IMException;
import improject.Message;

public class MessageHandler {
	
	//timeout when we need to reply automatially or ask user confirmation
	//in minutes
	private static final int REPLY_TIMEOUT = 3;
	
	/**
	 * We use it for 2 step process of starting topic via IM.
	 * Stores first received message - topic (map value) from particular user (map key)
	 * TODO: maybe user timeout map? We don't need very old messages.
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

		String reply = prepareReply(message);
		
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
		
		//TODO: save only no-commands?
		UserMessage previousMessage = messages.get(userInfo.getCurrentIMAccount());
		Notification previousNotification = notifications.get(userInfo.getCurrentIMAccount());
		
		UserMessage imMessage = parseMessage(body);
		messages.put(userInfo.getCurrentIMAccount(), imMessage);
		
		switch (imMessage.getCommand()) {
		case START_CONVO:
		case START_QUESTION:
			String newConvo = imMessage.getParam();
			if (newConvo == null) {
				if (previousMessage != null) {
					newConvo = previousMessage.getParam();
				}
				else {
					//TODO: possible?
				}
			}
			
//			int tid = DBUtil.createTopic(userInfo.getUid(), previousMessage);
//			reply = "Thank you. Click on this link to join the conversation: " + IMNotifier.TALK_URL + tid;
			int tid = 1;
			return "Thank you. Click on this link to start the live conversation: " + IMNotifier.TALK_URL + tid + newConvo;
			
			//send request to API
//			break;
		case REPLY:
			String newReply = imMessage.getParam();
			if (newReply == null) {
				if (previousMessage != null) {
					//TODO: yes handling!!
					return "Thank you. Your last message was <"+previousNotification.getText()+">.\n" +
							"Would you like to reply to this message with \""+previousMessage.getParam()+"\"? " +
							"Reply with 'yes' to send the message, or @exit to start over.";
				}
				else {
					//TODO: possible?
				}
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
				switch (previousNotification.getType()) {
				case CONVO:
					//TODO: save answer
					return "Thank you, your answer was added.";
				case ANSWER:
					return "Thank you, your reply was sent to "+previousNotification.getUserName()+".";
				}
			}
			else {
				//if not - ask!!
				return reply = "Hi, thank you for the message: \""+message.getBody()+"\".\n"+
				 	"Would you like to start a new conversation, ask a question, " +
				 	"or reply to your last message? "+
				 	"Type \"@start\" to start a live conversation, " +
				 	"\"@question\" to ask a question, or \"@reply\" to reply.";
			}
			
			break;
		}
		
//			UserMessage previousMessage = messages.get(userInfo.getCurrentIMAccount());
//			if (previousMessage == null) {
//				reply = "Hi, thank you for the message. "+
//				 	"Would you like to start the new conversation: \""+message.getBody()+"\"? "+
//				 	"Reply 'yes' to start the new conversation. " +
//				 	"To contact us, please send an email to support@talkabouthealth.com.";
//			}
//			else {
//				//second step - create topic if 'yes'
//				if (message.getBody().equals("yes")) {
////					int tid = DBUtil.createTopic(userInfo.getUid(), previousMessage);
////					reply = "Thank you. Click on this link to join the conversation: " + IMNotifier.TALK_URL + tid;
//				}
//				else {
//					reply = "Conversation isn't created. Please write again to create a new one.";
//				}
//			}
		
		return reply;
	}

	private UserMessage parseMessage(String body) {
		UserMessage message = new UserMessage();
		
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

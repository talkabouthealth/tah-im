package com.tah.im;

import java.util.HashMap;
import java.util.Map;

import com.tah.im.model.IMMessage;
import com.tah.im.model.IMMessage.MessageCommand;
import com.tah.im.model.UserInfo;

import improject.IMException;
import improject.Message;

public class MessageHandler {
	
	/**
	 * We use it for 2 step process of starting topic via IM.
	 * Stores first received message - topic (map value) from particular user (map key)
	 * TODO: maybe user timeout map? We don't need very old messages.
	 */
	private Map<String, String> receivedMessages = new HashMap<String, String>();
	private IMNotifier imNotifier;
	
	public MessageHandler(IMNotifier imNotifier) {
		super();
		this.imNotifier = imNotifier;
	}

	public void handle(Message message) {
		System.out.println("Message received:\n" + message);

		// Create topic with given topic name
		UserInfo userInfo = IMUtil.getUserInfo(message.getFrom());
		
		String reply = null;
		if (!userInfo.isExist()) {
			//if no such user - reply with registration message
			//TODO: url to Notifications/Accounts page?
			reply = "Hi, thank you for the message. Please register here: "+IMNotifier.SIGNUP_URL;
		}
		else {
			String body = message.getBody();
			
			IMMessage imMessage = parseMessage(body);
			switch (imMessage.getCommand()) {
			case START_CONVO:
			case START_QUESTION:
				String newConvo = imMessage.getParam();
				if (newConvo == null) {
					//get from previous message
				}
				
				//send request to API
				break;
			case REPLY:
				break;
			case EXIT:
				break;
				
			case NO_COMMAND:
				//if < 10 mins - reply!
				
				//if not - ask!!
				
				break;
			}
			
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
					reply = "Thank you. Click on this link to join the conversation: " + IMNotifier.TALK_URL + tid;
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
			imNotifier.getSession().sendMessage(replyMessage);
		} catch (IMException e) {
			//TODO: handle errors correctly?
			e.printStackTrace();
		}
	}
	
	private IMMessage parseMessage(String body) {
		IMMessage message = new IMMessage();
		
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

}

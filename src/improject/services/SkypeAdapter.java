package improject.services;

import improject.IMException;
import improject.LoginInfo;
import improject.Message;
import improject.IMSession.IMService;

import com.skype.ChatMessage;
import com.skype.ChatMessageAdapter;
import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.User;
import com.skype.User.Status;

public class SkypeAdapter extends AbstractServiceAdapter {
	
	public SkypeAdapter(LoginInfo loginInfo) {
		super(loginInfo);
	}

	@Override
	public void connect() throws IMException {
		Skype.setDeamon(true); // to prevent exiting from this program
		try {
	        Skype.addChatMessageListener(new ChatMessageAdapter() {
	            public void chatMessageReceived(ChatMessage received) throws SkypeException {
	            	if(!received.getSender().isAuthorized()) {
	            		System.out.println("Contact Request @SKYPE: " + received.getSenderDisplayName());
	            		received.getSender().setAuthorized(true);
	            	}
	            	
	                if (received.getType().equals(ChatMessage.Type.SAID)) {
	                	Message message = new Message();
	    				message.setImService(IMService.SKYPE);
	    				message.setBody(received.getContent());
	    				message.setFrom(received.getSenderId());
	    				message.setTo(loginInfo.getUser());
	    				
	    				messageListener.messageReceived(message);
	                }
	            }
	            
	        });
	        
		}
		catch (SkypeException skypeException) {
			throw new IMException(skypeException.getMessage());
		}

	}
	
	public void disconnect() throws IMException {
	}
	
	public boolean status() {
		try {
			return Skype.isRunning();
		} catch (SkypeException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void sendMessage(Message message) throws IMException {
		try {
			Skype.chat(message.getTo()).send(message.getBody());
		} catch (Exception e) {
			throw new IMException(e.getMessage());
		}
	}
	
	@Override
	public boolean isOnline(String contact) throws IMException {
		try {
			Status status = Skype.getContactList().getFriend(contact).getOnlineStatus();
			if (status == Status.OFFLINE) {
				return false;
			}
			else {
				return true;
			}
		} catch (Exception e) {
			throw new IMException(e.getMessage());
		}
	}

	@Override
	public void addContact(String contact) throws IMException {
		try {
			User usr = Skype.getUser(contact);				
			if(!usr.isAuthorized()) usr.setAuthorized(true);
			usr.send("Hello! Please add me to your Contact list.");
		} catch (SkypeException e) {
			throw new IMException(e.toString());
		}
	}
}

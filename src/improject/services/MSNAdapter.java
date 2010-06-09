package improject.services;

import java.util.ArrayList;
import java.util.List;

import improject.IMException;
import improject.LoginInfo;
import improject.Message;
import improject.IMSession.IMService;
import net.sf.jml.Email;
import net.sf.jml.MsnConnection;
import net.sf.jml.MsnContact;
import net.sf.jml.MsnContactList;
import net.sf.jml.MsnMessenger;
import net.sf.jml.MsnSwitchboard;
import net.sf.jml.MsnUserStatus;
import net.sf.jml.event.MsnAdapter;
import net.sf.jml.impl.MsnMessengerFactory;
import net.sf.jml.message.MsnInstantMessage;

/**
 * Service adapter for MSN Messenger. Implemented using JML (http://jml.blathersource.org/)
 * @author kindcoder
 *
 */
public class MSNAdapter extends AbstractServiceAdapter {
	MsnAdapter adapter = null;
	MsnMessenger messenger = null;

	public MSNAdapter(LoginInfo loginInfo) {
		super(loginInfo);
	}

	@Override
	public void connect() throws IMException {
		if(messenger != null || adapter != null) {
			disconnect();
			messenger = null;
			adapter = null;
		}
		
		messenger = MsnMessengerFactory.createMsnMessenger(
				loginInfo.getUser(), loginInfo.getPassword());
		adapter = new MsnAdapter() {

            public void instantMessageReceived(MsnSwitchboard switchboard,
                    MsnInstantMessage message, MsnContact contact) {
            	Message newMessage = new Message();
            	newMessage.setImService(IMService.MSN);
            	newMessage.setBody(message.getContent());
            	newMessage.setFrom(contact.getEmail().getEmailAddress());
            	newMessage.setTo(loginInfo.getUser());
            	
            	messageListener.messageReceived(newMessage);
            }
            
            public void contactAddedMe(MsnMessenger messenger,
            		MsnContact contact) {
        		System.out.println("Contact Request @MSN: " + contact.getEmail());            	
            	messenger.removeFriend(contact.getEmail(),false);            	
            	messenger.addFriend(contact.getEmail(),null);
            }

			@Override
			public void contactStatusChanged(MsnMessenger messenger,
            		MsnContact contact) {
				userListener.statusChanged(contact.getEmail().toString(), contact.getStatus().toString());
			}
            
            
        };
		
		messenger.addMessageListener(adapter);
		messenger.addContactListListener(adapter);
		messenger.login();
	}
	
	public void disconnect() {
		messenger.logout();
		messenger.removeMessageListener(adapter);
	}
	
	public boolean status() {
		MsnConnection X = messenger.getConnection(); 
		if(X == null || X.getConnectionType() == null) return false; else return true;
	}

	@Override
	public void sendMessage(Message message) throws IMException {
		try {
			messenger.sendText(Email.parseStr(message.getTo()), message.getBody());
		} catch (Exception e) {
			throw new IMException(e.getMessage());
		}
	}
	
	@Override
	public boolean isOnline(String contact) {
		Email e = Email.parseStr(contact);		
		MsnContactList cl = messenger.getContactList();		
		MsnContact c = cl.getContactByEmail(e);
		if(c == null) return false;
		
		MsnUserStatus status = c.getStatus();
		if (status == MsnUserStatus.OFFLINE) {
			return false;
		}
		else {
			return true;
		}
	}

	@Override
	public void addContact(String contact) throws IMException {
		//TODO -- may need work on getting messages 
		messenger.removeFriend(Email.parseStr(contact),false);
		messenger.addFriend(Email.parseStr(contact),null);
	}

	@Override
	public List<String> getOnlineContacts() throws IMException {
		List<String> onlineContacts = new ArrayList<String>();
		
		MsnContactList contactList = messenger.getContactList();
		for (MsnContact user : contactList.getContacts()) {
			if (user.getStatus() == MsnUserStatus.ONLINE) {
				onlineContacts.add(user.getOldDisplayName()); // change from getId() to getOldDisplayName()
			}
		}
		
		return onlineContacts;
	}
}

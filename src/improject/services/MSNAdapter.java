package improject.services;

import improject.IMException;
import improject.LoginInfo;
import improject.Message;
import improject.IMSession.IMService;
import net.sf.jml.Email;
import net.sf.jml.MsnConnection;
import net.sf.jml.MsnContact;
import net.sf.jml.MsnMessenger;
import net.sf.jml.MsnSwitchboard;
import net.sf.jml.MsnUserStatus;
import net.sf.jml.event.MsnMessageAdapter;
import net.sf.jml.impl.MsnMessengerFactory;
import net.sf.jml.message.MsnInstantMessage;

/**
 * Service adapter for MSN Messenger. Implemented using JML (http://jml.blathersource.org/)
 * @author kindcoder
 *
 */
public class MSNAdapter extends AbstractServiceAdapter {
	MsnMessageAdapter adapter;
	MsnMessenger messenger;

	public MSNAdapter(LoginInfo loginInfo) {
		super(loginInfo);
	}

	@Override
	public void connect() throws IMException {
		messenger = MsnMessengerFactory.createMsnMessenger(
				loginInfo.getUser(), loginInfo.getPassword());
		adapter = new MsnMessageAdapter() {

            public void instantMessageReceived(MsnSwitchboard switchboard,
                    MsnInstantMessage message, MsnContact contact) {
            	Message newMessage = new Message();
            	newMessage.setImService(IMService.MSN);
            	newMessage.setBody(message.getContent());
            	newMessage.setFrom(contact.getEmail().getEmailAddress());
            	newMessage.setTo(loginInfo.getUser());
            	
            	messageListener.messageReceived(newMessage);
            }
        };
		
		messenger.addMessageListener(adapter);
		messenger.login();
	}
	
	public void disconnect() {
		messenger.logout();
		messenger.removeMessageListener(adapter);
	}
	
	public boolean status() {
		MsnConnection X = messenger.getConnection(); 
		if(X==null) return false; else return true;
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
		MsnUserStatus status = messenger.getContactList().getContactByEmail(Email.parseStr(contact)).getStatus();
		if (status == MsnUserStatus.OFFLINE) {
			return false;
		}
		else {
			return true;
		}
	}
}

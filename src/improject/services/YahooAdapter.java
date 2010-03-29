package improject.services;

import java.io.IOException;

import improject.IMException;
import improject.LoginInfo;
import improject.Message;
import improject.IMSession.IMService;

import org.openymsg.network.Session;
import org.openymsg.network.SessionState;
import org.openymsg.network.Status;
import org.openymsg.network.event.SessionAdapter;
import org.openymsg.network.event.SessionEvent;

/**
 * Implemented using OpenYMSG library (http://openymsg.blathersource.org/node/4)
 * @author kindcoder
 *
 */
public class YahooAdapter extends AbstractServiceAdapter {
	
	Session session;

	public YahooAdapter(LoginInfo loginInfo) {
		super(loginInfo);
	}

	@Override
	public void connect() throws IMException {
		session = new Session();
		
		session.addSessionListener(new SessionAdapter() {
			@Override
			public void messageReceived(SessionEvent event) {	
				Message message = new Message();
				message.setImService(IMService.YAHOO);
				message.setBody(event.getMessage());
				message.setFrom(event.getFrom());
				message.setTo(event.getTo());
				
				messageListener.messageReceived(message);
			}
		});
		
		try {
			session.login(loginInfo.getUser(), loginInfo.getPassword());
		} catch (Exception e) {
			throw new IMException(e.getMessage());
		} 	
	}
	
	public void disconnect() throws IMException {
		try {
			session.logout();
		} catch (IllegalStateException e) {
			throw new IMException(e.getMessage());
		} catch (IOException e) {
			throw new IMException(e.getMessage());
		}
	}
	
	public boolean status() {
		SessionState X = session.getSessionStatus();
		if(X == SessionState.UNSTARTED || X == SessionState.FAILED) return false; else return true;
	}

	@Override
	public void sendMessage(Message message) throws IMException {
		try {
			session.sendMessage(message.getTo(), message.getBody());
		} catch (Exception e) {
			throw new IMException(e.getMessage());
		}
	}

	@Override
	public boolean isOnline(String contact) {
		Status status = session.getRoster().getUser(contact).getStatus();
		if (status == Status.OFFLINE) {
			return false;
		}
		else {
			return true;
		}
	}
}

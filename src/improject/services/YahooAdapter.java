package improject.services;

import improject.IMException;
import improject.LoginInfo;
import improject.Message;
import improject.IMSession.IMService;

import java.io.IOException;

import org.openymsg.network.Session;
import org.openymsg.network.SessionState;
import org.openymsg.network.Status;
import org.openymsg.network.YahooUser;
import org.openymsg.network.event.SessionAdapter;
import org.openymsg.network.event.SessionEvent;

/**
 * Implemented using OpenYMSG library (http://openymsg.blathersource.org/node/4)
 * @author kindcoder
 *
 */
public class YahooAdapter extends AbstractServiceAdapter {
	
	Session session = null;

	public YahooAdapter(LoginInfo loginInfo) {
		super(loginInfo);
	}

	@Override
	public void connect() throws IMException {
		if(session != null) {
			try { disconnect(); } catch (IMException e) {}
			session = null;
		}
		
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

			@Override
			public void contactRequestReceived(SessionEvent ev) {
				System.out.println("Contact Request @YAHOO: " + ev.getFrom());
				try {
					session.acceptFriendAuthorization(ev.getFrom());
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
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
		YahooUser u = session.getRoster().getUser(contact);
		if(u==null) return false;
		
		Status status = u.getStatus();
		if (status == Status.OFFLINE) {
			return false;
		}
		else {
			return true;
		}
	}

	@Override
	public void addContact(String contact) throws IMException {		
		try {
			session.sendNewFriendRequest(contact,"Friends");
		} catch (IOException e) {
			throw new IMException(e.toString());
		}
	}
}

package improject.services;

import improject.IMException;
import improject.LoginInfo;
import improject.Message;
import improject.IMSession.IMService;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;

/**
 * Service adapter for Google IM (GTalk). Implemented using Smack API (http://www.igniterealtime.org/projects/smack/index.jsp)
 * @author kindcoder
 *
 */
public class GoogleAdapter extends AbstractServiceAdapter {
	
	XMPPConnection connection;

	public GoogleAdapter(LoginInfo loginInfo) {
		super(loginInfo);
	}

	@Override
	public void connect() throws IMException {		
		//initialize connection
		ConnectionConfiguration cc = new ConnectionConfiguration("talk.google.com", 5222, "gmail.com");
		cc.setSASLAuthenticationEnabled(true);
		//cc.setDebuggerEnabled(true);
		connection = new XMPPConnection(cc);
				
		//use non-blocking thread
		Thread t = new Thread() {
			public void run() {
				try {
					connection.connect();
					connection.login(loginInfo.getUser(), loginInfo.getPassword());

					connection.addPacketListener(
						new PacketListener() {
							@Override
							public void processPacket(Packet packet) {
								//convert packet to message
								org.jivesoftware.smack.packet.Message message = 
									(org.jivesoftware.smack.packet.Message)packet;
								Message newMessage = new Message();
				            	newMessage.setImService(IMService.GOOGLE);
				            	newMessage.setBody(message.getBody());
				            	newMessage.setFrom(message.getFrom());
				            	newMessage.setTo(loginInfo.getUser());
				            	
				            	messageListener.messageReceived(newMessage);
							}
						},
						new PacketTypeFilter(org.jivesoftware.smack.packet.Message.class)
					);
					
				} catch (Exception e) {
					e.printStackTrace();
				} 	
			}
			
		};
		t.setDaemon(true);
		t.start();
	}
	
	public void disconnect() {
		connection.disconnect();
	}
	
	public boolean status() {
		return connection.isConnected();
	}

	@Override
	public void sendMessage(Message message) throws IMException {
		try {
			org.jivesoftware.smack.packet.Message newMessage = 
				new org.jivesoftware.smack.packet.Message(message.getTo(), 
						org.jivesoftware.smack.packet.Message.Type.chat);
			
			newMessage.setBody(message.getBody());
			
			connection.sendPacket(newMessage);
		} catch (Exception e) {
			throw new IMException(e.getMessage());
		}
	}
	
	@Override
	public boolean isOnline(String contact) {
		Presence presence  = connection.getRoster().getPresence(contact);
		if (presence.getType() == Type.unavailable) {
			return false;
		}
		else {
			return true;
		}
	}
}

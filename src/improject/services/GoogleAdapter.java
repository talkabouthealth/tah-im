package improject.services;

import improject.IMException;
import improject.IMSession.IMService;
import improject.LoginInfo;
import improject.Message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.packet.XMPPError;

/**
 * Service adapter for Google IM (GTalk). Implemented using Smack API (http://www.igniterealtime.org/projects/smack/index.jsp)
 * @author kindcoder
 *
 */
public class GoogleAdapter extends AbstractServiceAdapter {
	
	XMPPConnection connection = null;

	public GoogleAdapter(LoginInfo loginInfo) {
		super(loginInfo);
	}

	@Override
	public void connect() throws IMException {	
		if(connection != null) {
			disconnect();
			connection = null;
		}
		
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

					//set roster mode to autoaccept
					connection.getRoster().setSubscriptionMode(SubscriptionMode.accept_all);

					connection.addPacketListener(
						new PacketListener() {
							@Override
							public void processPacket(Packet packet) {								
								//convert packet to message
								org.jivesoftware.smack.packet.Message message = 
									(org.jivesoftware.smack.packet.Message)packet;
								
								XMPPError e = packet.getError();
								if(e!=null) {
									System.out.println("Error msg from GOOGLE : " + e.toString()); 
									return;
								}
								
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
					
					Roster roster = connection.getRoster();
					roster.addRosterListener(new RosterListener() {	
						public void presenceChanged(Presence presence) {
							userListener.statusChanged(presence.getFrom(), presence.getType().toString());
					    }
						
						public void entriesAdded(Collection<String> addresses) {}
					    public void entriesDeleted(Collection<String> addresses) {}
					    public void entriesUpdated(Collection<String> addresses) {}
					});

					
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

	@Override
	public void addContact(String contact) throws IMException {
		/*  Below cause null pointer while add users into contact
		try {
			Collection collection =  connection.getRoster().getEntries();
			Iterator iterator = collection.iterator();
			while(iterator.hasNext()){
				RosterEntry rosterEntry = (RosterEntry) iterator.next();
				System.out.println(rosterEntry);
			}
			connection.getRoster().removeEntry(connection.getRoster().getEntry(contact));
		} catch (XMPPException e) {}		
		*/
		if(connection.getRoster().getEntry(contact) != null){
			try {
				Collection<RosterEntry> collection =  connection.getRoster().getEntries();
				for (RosterEntry rosterEntry : collection) {
					System.out.println(rosterEntry);
				}
				connection.getRoster().removeEntry(connection.getRoster().getEntry(contact));
			} catch (XMPPException e) {}		
		}
		try {
			connection.getRoster().createEntry(contact,contact,null);
		} catch (XMPPException e) {
			throw new IMException(e.toString());
		}
	}

	@Override
	public List<String> getOnlineContacts() throws IMException {
		//get all contacts
		Roster roster = connection.getRoster();
		Collection<RosterEntry> entries = roster.getEntries();
		
		List<String> onlineContacts = new ArrayList<String>();
		for (RosterEntry entry : entries) {
			String user = entry.getUser();
			//offline contacts are always "unavailable" Type
			if (roster.getPresence(user).getType() != Presence.Type.unavailable) {
				onlineContacts.add(user);
			}
		}
		return onlineContacts;
	}
}

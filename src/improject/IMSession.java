package improject;

import improject.services.GoogleAdapter;
import improject.services.MSNAdapter;
import improject.services.ServiceAdapter;
import improject.services.SkypeAdapter;
import improject.services.YahooAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The main entry point to the library. 
 * @author kindcoder
 *
 */
public class IMSession implements MessageListener, UserListener {
	
	//reconnection attempt timeout, sec
	private double reconnectTimeout = 60;
	private double reconnectWait = 300;
	private Map<String,Long> connectTimes;
	private Map<String,Long> disconnectTimes;
	
	//All supported IM services
	public enum IMService {
		YAHOO, MSN, GOOGLE, SKYPE
	}
	
	//login information for all services
	private List<LoginInfo> logins;
	//subscribed listeners
	private List<MessageListener> messageListeners;
	//listeners for user statuses changes
	private List<UserListener> userListeners;
	//list of real service adapters/connections
	private Map<String, ServiceAdapter> serviceAdapters;
	
	public IMSession() {
		logins = new ArrayList<LoginInfo>();
		messageListeners = new ArrayList<MessageListener>();
		userListeners = new ArrayList<UserListener>();
		serviceAdapters = new HashMap<String, ServiceAdapter>();
		connectTimes = new HashMap<String, Long>();
		disconnectTimes = new HashMap<String, Long>();
	}
	
	/**
	 * Adds login information for connections
	 * @param imService
	 * @param user
	 * @param password
	 */
	public void addLogin(IMService imService, String user, String password) {
		LoginInfo loginInfo = new LoginInfo(imService, user, password);
		logins.add(loginInfo);
	}
	
	/**
	 * Initialize and connects to all services, whose login information was given
	 * @throws IMException
	 */
	public void connect() throws IMException {
		for (LoginInfo loginInfo : logins) {
			ServiceAdapter imService = createServiceAdapter(loginInfo);
			imService.setMessageListener(this);
			imService.setUserListener(this);
			imService.connect();
			
			//save serviceAdapters for future use
			String sid = loginInfo.getUser() + serviceAdapterToString(loginInfo.getImService());
			System.out.println("sid is " + sid);
			serviceAdapters.put(sid, imService);
			serviceAdapters.put(loginInfo.getUser(), imService);
			System.out.println("imService of dis is " + serviceAdapters.get(sid));
			connectTimes.put(sid,new Long(System.currentTimeMillis()/1000));
			disconnectTimes.put(sid,new Long(-1));
		}
	}
	
	public void checkConnections() {
		for (LoginInfo loginInfo : logins) {
			//save serviceAdapters for future use
			String sid = loginInfo.getUser() + serviceAdapterToString(loginInfo.getImService());
			ServiceAdapter imService = serviceAdapters.get(sid);
			
			//try restarting dead connections
			double timeout = System.currentTimeMillis()/1000-reconnectTimeout;
			if(!imService.status() && connectTimes.get(sid).longValue()<timeout) {
				if(disconnectTimes.get(sid).longValue()<0) {
					System.out.println("Connection lost on " + imService.toString());
					disconnectTimes.put(sid,new Long(System.currentTimeMillis()/1000));
				}
				
				try {
					timeout = System.currentTimeMillis()/1000-reconnectWait;
					if(disconnectTimes.get(sid).longValue()<timeout) {
						connectTimes.put(sid,new Long(System.currentTimeMillis()/1000));
						
						System.out.println("Reconnecting " + imService.toString());
						imService.connect();
						System.out.println("... success");
						
						disconnectTimes.put(sid,new Long(-1));
					}
				} catch (IMException e) {
					System.out.println("... failure");
				}				
			}
		}	
			
	}
	
	public void disconnect() throws IMException {
		for (LoginInfo loginInfo : logins) {
			//save serviceAdapters for future use
			String sid = loginInfo.getUser() + serviceAdapterToString(loginInfo.getImService());			
			ServiceAdapter imService = serviceAdapters.get(sid);
			imService.disconnect();	
		}		
	}
	
	private String serviceAdapterToString(IMService service) {
		switch (service) {
		case YAHOO:
			return "@YAHOO";
		case MSN:
			return "@MSN";
		case GOOGLE:
			return "@GOOGLE";
		case SKYPE:
			return "@SKYPE";
		default: 
			throw new IllegalArgumentException();
		}		
	}
	
	private ServiceAdapter createServiceAdapter(LoginInfo loginInfo) {
		switch (loginInfo.getImService()) {
			case YAHOO:
				return new YahooAdapter(loginInfo);
			case MSN:
				return new MSNAdapter(loginInfo);
			case GOOGLE:
				return new GoogleAdapter(loginInfo);
			case SKYPE:
				return new SkypeAdapter(loginInfo);
			default: 
				throw new IllegalArgumentException();
		}
	}
	
	public void addMessageListener(MessageListener messageListener) {
		messageListeners.add(messageListener);
	}
	
	public void addUserListener(UserListener userListener) {
		userListeners.add(userListener);
	}
	
	/**
	 * Send given message
	 * Connection is searched using "From" field
	 * @param message
	 * @throws IMException
	 */
	public void sendMessage(Message message) throws IMException {
		ServiceAdapter imService = serviceAdapters.get(message.getFrom());
		imService.sendMessage(message);
	}
	
	/**
	 * Check user is online
	 */	
	public boolean isOnline(String sessionOwner, String contact) throws IMException {
		ServiceAdapter imService = serviceAdapters.get(sessionOwner);
		System.out.println(sessionOwner);
		System.out.println("imService of sessionOwner is " + imService);
		System.out.println("Is " + contact + " online?");
		return imService.isOnline(contact);
	}

	/**
	 * Add user to contacts list & issue authorization request
	 */	
	public void addContact(String sessionOwner, String contact) throws IMException {
		ServiceAdapter imService = serviceAdapters.get(sessionOwner);
		imService.addContact(contact);		
	}
	
	/**
	 * Transmit messages from internal adapters to client listeners
	 */
	@Override
	public void messageReceived(Message message) {
		for (MessageListener messageListener : messageListeners) {
			messageListener.messageReceived(message);
		}
	}
	
	/**
	 * Return all online contacts
	 */
	public List<String> getOnlineContacts(String sessionOwner) throws IMException {
		ServiceAdapter imService = serviceAdapters.get(sessionOwner);
		return imService.getOnlineContacts();
	}

	/**
	 * Transmit status updates from internal adapters to client listeners
	 */
	@Override
	public void statusChanged(String user, String newStatus) {
		for (UserListener userListener : userListeners) {
			userListener.statusChanged(user, newStatus);
		}
	}
}

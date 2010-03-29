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
public class IMSession implements MessageListener {
	//reconnection attempt timeout, sec
	private double reconnectTimeout = 30;
	private Map<String,Long> connectionTimes;
	
	//All supported IM services
	public enum IMService {
		YAHOO, MSN, GOOGLE, SKYPE
	}
	
	//login information for all services
	private List<LoginInfo> logins;
	//subscribed listeners
	private List<MessageListener> messageListeners;
	//list of real service adapters/connections
	private Map<String, ServiceAdapter> serviceAdapters;
	
	public IMSession() {
		logins = new ArrayList<LoginInfo>();
		messageListeners = new ArrayList<MessageListener>();
		serviceAdapters = new HashMap<String, ServiceAdapter>();
		connectionTimes = new HashMap<String, Long>();
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
			imService.connect();
			
			//save serviceAdapters for future use
			String sid = loginInfo.getUser() + serviceAdapterToString(loginInfo.getImService());
			serviceAdapters.put(sid, imService);
			connectionTimes.put(sid,new Long(System.currentTimeMillis()/1000));			
		}
	}
	
	public void checkConnections() {
		for (LoginInfo loginInfo : logins) {
			//save serviceAdapters for future use
			String sid = loginInfo.getUser() + serviceAdapterToString(loginInfo.getImService());
			ServiceAdapter imService = serviceAdapters.get(sid);
			
			//try restarting dead connections
			if((! imService.status()) && 
			  (connectionTimes.get(sid).longValue()<System.currentTimeMillis()/1000-reconnectTimeout)) 
				try {
					connectionTimes.put(sid,new Long(System.currentTimeMillis()/1000));			
					System.out.print("Trying to reconnect" + imService.toString());
					imService.connect();
					System.out.println("... success");
				} catch (IMException e) {
					System.out.println("... failure");
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
	
	public boolean isOnline(String sessionOwner, String contact) throws IMException {
		ServiceAdapter imService = serviceAdapters.get(sessionOwner);
		return imService.isOnline(contact);
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
}

package improject.services;

import improject.IMException;
import improject.Message;
import improject.MessageListener;

/**
 * Interface for operations with each service
 * @author kindcoder
 *
 */
public interface ServiceAdapter {
	
	void setMessageListener(MessageListener messageListener);
	
	void connect() throws IMException;
	
	void disconnect() throws IMException;
	
	boolean status();
	
	void sendMessage(Message message) throws IMException;

	void addContact(String contact) throws IMException;
	
	boolean isOnline(String contact) throws IMException;
}

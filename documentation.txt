This is a bundled IM API library, created for Yuriy Mishchenko 
by Ilya Lakhyzha (aka kindcoder). It provides a common java 
module that can be used to communicate from within a java 
application with IM services including GTalk, MSN messenger, 
Yahoo messenger, and Skype, using uniform interface.

It should be understood that no redistribution of this library 
should ever occur unless an explicit permission from Yuriy 
Mishchenko (yuriy.mishchenko@gmail.com) had been obtained.



############################################################
                       BRIEF DOCUMENTATION
############################################################
NOTE: Due to Skype API limitations, in order for your java 
client to work with Skype, you need to have Skype instance
running with (!)the user used in your java application 
logged in on Skype(!). When your java application will 
start, Skype will display request to permit that application
use its API; someone will need to click once to grant
such permission.

INDIVIDUAL LIBRARIES, SUCH AS SMACK, ARE INCLUDED HERE FOR 
YOUR CONVENIENCE, HOWEVER, IT SHOULD BE UNDERSTOOD THAT SUCH
LIBRARIES ARE PROPERTY OF THE RESPECTIVE AUTHORS AND MAY BE 
SUBJECT TO SEPARATE LICENSE AGREEMENTS AS STIPULATED BY THEIR
OWNERS



===========================================================
                         EXAMPLE USAGE
===========================================================
import improject.IMException;
import improject.IMSession;
import improject.Message;
import improject.MessageListener;
import improject.IMSession.IMService;

public class echo {	
  public static void main(String[] args) {		
	final IMSession session = new IMSession();
		
	// Add login information of required user(s)
	session.addLogin(IMService.YAHOO, "YahooID", "YahooPass");
	session.addLogin(IMService.MSN, "MSNID", "MSNPass");
	session.addLogin(IMService.GOOGLE, "GTalkID", "GTalkPass");
	session.addLogin(IMService.SKYPE, "SkypeID", "SkypePass");
		
	//add message listener(s) for all service
	session.addMessageListener(new MessageListener() {
	  @Override
  	  public void messageReceived(Message message) {
          // Print received message
		System.out.println("Message received:");
		System.out.println(message);
				
          // Discover & print user "online?" status
		try {
			System.out.println("User is online? " + session.isOnline(message.getTo(), message.getFrom()));
		} catch (IMException e) {
			e.printStackTrace();
		}
		
		// Send reply to the same service/user it came from
		System.out.println("Sending reply...\n");

		Message replyMessage = new Message();
		replyMessage.setImService(message.getImService());
		replyMessage.setBody("Hi!");
		replyMessage.setFrom(message.getTo());
		replyMessage.setTo(message.getFrom());

		try {
			session.sendMessage(replyMessage);
		} catch (IMException e) {
			e.printStackTrace();
		}
	  }
	});
		
	// Connect and login to all added user accounts
	try { 
		session.connect();
	} catch (IMException e) {
		e.printStackTrace();
	}
		
	// Wait in the loop for incoming messages; 
	// incoming messages will trigger above 
	// MessageListener function
	while (true) {
        try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
  }
}





============================================================
DECLARATIONS

CLASS IMSession IS THE MAIN CLASS WRAPPER FOR ALL IM SERVICES 
SUPPORTED IM SERVICES ARE:
public enum IMService { YAHOO, MSN, GOOGLE, SKYPE }

public class IMSession {		
	//Constructor
	public IMSession();
	
	/* Adds login information for connections
	 * @param imService
	 * @param user
	 * @param password
	 */
	public void addLogin(IMService imService, String user, String password);
	
	/* Initialize and connects to all services, whose login information was given
	 * @throws IMException
	 */
	public void connect() throws IMException;
	
	/* Adds function handler to be called upon message arrival
	 * @param messageListener
	 */
	public void addMessageListener(MessageListener messageListener);
	
	/* Send given message
	 * Connection is searched using "From" field of message
	 * @param message
	 * @throws IMException
	 */
	public void sendMessage(Message message) throws IMException;

	/* Check that contact is online, IM service is specified by 
	 * id of the listening connection, sessionOwner
	 * @param sessionOwner
	 * @param contact
	 * @throws IMException
	 */	
	public boolean isOnline(String sessionOwner, String contact) throws IMException;

	/* Transmit messages from internal adapters to client listeners
	 * @param message
	 */
	public void messageReceived(Message message);
}



------------------------------------------------------------
CLASS Message ENCAPSULATES IM MESSAGE
public class Message {	
	// Prints out message as a string
	public String toString();

    // Get message's body
	public String getBody(); 

    // Set message's body
	public void setBody(String body);

    // Get message's From field
	public String getFrom();

    // Set message's From field
	public void setFrom(String from);

    // Get IM service of this message
	public IMService getImService();

    // Set IM service for this message
	public void setImService(IMService imService);

    // Get message's To field
	public String getTo();

    // Set message's To field
	public void setTo(String to);
}



------------------------------------------------------------
CLASS MessageListener IS INTERFACE FOR HANDLER TO BE CALLED UPON
RECEIVING OF A MESSAGE BY IM CLIENT
public interface MessageListener {
    // Function to be called on reception of messages
	public void messageReceived(Message message);
}



------------------------------------------------------------
CLASS LoginInfo ENCAPSULATES CLIENT'S LOGIN INFORMATION
public class LoginInfo {
	/* Constructor
	 * @param imService
	 * @param user
	 * @throws password
	 */	
	public LoginInfo(IMService imService, String user, String password);

    // Get this client's IM Service
	public IMService getImService();

    // Set this client's IM Service
	public void setImService(IMService imService);

    // Get this client's IM user
	public String getUser();

    // Set this client's IM user
	public void setUser(String user);

    // Get this client's password
	public String getPassword();

    // Set this client's password
	public void setPassword(String password);
}



------------------------------------------------------------
CLASS IMException IS EXCEPTION HANDLER FOR THE LIBRARY
public class IMException extends Exception {
    //Constructor
	public IMException(String message);
}





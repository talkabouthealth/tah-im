package com.tah.im;

import net.sf.jml.MsnMessenger;
import net.sf.jml.MsnProtocol;
import net.sf.jml.event.MsnMessengerAdapter;
import net.sf.jml.impl.MsnMessengerFactory;

public class testIM {
	public static void main(String[] args) throws Exception {
		IMNotifier imNotifier = IMNotifier.getInstance();
		
		Thread.sleep(5000);
		
		System.err.println("---");
		
//		imNotifier.addContact("kan.kangaroo");
		
//		System.out.println(imNotifier.isUserOnline("kan.kangaroo@hotmail.com"));
		
		

        
		
		/*String[][] data_list = {{"thero666@gmail.com", "talkabouthealth.com@gmail.com"}, 
                {"http://yahoo.com", "http://www.google.com"}};
		*/

/*		 

		IMNotifier MyNotifier = IMNotifier.getInstance();
		List<String> onlineUsers = MyNotifier.getSession().getOnlineContacts(MyNotifier.getMainAcc());
		MyNotifier.getSession().addContact(MyNotifier.getMainAcc(), "testim1122@gmail.com");
		 System.out.println("The following users from google talk are online: " + onlineUsers.size());
		 for(int i = 0; i < onlineUsers.size(); i++){
			 System.out.println(onlineUsers.get(i));
		 }
		 System.out.println("================================================ ");	
/*		
		
		 IMNotifierYahoo MyNotifierYahoo = IMNotifierYahoo.getInstance();
		 List<String> onlineUsersYahoo = MyNotifierYahoo.getSession().getOnlineContacts(MyNotifierYahoo.getMainAcc());		 
//		 MyNotifierYahoo.getSession().addContact(MyNotifierYahoo.getMainAcc(), "testim9900");
		 System.out.println("The following users from Yahoo are online: ");
		 for(int i = 0; i < onlineUsersYahoo.size(); i++){
			 System.out.println(onlineUsersYahoo.get(i));
		 }
		 System.out.println("================================================ ");
	
/*	

		 IMNotifierMSN MyNotifierMSN = IMNotifierMSN.getInstance();
		 List<String> onlineUsersMSN = MyNotifierMSN.getSession().getOnlineContacts(MyNotifierMSN.getMainAcc());
		 MyNotifierMSN.getSession().addContact(MyNotifierMSN.getMainAcc(), "elton.tsai@hotmail.com");
		 System.out.println("The following users from MSN are online: ");
		 for(int i = 0; i < onlineUsersMSN.size(); i++){
			 System.out.println(onlineUsersMSN.get(i));
		 }
		 System.out.println("================================================ ");
		 
	/*		Map<String, userInfo> ous = onlineUsersSingleton.getInstance();
			Collection collection = ous.values();
			Iterator iterator = collection.iterator();
			System.out.println("List");
			while(iterator.hasNext()){
				userInfo uI = (userInfo) iterator.next();
				System.out.println("List");
				System.out.println(uI.getEmail() + " has " + uI.getUid());
				
			}	
		 
/*		
		IMNotifier MyNotifier = new IMNotifier();

		MyNotifier.isUserOnline("testIM1122@gmail.com");
		System.out.println("Is it broadcast to all users? " + MyNotifier.Broadcast(mail_list, UID, 0));
//		MyNotifier.isUserOnline("testIM1122@gmail.com");
		//MyNotifier.Broadcast(data_list2);
	*/
		
	}
}

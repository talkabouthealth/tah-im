package com.tah.im;

import improject.Message;
import improject.IMSession.IMService;

public class testIM {
	public static void main(String[] args) throws Exception {
		/*String[][] data_list = {{"thero666@gmail.com", "talkabouthealth.com@gmail.com"}, 
                {"http://yahoo.com", "http://www.google.com"}};
		*/
        
		String[] mail_list = {"testIM1122@gmail.com"};
		
		int[] UID = {12};
		
		IMNotifier MyNotifier = new IMNotifier();

		MyNotifier.isUserOnline("testIM1122@gmail.com");
		System.out.println("Is it broadcast to all users? " + MyNotifier.Broadcast(mail_list, UID, 0));
//		MyNotifier.isUserOnline("testIM1122@gmail.com");
		//MyNotifier.Broadcast(data_list2);
	
		
	}
}

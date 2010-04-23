package com.tah.im;

public class testIM {
	public static void main(String[] args) throws Exception {
		/*String[][] data_list = {{"thero666@gmail.com", "talkabouthealth.com@gmail.com"}, 
                {"http://yahoo.com", "http://www.google.com"}};
		*/
        
		String[] mail_list = {"thero666@gmail.com", "talkabouthealth.com@gmail.com"};
		
		int[] UID = {1,5};
		
		IMNotifier MyNotifier = new IMNotifier();
								
		
		System.out.println("Is it broadcast to all users? " + MyNotifier.Broadcast(mail_list, UID, 0));
		
		//MyNotifier.Broadcast(data_list2);
	
		
	}
}

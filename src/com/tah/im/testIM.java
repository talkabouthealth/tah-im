package com.tah.im;

public class testIM {
	public static void main(String[] args) throws Exception {
		String[][] data_list = {{"thero666@gmail.com", "talkabouthealth.com@gmail.com"}, 
                {"http://yahoo.com", "http://www.google.com"}};
		
		int[] UID = {1,5};
		
		/*String[][] data_list2 = {{"thero666@gmail.com", "talkabouthealth.com@gmail.com"}, 
                {"http://www.facebook.com", "http://www.gamefaq.com"}};
		*/
		IMNotifier MyNotifier = new IMNotifier();
								
		
		MyNotifier.Broadcast(data_list, UID);
		
		//MyNotifier.Broadcast(data_list2);
	
		
	}
}

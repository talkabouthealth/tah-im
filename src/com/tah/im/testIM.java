package com.tah.im;

public class testIM {
	public static void main(String[] args) {
		String[][] data_list = {{"thero666@gmail.com", "talkabouthealth.com@gmail.com"}, 
                {"http://yahoo.com", "http://www.google.com"}}; 
		
		IMInterface MyInterface = new IMInterface();
		
		MyInterface.Broadcast(data_list);	
		
	}
}

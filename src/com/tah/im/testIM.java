package com.tah.im;

public class testIM {
	public static void main(String[] args) {
		//String [] account = { "thero666@gmail.com", "talkabouthealth.com@gmail.com" };
		String[][] data_list = {{"thero666@gmail.com", "talkabouthealth.com@gmail.com"}, 
                {"http://yahoo.com", "http://www.google.com"}}; 
		
		/*for(int i = 0; i < data_list.length; i++) { 
            for(int j = 0; j < data_list[0].length; j++) 
                System.out.print(data_list[i][j] + " "); 
             
        }*/ 
		
		//System.out.println(data_list[0][0] + data_list[0][1]);
		
		//IMInterface_b b = new IMInterface_b();
		IMInterface_b.Broadcast(data_list);		
		// IMInterface_b.Broadcast(account);		
	}
}

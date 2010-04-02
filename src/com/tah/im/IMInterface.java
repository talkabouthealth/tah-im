package com.tah.im;

import improject.IMException;
import improject.IMSession;
import improject.Message;
//import improject.MessageListener;
import improject.IMSession.IMService;


public class IMInterface {

	private IMSession session;
	private String MainAccount;
	private String MainPasswd;
	
	public IMInterface(){
		//login by this account		
		this.MainAccount = "testIM5566@gmail.com";
		this.MainPasswd = "hu183183";
		
		this.session = new IMSession();
		
		session.addLogin(IMService.GOOGLE, MainAccount, MainPasswd);
		
		try { 
			session.connect();
		} catch (IMException e) {
			e.printStackTrace();
		}			
		
		// Create Thread to keep the Main Account Online
		Thread thread = new Thread(new Runnable() {
            public void run() { 
                while(true) { 
                	 try {
         				Thread.sleep(5000);
         			} catch (InterruptedException e) {
         				e.printStackTrace();
         			}
                } 
            }        
        }); 
		thread.start();		
	}

	public void Broadcast(final String[][] Data_list) {
		
		System.out.println("Broadcast...\n");
		
		//setting Broadcast Message
		Message bMessage = new Message();
		bMessage.setImService(IMService.GOOGLE);
		bMessage.setBody("No link. Try it later.");  //default message
		bMessage.setFrom(this.MainAccount);
		
		try { 
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Sending Message
		try {
				for(int j = 0; j < Data_list[0].length; j++){
					if(session.isOnline(MainAccount, Data_list[0][j])){
						System.out.println(Data_list[0][j] + " is online. Send messages to it");
						bMessage.setTo(Data_list[0][j]);
						bMessage.setBody("Please use the link: " + Data_list[1][j]);
						session.sendMessage(bMessage);
					}
				}
		} catch (IMException e) {
			e.printStackTrace();
		}				
			
	}//end of Broadcast
}

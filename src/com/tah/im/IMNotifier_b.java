package com.tah.im;

import improject.IMException;
import improject.IMSession;
import improject.Message;
import improject.MessageListener;
import improject.IMSession.IMService;


public class IMNotifier_b{

	private IMSession session;
	private String MainAccount;
	private String MainPasswd;
	private boolean[] UserStatus;  //note that big U
	
	//Constructor: login when creating the IMInterface
	public IMNotifier_b(){
		//login by this account		
		this.MainAccount = "testIM5566@gmail.com";
		this.MainPasswd = "hu183183";
		
		this.session = new IMSession();
		
		session.addLogin(IMService.GOOGLE, MainAccount, MainPasswd);
		
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
		
		//connect 
		try { 
			session.connect();
		} catch (IMException e) {
			e.printStackTrace();
		}			
		
		// Create Thread to keep the Main Account Online
		// incoming Message will trigger Listener
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
	
	//Constructor with friend_list
	public IMNotifier_b(final String[][] Data_list){
		//login by this account		
		this.MainAccount = "testIM5566@gmail.com";
		this.MainPasswd = "hu183183";
		
		this.session = new IMSession();
		
		session.addLogin(IMService.GOOGLE, MainAccount, MainPasswd);
		
		//connect 
		try { 
			session.connect();
		} catch (IMException e) {
			e.printStackTrace();
		}			
		
		this.UserStatus = getUserStatus(Data_list);		
		
		// Create Thread to keep the Main Account Online
		// incoming Message will trigger Listener
		Thread thread = new Thread(new Runnable() {
			public void run() {
				boolean[] tempStatus;
                int[] S_table = new int[Data_list[0].length];
				while(true) { 
                	 try {
         				Thread.sleep(5000);
         				tempStatus = getUserStatus(Data_list);
         				S_table = CompareStatus(tempStatus, UserStatus);
         				for(int i=0;i<S_table.length;i++){
         					switch(S_table[i]){
         					case 1:
         						System.out.println("User "+Data_list[0][i]+" is off-line.");
         						break;
         					case -1:
         						System.out.println("User "+Data_list[0][i]+" is now online.");
         						break;
         					default:
         						;
         					}
         				}
         				UserStatus=getUserStatus(Data_list);
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
				for(int i = 0; i < Data_list[0].length; i++){
					if(session.isOnline(MainAccount, Data_list[0][i])){
						System.out.println(Data_list[0][i] + " is online. Send messages to it");
						bMessage.setTo(Data_list[0][i]);
						bMessage.setBody("Please use the link: " + Data_list[1][i]);
						session.sendMessage(bMessage);
					}
				}
		} catch (IMException e) {
			e.printStackTrace();
		}				
			
	}//end of Broadcast
	
	public boolean[] getUserStatus(final String[][] Data_list){
		boolean[] userStatus = new boolean[Data_list[0].length];  // note that small u
		
		try { 
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
				
		for(int i = 0; i < Data_list[0].length; i++){
			try {
				userStatus[i] = session.isOnline(this.MainAccount, Data_list[0][i]);
			} catch (IMException e) {
				e.printStackTrace();
			}
		}
		
		/*for(boolean b:userStatus){
			System.out.println(b);
		}*/
		
		return userStatus; 
	}
	
	public int[] CompareStatus(boolean[] b1, boolean[] b2){  //b1 is now, b2 is previous
		int[] S_table = new int[b1.length];
		
		if(b1.length==b2.length){
			for(int i=0;i<b1.length;i++){
				if(b1[i]!=b2[i]){
					if(b1[i]==false) S_table[i]=1; //change to off-line
					else S_table[i]=-1; //change to online
				}
				else{
					S_table[i]=0; //no change
				}
			}
		}
		return S_table;
	}
}

package com.tah.im;

import improject.LoginInfo;
import improject.IMSession.IMService;


public class testIM {
	
	private static final LoginInfo[] DEV_LOGIN_ARRAY = new LoginInfo[] {
		new LoginInfo(IMService.GOOGLE, "talkabouthealth.com.test@gmail.com", "CarrotCake917"),
		new LoginInfo(IMService.MSN, "talkabouthealth.com.test@hotmail.com", "CarrotCake917"),
		new LoginInfo(IMService.YAHOO, "talkabouthealthtest@ymail.com", "CarrotCake917"),
	};
 
 
	public static void main(String[] args) throws Exception {
		IMNotifier.init(DEV_LOGIN_ARRAY);
		IMNotifier imNotifier = IMNotifier.getInstance();
		
		Thread.sleep(3000);
		
//		imNotifier.broadcast(new String[]{"4c2cb43160adf3055c97d061"}, "4c5bce0aaeccbce58e8da64f");
		
//		Thread.sleep(10000);
		
//		System.in.read();
//		
//		System.err.println("---");
//		
//		IMSession imSession = imNotifier.getSession();
//		
//		Message replyMessage = new Message();
//		replyMessage.setImService(IMService.GOOGLE);
//		replyMessage.setBody("Test2222");
//		replyMessage.setFrom("talkabouthealth.com.test@gmail.com");
//		replyMessage.setTo("kan.kangaroo");
//		
//		try {
//			imSession.sendMessage(replyMessage);
//		} catch (IMException e) {
//			e.printStackTrace();
//		}
//		
//		System.err.println("-Sent!!-");
		
//		imSession.addContact("talkabouthealth.com.test@gmail.com", "kan.kangaroo@hotmail.com");
//		imNotifier.addContact("kan.kangaroo");
		
//		System.out.println(imNotifier.isUserOnline("kan.kangaroo@hotmail.com"));
		
	}
}

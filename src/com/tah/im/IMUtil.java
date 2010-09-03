package com.tah.im;

import com.tah.im.model.UserInfo;

import improject.IMSession.IMService;

public class IMUtil {
	
	//convert user (format of IMService) to UserInfo with data form TAH db
	public static UserInfo getUserInfo(String user) {
		if (user == null) {
			return null;
		}
		
		String imService = null;
		String imUsername = null;
		if (user.contains("@gmail")) {
			//Google service
			imService = "GoogleTalk";
			imUsername = removeService(user);
		}
		else if (user.contains("@live") || user.contains("@hotmail")) {
			imService = "WindowLive";
			imUsername = removeService(user);
		}
		else {
			//for now default - Yahoo
			imService = "YahooIM";
			imUsername = user;
		}
		
		//TODO: user can enter full id and we won't find it in db?
		UserInfo userInfo = DBUtil.getUserByIm(imService, imUsername);
		return userInfo;
	}
	
	//TODO move it to enum?
	public static IMService getIMServiceByName(String imService) {
		//'YahooIM', 'WindowLive', 'GoogleTalk'
		if ("GoogleTalk".equals(imService)) {
			return IMService.GOOGLE;
		}
		else if ("WindowLive".equals(imService)) {
			return IMService.MSN;
		}
		else if ("YahooIM".equals(imService)) {
			return IMService.YAHOO;
		}
		else {
			throw new IllegalArgumentException("Bad IM Service name");
		}
	}
	
	public static String prepareUsername(String imUsername, IMService imService) {
		if (imUsername.contains("@")) {
			//Yahoo doesn't need "@yahoo.com"
			if (imService == IMService.YAHOO) {
				imUsername = removeService(imUsername);
			}
		}
		else {
			if (imService == IMService.GOOGLE) {
				imUsername += "@gmail.com";
			}
			if (imService == IMService.MSN) {
				//TODO: hotmail or live?
				imUsername += "@hotmail.com";
			}
		}
		
		return imUsername;
	}
	
	private static String removeService(String imUsername) {
		int end = imUsername.indexOf("@");
		if (end != -1) {
			imUsername = imUsername.substring(0, end);
		}
		return imUsername;
	}

}

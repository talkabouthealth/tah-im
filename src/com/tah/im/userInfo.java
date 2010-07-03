package com.tah.im;

import java.util.Date;

import com.mongodb.DBObject;

public class userInfo {
	
	private String uid;
	private String uname;
	private String email;
	private String gender;
	
	public userInfo(){}
	
	public userInfo(String userEmail) {
//		String sql = "SELECT talkers.*, MAX(noti_history.noti_time) " +
//				"FROM talkers LEFT JOIN noti_history ON talkers.uid = noti_history.uid " +
//				"WHERE email = '" + userMail + "' GROUP BY talkers.uid ORDER BY MAX(noti_history.noti_time)";
		
		//TODO: do not use DBObject?
		DBObject talkerDBObject = DBUtil.getTalkerByEmail(userEmail);
		if (talkerDBObject != null) {
			uid = talkerDBObject.get("_id").toString();
			uname = (String)talkerDBObject.get("uname");
			email = (String)talkerDBObject.get("email");
			gender = (String)talkerDBObject.get("gender");
		}
	}
	
	public long numOfNoti(String uid) {
//		String sql = "SELECT COUNT(*) FROM talkers LEFT JOIN noti_history " +
//		"ON talkers.uid = noti_history.uid " +
//		"WHERE noti_history.noti_time > '" + _time + "' " +
//				"AND noti_history.uid =" + _uid + " ORDER BY noti_history.noti_time"; 
		
		long numberOfNotifications = DBUtil.getNumOfNotifications(uid);
		return numberOfNotifications;
	}
	
	public String getIMType(String uid) {
		return DBUtil.getIMType(uid);
	}
	
	public Date lastNotiTime(String uid) {
//		String sql = "SELECT talkers.*, MAX(noti_history.noti_time) FROM talkers " +
//		"LEFT JOIN noti_history ON talkers.uid = noti_history.uid " +
//		"WHERE talkers.uid = '" + _uid + "' GROUP BY talkers.uid ORDER BY MAX(noti_history.noti_time)";
		
		return DBUtil.getLastNotification(uid);
	}
	
	public boolean isExist(String _mail) {
		return DBUtil.isEmailExist(_mail);
	}
	
	public String getUid(){
		return uid;
	}

	public String getUname(){
		return uname;
	}

	public String getEmail(){
		return email;
	}
	public String getGender(){
		return gender;
	}
	
}

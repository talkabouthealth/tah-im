package com.tah.im;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class userInfo {
	
	private dbConnection con;
	private int uid;
	private String uname;
	private String email;
	private String gender;
	private Timestamp lastNotiTime;
	private int timesBeenNoti;
	
	public userInfo() throws SQLException{
	//	RS = _rs;
			con = new dbConnection();

	}
	
	public userInfo(int _uid) throws SQLException{
		String sql = "SELECT * FROM talkers WHERE uid = " + _uid;
		con = new dbConnection();
		con.setRs(sql);
		String period;
		java.util.Date date= new java.util.Date();
		period = ((new Timestamp(date.getTime())).getYear() + 1900) + "-" + ((new Timestamp(date.getTime())).getMonth() + 1) + "-" + ((new Timestamp(date.getTime())).getDate()  - 1) + " " + (new Timestamp(date.getTime())).getHours() + ":" + (new Timestamp(date.getTime())).getMinutes() + ":" + (new Timestamp(date.getTime())).getSeconds();
		while(con.getRs().next()){
			
			uname = con.getRs().getString("uname");
			email = con.getRs().getString("email");
			gender = con.getRs().getString("gender");
			timesBeenNoti = numOfNoti(con.getRs().getInt("uid"), period);
		}
		con.getRs().close();
		con.getCon().close();
	}
	public userInfo(String userMail) throws SQLException{
		String sql = "SELECT talkers.*, MAX(noti_history.noti_time) FROM talkers LEFT JOIN noti_history ON talkers.uid = noti_history.uid WHERE email = '" + userMail + "' GROUP BY talkers.uid ORDER BY MAX(noti_history.noti_time)";
		con = new dbConnection();
		con.setRs(sql);

		while(con.getRs().next()){
			
			uname = con.getRs().getString("uname");
			email = con.getRs().getString("email");
			gender = con.getRs().getString("gender");
			lastNotiTime = con.getRs().getTimestamp("MAX(noti_history.noti_time)");
		}
		con.getRs().close();
		con.getCon().close();
	}
	public int numOfNoti(int _uid, String _time) throws SQLException{
		int counter;
		counter = 0;
		
		String sql = "SELECT COUNT(*) FROM talkers LEFT JOIN noti_history ON talkers.uid = noti_history.uid WHERE noti_history.noti_time > '" + _time + "' AND noti_history.uid =" + _uid + " ORDER BY noti_history.noti_time"; 
		con.setRs(sql);
		while(con.getRs().next()){
			counter = con.getRs().getInt("COUNT(*)");
		}
		con.getRs().close();
		con.getCon().close();
		return counter;
	}
	public int getTimesBeenNoti(){
		return timesBeenNoti;
	}
	public String getUname(){
		return uname;
	}
	public Timestamp getlastNotiTime(){
		return lastNotiTime;
	}
	public String getEmail(){
		return email;
	}
	public String getGender(){
		return gender;
	}
}

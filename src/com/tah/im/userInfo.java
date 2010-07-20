package com.tah.im;


public class UserInfo {

	private String uid;
	private String uname;
	private String email;
	private String gender;
	
	private String imService;
	private String imUsername;
	
	public UserInfo() {
	}

	public UserInfo(String imService, String imUsername) {
		super();
		this.imService = imService;
		this.imUsername = imUsername;
	}
	
	public boolean isExist() {
		return uid != null;
	}

	@Override
	public String toString() {
		return imUsername+" ("+imService+")";
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getImService() {
		return imService;
	}

	public void setImService(String imService) {
		this.imService = imService;
	}

	public String getImUsername() {
		return imUsername;
	}

	public void setImUsername(String imUsername) {
		this.imUsername = imUsername;
	}

}

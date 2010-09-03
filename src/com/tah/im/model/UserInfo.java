package com.tah.im.model;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.mongodb.DBObject;
import com.tah.im.IMAccountBean;


public class UserInfo {

	private String uid;
	private String uname;
	private String email;
	private String gender;
	
	private String imService;
	private String imUsername;
	
	private Set<IMAccountBean> imAccounts;
	
	public UserInfo() {
	}

	public UserInfo(String imService, String imUsername) {
		super();
		this.imService = imService;
		this.imUsername = imUsername;
	}
	
	public void parseDBInfo(DBObject talkerDBObject) {
		if (talkerDBObject != null) {
			setUid(talkerDBObject.get("_id").toString());
			setUname((String)talkerDBObject.get("uname"));
			setEmail((String)talkerDBObject.get("email"));
			setGender((String)talkerDBObject.get("gender"));
			
			@SuppressWarnings("unchecked")
			Collection<DBObject> imAccountsDBList = (Collection<DBObject>)talkerDBObject.get("im_accounts");
			parseIMAccounts(imAccountsDBList);
		}
	}
	
	private void parseIMAccounts(Collection<DBObject> imAccountsDBList) {
		Set<IMAccountBean> imAccountsSet = new LinkedHashSet<IMAccountBean>();
		if (imAccountsDBList != null) {
			for (DBObject emailDBObject : imAccountsDBList) {
				String userName = (String)emailDBObject.get("uname");
				String service = (String)emailDBObject.get("service");
				IMAccountBean imAccount = new IMAccountBean(userName, service);
				
				imAccountsSet.add(imAccount);
			}
		}
		
		imAccounts = imAccountsSet;
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

	public Set<IMAccountBean> getImAccounts() {
		return imAccounts;
	}

	public void setImAccounts(Set<IMAccountBean> imAccounts) {
		this.imAccounts = imAccounts;
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

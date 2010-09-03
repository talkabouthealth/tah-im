package com.tah.im.model;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.mongodb.DBObject;


public class UserInfo {

	private String uid;
	private String uname;
	private String email;
	private String gender;
	
	//IM account in the current context (e.g. message received from this account)
	private IMAccount currentIMAccount;
	private Set<IMAccount> imAccounts;
	
	public UserInfo() {
	}

	public UserInfo(String imService, String imUsername) {
		super();
		currentIMAccount = new IMAccount(imUsername, imService);
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
		Set<IMAccount> imAccountsSet = new LinkedHashSet<IMAccount>();
		if (imAccountsDBList != null) {
			for (DBObject emailDBObject : imAccountsDBList) {
				String userName = (String)emailDBObject.get("uname");
				String service = (String)emailDBObject.get("service");
				IMAccount imAccount = new IMAccount(userName, service);
				
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
		return currentIMAccount.toString();
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

	public Set<IMAccount> getImAccounts() {
		return imAccounts;
	}

	public void setImAccounts(Set<IMAccount> imAccounts) {
		this.imAccounts = imAccounts;
	}

	public IMAccount getCurrentIMAccount() {
		return currentIMAccount;
	}

	public void setCurrentIMAccount(IMAccount currentIMAccount) {
		this.currentIMAccount = currentIMAccount;
	}
}

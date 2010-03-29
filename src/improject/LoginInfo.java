package improject;

import improject.IMSession.IMService;

/** 
 * Stores service type and auth information for connection
 * @author kindcoder
 */
public class LoginInfo {
	
	private IMService imService;
	private String user;
	private String password;

	public LoginInfo(IMService imService, String user, String password) {
		super();
		this.imService = imService;
		this.user = user;
		this.password = password;
	}
	public IMService getImService() {
		return imService;
	}
	public void setImService(IMService imService) {
		this.imService = imService;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

}

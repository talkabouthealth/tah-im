package improject.services;

import improject.LoginInfo;
import improject.MessageListener;

/**
 * Implements common data and methods of the services
 * @author kindcoder
 *
 */
public abstract class AbstractServiceAdapter implements ServiceAdapter {

	protected LoginInfo loginInfo;
	protected MessageListener messageListener;
	
	public AbstractServiceAdapter (LoginInfo loginInfo) {
		this.loginInfo = loginInfo;
	}
	
	@Override
	public void setMessageListener(MessageListener messageListener) {
		this.messageListener = messageListener;
	}
}

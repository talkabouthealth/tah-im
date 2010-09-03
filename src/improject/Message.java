package improject;

import java.util.Date;

import improject.IMSession.IMService;

/**
 * Message bean 
 * @author kindcoder
 */
public class Message {
	
	private String body;
	private String from;
	private String to;
	private IMService imService;
	private Date time;
	
	@Override
	public String toString() {
		return "Service: "+imService+"\n" +
			"From: "+from+"\n" +
			"To: "+to+"\n" +
			"Body: "+body+"\n";
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public IMService getImService() {
		return imService;
	}
	public void setImService(IMService imService) {
		this.imService = imService;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
}

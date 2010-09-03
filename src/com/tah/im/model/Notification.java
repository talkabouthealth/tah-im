package com.tah.im.model;

import java.util.Date;

public class Notification {
	
	public enum NotificationType {
		CONVO,
		QUESTION,
		ANSWER
	}
	
	//id and text of convo, question or answer
	private String relatedId;
	private String text;
	
	private String userName;
	
	private NotificationType type;
	private Date time;
	
	public Notification(NotificationType type) {
		this.type = type;
		this.time = new Date();
	}
	
	public String getRelatedId() {
		return relatedId;
	}
	public void setRelatedId(String relatedId) {
		this.relatedId = relatedId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public NotificationType getType() {
		return type;
	}
	public void setType(NotificationType type) {
		this.type = type;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}

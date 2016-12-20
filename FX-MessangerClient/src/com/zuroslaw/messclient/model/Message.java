package com.zuroslaw.messclient.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Message {
	
	private final StringProperty userName;
	private final StringProperty content;
	private final LongProperty id;
	private final StringProperty time;
	//TODO: StringProperty time to LocalDateTime time
	
	public Message(String userName, String content, long id, String time) {
		this.userName = new SimpleStringProperty(userName);
		this.content = new SimpleStringProperty(content);
		this.id = new SimpleLongProperty(id);
		this.time = new SimpleStringProperty(time);
	}
	
	public static Message nullMessage() {
		return new Message("", "", -1L, "");
	}
	
	public String getUserName() {
		return userName.get();
	}
	
	public String getContent() {
		return content.get();
	}
	
	public Long getId() {
		return id.get();
	}
	
	public String getTime() {
		return time.get();
	}
	
	public void setUserName(String userName) {
		this.userName.set(userName);
	}
	
	public void setContent(String content) {
		this.content.set(content);
	}
	
	public void setId(long id) {
		this.id.set(id);
	}
	
	public void setTime(String time) {
		this.time.set(time);
	}
	
	public String toString() {
		return getTime() + " | " + getUserName() + ": " + getContent();
	}
	
	public boolean isNull() {
		return (id.get() == -1L);
	}
	
}

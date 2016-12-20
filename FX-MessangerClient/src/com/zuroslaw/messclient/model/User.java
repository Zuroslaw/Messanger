package com.zuroslaw.messclient.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {
	
	protected final StringProperty userName;
	
	public User(String userName) {
		this.userName = new SimpleStringProperty(userName);
	}

	public String getUserName() {
		return userName.get();
	}
	
	public void setUserName(String userName) {
		this.userName.set(userName);
	}
	
	public StringProperty userNameProperty() {
		return userName;
	}
}

package com.zuroslaw.messclient.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FullUser extends User {
	
	private StringProperty password;
	private BooleanProperty status;
	
	public FullUser(String userName, String password, boolean status) {
		super(userName);
		this.password = new SimpleStringProperty(password);
		this.status = new SimpleBooleanProperty(status);
	}

	public String getPassword() {
		return password.get();
	}
	
	public void setPassword(String password) {
		this.password.set(password);
	}
	
	public StringProperty passwordProperty() {
		return password;
	}
	
	public boolean getStatus() {
		return status.get();
	}
	
	public void setStatus(boolean status) {
		this.status.set(status);
	}
	
	public BooleanProperty statusProperty() {
		return status;
	}
	
	//test:
	
	public String toString() {
		return userName + " " + password + " " + status;
	}
	
	/*public String getStatus() {
		return status.get();
	}
	
	public void setStatus(String status) {
		this.status.set(status);
	}
	
	public StringProperty statusProperty() {
		return status;
	}*/
}

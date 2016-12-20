package server;

import java.util.Base64;

public class User {
	
	private String name;
	/*very*/ private String password;
	//private UpdateInfo updateInfo = new UpdateInfo(false, false);
	private boolean isLogged = false;
	
	public static User nullUser() {
		return new User("@nulluser");
	}
	
	public User(String name, String password) {
		this.name = name;
		this.password = password;
	}
	
	public User(String name) {
		this.name = name;
		this.password = "";
	}
	
	public String getName() {
		return name;
	}
	
	public String getPassword() {
		return password;
	}
	
	boolean authorize(String auth64) {
		String authorizationData = auth64.split(" ")[1];
		String userData = name + ":" + password;
		return Base64.getEncoder().encodeToString(userData.getBytes()).equals(authorizationData);
	}
	
	public void setLoggedOn() {
		this.isLogged = true;
	}
	
	public void setLoggedOff() {
		this.isLogged = false;
	}
	
	public boolean isLogged() {
		return isLogged;
	}
	
	/*public setUpdateInfo(boolean hasNewMessages, boolean hasNewUsers) {
		updateInfo.set(hasNewMessages, hasNewUsers);
	}
	
	public UpdateInfo getUpdateInfo() {
		return updateInfo;
	}*/
	
}
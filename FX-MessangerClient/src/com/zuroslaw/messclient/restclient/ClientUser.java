package com.zuroslaw.messclient.restclient;

import java.util.Base64;

public class ClientUser {
	
	private String userName;
	private String authorization64;
	
	public ClientUser(String userName, String password) {
		this.userName = userName;
		authorization64 = getBase64String(userName, password);
	}
	
	public static String getBase64String(String userName, String password) {
		byte[] userbytes = (userName + ":" + password).getBytes();
		return "Basic " + Base64.getEncoder().encodeToString(userbytes);
	}
	
	public String getUserName() {
		return userName;
	}
	
	public String getAuthorization64() {
		return authorization64;
	}

	public void setUser(String userName, String password) {
		this.userName = userName;
		this.authorization64 = getBase64String(userName, password);
	}
	
	
}

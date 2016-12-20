package com.zuroslaw.messclient.restclient;

public class AuthorizationException extends Exception {

	private static final long serialVersionUID = -1802055549787716985L;
	
	public AuthorizationException(Exception e) {
		super(e);
	}
	
	public AuthorizationException(String msg) {
		super(msg);
	}
}

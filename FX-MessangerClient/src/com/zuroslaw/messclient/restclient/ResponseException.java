package com.zuroslaw.messclient.restclient;

public class ResponseException extends Exception {
	
	private static final long serialVersionUID = -1548078811237801302L;

	public ResponseException(Exception e) {
		super(e);
	}
	
	public ResponseException(String msg) {
		super(msg);
	}
	
}

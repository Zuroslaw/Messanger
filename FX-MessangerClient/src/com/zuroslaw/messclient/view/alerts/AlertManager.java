package com.zuroslaw.messclient.view.alerts;

import com.mashape.unirest.http.exceptions.UnirestException;
import java.net.MalformedURLException;
import com.zuroslaw.messclient.restclient.AuthorizationException;
import com.zuroslaw.messclient.restclient.ResponseException;

public class AlertManager {
	
	private static AlertManager instance;
	private AlertManager() {}
	public static AlertManager getInstance() {
		if (instance == null) return new AlertManager();
		return instance;
	}
	public static void raiseWithException(Exception e) {
		if (e instanceof ResponseException || e instanceof UnirestException || e instanceof MalformedURLException) {
			ResponseFailureAlert.raise();
		} else if (e instanceof AuthorizationException) {
			AuthorizationFailureAlert.raise();
		} else {
			ExceptionDefaultAlert.raise(e);
		}
	}
}

package com.zuroslaw.messclient.view.alerts;

import javafx.scene.control.Alert;

public class AuthorizationFailureAlert extends Alert {
	
	private AuthorizationFailureAlert() {
		super(AlertType.ERROR);
		this.setHeaderText("Authorization failure");
		this.setContentText("Cannot connect to server with your username and password.\n"
				+ "You may have been kicked or your account has been deleted.");
	}
	
	public static void raise() {
		AuthorizationFailureAlert authorizationFailureAlert = new AuthorizationFailureAlert();
		authorizationFailureAlert.showAndWait();
	}
}

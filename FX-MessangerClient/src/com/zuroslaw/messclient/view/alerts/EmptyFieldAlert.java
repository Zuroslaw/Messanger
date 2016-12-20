package com.zuroslaw.messclient.view.alerts;

import javafx.scene.control.Alert;

public class EmptyFieldAlert extends Alert {

	public EmptyFieldAlert() {
		super(AlertType.INFORMATION);
		this.setContentText("Username and password fields must not be empty!");
	}
	
	public static void raise() {
		EmptyFieldAlert emptyFieldAlert = new EmptyFieldAlert();
		emptyFieldAlert.showAndWait();
	}
}

package com.zuroslaw.messclient.view.alerts;

import javafx.scene.control.Alert;

public class InvalidAccountRequestAlert extends Alert {
	
	public InvalidAccountRequestAlert() {
		super(AlertType.ERROR);
		this.setHeaderText("Cannot add request!");
		this.setContentText("Choosen username is invalid or occupied.");
	}
	
	public static void raise() {
		InvalidAccountRequestAlert invalidAccountRequestAlert = new InvalidAccountRequestAlert();
		invalidAccountRequestAlert.showAndWait();
	}
}

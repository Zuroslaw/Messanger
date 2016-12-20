package com.zuroslaw.messclient.view.alerts;

import javafx.scene.control.Alert;

public class RequestAddedAlert extends Alert {

	public RequestAddedAlert() {
		super(AlertType.INFORMATION);
		this.setContentText("Account request added! Now it's time for admin to decide...");
	}
	
	public static void raise() {
		RequestAddedAlert requestAddedAlert = new RequestAddedAlert();
		requestAddedAlert.showAndWait();
	}

}

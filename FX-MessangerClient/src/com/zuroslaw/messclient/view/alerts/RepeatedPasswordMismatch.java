package com.zuroslaw.messclient.view.alerts;

import javafx.scene.control.Alert;

public class RepeatedPasswordMismatch extends Alert {

	public RepeatedPasswordMismatch() {
		super(AlertType.INFORMATION);
		this.setContentText("Repeated password does not match original password.");
	}
	
	public static void raise() {
		RepeatedPasswordMismatch repeatedPasswordMismatch = new RepeatedPasswordMismatch();
		repeatedPasswordMismatch.showAndWait();
	}
}

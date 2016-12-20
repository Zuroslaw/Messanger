package com.zuroslaw.messclient.view.alerts;

import javafx.scene.control.Alert;

public class ResponseFailureAlert extends Alert {

	private ResponseFailureAlert() {
		super(AlertType.ERROR);
		this.setHeaderText("Server response failure");
		this.setContentText("Unable to get valid response from the server. Check if the service URL"
				+ " is proper.\nIf this is not the case, probably server is offline or it's API has changed.\n\n"
				+ "To change service URL press \"Change service URL\" button.");
	}
	
	public static void raise() {
		ResponseFailureAlert responseFailureAlert = new ResponseFailureAlert();
		responseFailureAlert.showAndWait();
	}

}

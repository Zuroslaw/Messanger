package com.zuroslaw.messclient.view;

import com.zuroslaw.messclient.restclient.Requester;
import com.zuroslaw.messclient.view.alerts.AlertManager;
import com.zuroslaw.messclient.view.alerts.EmptyFieldAlert;
import com.zuroslaw.messclient.view.alerts.InvalidAccountRequestAlert;
import com.zuroslaw.messclient.view.alerts.RepeatedPasswordMismatch;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AccountRequestDialogController {
	
	@FXML
	private TextField userNameField;
	
	@FXML
	private PasswordField passwordField;

	@FXML
	private PasswordField repeatPasswordField;

	@FXML
	private Button addRequestButton;

	@FXML
	private Button cancelButton;
	
	private Stage stage;
	private boolean requestStatus = false;
	private String serviceURL;
	
	@FXML
	public void initialize() {
		
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void setServiceURL(String serviceURL) {
		this.serviceURL = serviceURL;
	}

	public boolean getRequestStatus() {
		return requestStatus;
	}
	
	public void addRequestHandler() {
		String userName = userNameField.getText();
		String password = passwordField.getText();
		String repeatPassword = repeatPasswordField.getText();
		if (userName.isEmpty() || password.isEmpty()) {
			EmptyFieldAlert.raise();
			return;
		}
		if (!password.equals(repeatPassword)) {
			RepeatedPasswordMismatch.raise();
			return;
		}
		try {
			requestStatus = Requester.requestAccount(serviceURL, userName, password);
			if (!requestStatus) {
				InvalidAccountRequestAlert.raise();
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			AlertManager.raiseWithException(e);
		}
		stage.close();
	}
	
	public void cancelHandler() {
		stage.close();
	}
}

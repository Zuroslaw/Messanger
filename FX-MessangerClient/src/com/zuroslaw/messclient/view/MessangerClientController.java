package com.zuroslaw.messclient.view;

import java.util.Optional;
import java.util.TimerTask;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.zuroslaw.messclient.MainApp;
import com.zuroslaw.messclient.model.Message;
import com.zuroslaw.messclient.model.User;
import com.zuroslaw.messclient.restclient.*;
import com.zuroslaw.messclient.view.alerts.AlertManager;
import com.zuroslaw.messclient.view.alerts.AuthorizationFailureAlert;
import com.zuroslaw.messclient.view.alerts.RequestAddedAlert;
import com.zuroslaw.messclient.view.alerts.ResponseFailureAlert;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
//import javafx.scene.layout.Pane;

public class MessangerClientController {

	@FXML
	private TableView<User> userTable;
	
	@FXML
	private TableColumn<User, String> userColumn;
	
	@FXML
	private ListView<String> messageView;
	
	@FXML
	private TextField usernameText;
	
	@FXML
	private TextField passwordText;
	
	@FXML
	private TextField messageText;
	
	@FXML
	private Button logInButton;
	
	@FXML
	private Button logOutButton;
	
	@FXML
	private Button sendButton;
	
	@FXML
	private Button setServiceURLButton;
	
	@FXML
	private Button administrationButton;
	
	@FXML
	private Button accountRequestButton;
	
	//@FXML
	//private Pane shadePane;
	
	private MainApp mainApp;
	
	private Requester requester;
	
	private boolean isLoggedIn = false;
	
	private int updateExceptionsCounter = 0;
	
	private String serviceURL = "http://localhost:8080";
	
	//private Timer updateTimer;
	
	//TEST:
	
	/*public void showLoadingPane() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				shadePane.setVisible(true);
			}
		});
	}
	
	public void hideLoadingPane() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				shadePane.setVisible(false);
			}
		});
	}*/
	
	//END TEST
	
	@FXML
	public void logInHandler() throws UnirestException {
		//showLoadingPane(); //test
		boolean isAdmin;
		try {
			requester = Requester.initializeAndGet(usernameText.getText(), passwordText.getText(), serviceURL);
			isAdmin = requester.adminAuthorize();
		} catch (AuthorizationException e) {
			requester = null;
			//hideLoadingPane();	//test
			AuthorizationFailureAlert.raise();
			return;
		} catch (Exception e) {
			requester = null;
			//hideLoadingPane();	//test
			ResponseFailureAlert.raise();
			return;
		}
		if (isAdmin) administrationButton.setVisible(true);
		accountRequestButton.setVisible(false);
		userTable.setDisable(false);
		messageView.setDisable(false);
		logOutButton.setDisable(false);
		sendButton.setDisable(false);
		messageText.setDisable(false);
		
		setServiceURLButton.setVisible(false);
		setServiceURLButton.setDisable(true);		
		logInButton.setDisable(true);
		passwordText.clear();
		usernameText.setDisable(true);
		passwordText.setDisable(true);
		
		isLoggedIn = true;
		mainApp.setRequester(this.requester);
		
		mainApp.resetTimer();
		mainApp.getTimer().scheduleAtFixedRate(new TimerTask() {
	        @Override
	        public void run() {
	        	if (isLoggedIn)
	        		updateAll();
	        		//if(mainApp.getMessages().size() > 0) messageView.scrollTo(mainApp.getMessages().size() - 1);
	        		//messageView.getSelectionModel().select(mainApp.getMessages().size()-1);
	        }
	    }, 0, 500);
		//hideLoadingPane();	//test
	}
	
	@FXML
	public void logOutHandler() {
		isLoggedIn = false;
		
		mainApp.resetTimer();
		
		administrationButton.setVisible(false);
		accountRequestButton.setVisible(true);
		userTable.setDisable(true);
		messageView.setDisable(true);
		logOutButton.setDisable(true);
		sendButton.setDisable(true);
		messageText.setDisable(true);
		
		setServiceURLButton.setVisible(true);
		setServiceURLButton.setDisable(false);
		logInButton.setDisable(false);
		passwordText.clear();
		usernameText.setDisable(false);
		passwordText.setDisable(false);
		
		mainApp.resetData();
		userTable.setItems(mainApp.getUsers());
		messageView.setItems(mainApp.getStringMessages());
		
		requester.logOff();
	}
	
	@FXML
	public void sendHandler() throws UnirestException {
		String messageContent = messageText.getText();
		if (messageContent.isEmpty()) return;
		messageText.clear();
		requester.postMessage(messageContent);
		updateAll();
	}
	
	public void updateUsers() throws Exception {
		User[] users = requester.getUsers();
		if (users == null) return;
		if (users.length != 0) {
			mainApp.getUsers().clear();
			for (User usr : users) {
				mainApp.getUsers().add(usr);
			}
		}
	}
	
	public void showControlPanel() {
		mainApp.showControlPanel(requester);
	}
	
	public void showAccountRequestDialog() {
		boolean requestAdded = mainApp.showAccountRequestDialog(this.serviceURL);
		if (requestAdded) RequestAddedAlert.raise();
	}
	
	public void updateMessages() throws UnirestException {
		Message[] newMessages = requester.getNewMessages();
		
		if (newMessages.length != 0) {
			for (Message msg : newMessages) {
				mainApp.getMessages().add(msg);
			}
			messageView.setItems(mainApp.getStringMessages());
			/*if (mainApp.getMessages().size() > 0)
				messageView.scrollTo(mainApp.getMessages().size() - 1);*/
		}
	}
	
	public void updateAll() {
		try {
			updateMessages();
			updateUsers();
			updateExceptionsCounter = 0;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(updateExceptionsCounter);
			//if(updateExceptionsCounter++ >= 3) {
				Task<Void> raiseAlertTask = new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								AlertManager.raiseWithException(e);
							}
						});
						return null;
					}
				};
				raiseAlertTask.run();
				logOutHandler();
			};
		//}
	}
	
	public void messageTextOnEnter(ActionEvent e) throws UnirestException {
		sendHandler();
	}
	
	public void loggingTextOnEnter(ActionEvent e) throws UnirestException {
		logInHandler();
	}

	public MessangerClientController() {
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		
		userTable.setItems(mainApp.getUsers());
		messageView.setItems(mainApp.getStringMessages());
	}
	
	public void promptForServiceURL() {
		TextInputDialog ServiceURLDialog = new TextInputDialog(serviceURL);
		ServiceURLDialog.setTitle("Configuration");
		ServiceURLDialog.setHeaderText("Set the REST Server URL");
		ServiceURLDialog.setContentText("Service URL:");
		Optional<String> ServiceURLDialogResult = ServiceURLDialog.showAndWait();
		serviceURL = ServiceURLDialogResult.orElse(serviceURL);
	}
	
	@FXML
	public void initialize() throws Exception {
		userColumn.setCellValueFactory(user ->
										user.getValue().userNameProperty());
		accountRequestButton.setVisible(true);
		userTable.setDisable(true);
		messageView.setDisable(true);
		logOutButton.setDisable(true);
		sendButton.setDisable(true);
		messageText.setDisable(true);
		setServiceURLButton.setDisable(false);
		promptForServiceURL();
	}
	
}

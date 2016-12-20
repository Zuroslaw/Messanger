package com.zuroslaw.messclient.view;

import java.util.Timer;
import java.util.TimerTask;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.zuroslaw.messclient.model.FullUser;
import com.zuroslaw.messclient.restclient.Requester;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ControlPanelController {
	
	@FXML
	private TableView<FullUser> userTable;
	
	@FXML
	private TableColumn<FullUser, String> userNameColumn;
	
	@FXML
	private TableColumn<FullUser, Boolean> userStatusColumn;
	
	@FXML
	private Button kickButton;
	
	@FXML
	private Button deleteButton;
	
	@FXML
	private TextField userNameField;
	
	@FXML
	private TextField passwordField;
	
	@FXML
	private Button addButton;
	
	@FXML
	private Label addStatusLabel;
	
	@FXML
	private TableView<FullUser> userRequestsTable;
	
	@FXML
	private TableColumn<FullUser, String> userRequestsColumn;
	
	@FXML
	private Button addFromRequestButton;
	
	@FXML
	private Button rejectRequestButton;
	
	
	private Requester requester;
	private Timer controlPanelTimer = new Timer();
	
	private Stage stage;

	private ObservableList<FullUser> fullUserList = FXCollections.observableArrayList();
	private ObservableList<FullUser> userRequestsList = FXCollections.observableArrayList();
	
	@FXML
	public void initialize() {
		//System.out.println("hejo");
		userTable.setItems(fullUserList);
		userTable.getSortOrder().add(userStatusColumn);
		userNameColumn.setCellValueFactory( user -> user.getValue().userNameProperty() );
		userStatusColumn.setCellValueFactory( user -> user.getValue().statusProperty() );
		userStatusColumn.setCellFactory( col -> {
			return new TableCell<FullUser, Boolean>() {
				@Override
				protected void updateItem(Boolean item, boolean empty) {
					super.updateItem(item, empty);
					
					if( item == null || empty) {
						setText(null);
						setStyle("");
					} else {
						setText(item ? "Online" : "Offline");
						setTextFill(item ? Color.GREEN : Color.RED);
					}
					
				}
			};
		});
		
		userRequestsTable.setItems(userRequestsList);
		userRequestsColumn.setCellValueFactory( user -> user.getValue().userNameProperty() );
	}
	
	@FXML
	public void kickHandler() throws UnirestException {
		FullUser user = userTable.getSelectionModel().getSelectedItem();
		if (user == null) return;
		requester.kickUser(user.getUserName());
		updateUsers();
	}
	
	@FXML
	public void deleteHandler() throws UnirestException {
		FullUser user = userTable.getSelectionModel().getSelectedItem();
		if (user == null) return;
		
		Alert deleteAlert = new Alert(AlertType.CONFIRMATION);
		deleteAlert.setHeaderText("Delete user?");
		deleteAlert.setContentText("Are you sure you want to delete account with username \"" + user.getUserName() + "\"?");
		if (deleteAlert.showAndWait().get() != ButtonType.OK) return;
		requester.deleteUser(user.getUserName());
		updateUsers();
	}
	
	@FXML
	public void addHandler() throws UnirestException {
		boolean requestStatus = requester.addUser(userNameField.getText(), passwordField.getText());
		Timer statusShowTimer = new Timer();
		if (requestStatus) {
			addStatusLabel.setText("Added!");
			addStatusLabel.setTextFill(Color.GREEN);
			addStatusLabel.setVisible(true);
			statusShowTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					addStatusLabel.setVisible(false);
				}
			}, 2000);
			userNameField.clear();
			passwordField.clear();
			updateUsers();
		} else {
			addStatusLabel.setText("Can't add!");
			addStatusLabel.setTextFill(Color.RED);
			addStatusLabel.setVisible(true);
			statusShowTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					addStatusLabel.setVisible(false);
				}
			}, 2000);
		}
	}
	
	@FXML
	public void addFromRequestHandler() throws UnirestException {
		FullUser user = userRequestsTable.getSelectionModel().getSelectedItem();
		if (user == null) return;
		/*boolean requestStatus = */requester.addUserFromRequest(user.getUserName());
		updateUsers();
		//TODO: check request status
	}
	
	@FXML
	public void rejectRequestHandler() throws UnirestException {
		FullUser user = userRequestsTable.getSelectionModel().getSelectedItem();
		if (user == null) return;
		requester.rejectRequest(user.getUserName());
		updateUsers();
	}
	
	/*public void updateButtons() {
		int itemIndex = userTable.getSelectionModel().getSelectedIndex();
		if (itemIndex < 0) {
			kickButton.setDisable(true);
			deleteButton.setDisable(true);
		} else {
			kickButton.setDisable(false);
			deleteButton.setDisable(false);
		}
	}*/
	
	public void updateUsers() {
		int currentUserTableIndex = userTable.getSelectionModel().getSelectedIndex();
		int currentRequestTableIndex = userRequestsTable.getSelectionModel().getSelectedIndex();
		try {
			FullUser[] userRequestsArray = requester.getUserRequests();
			FullUser[] fullUserArray = requester.getFullUsers();
			fullUserList.clear();
			userRequestsList.clear();
			for (FullUser usr : fullUserArray) fullUserList.add(usr);
			for (FullUser usr : userRequestsArray) userRequestsList.add(usr);
			userTable.getSelectionModel().select(currentUserTableIndex);
			userRequestsTable.getSelectionModel().select(currentRequestTableIndex);
		} catch (Exception e) {
			e.printStackTrace();
			stage.close();
		}
	}
	
	public void setRequester(Requester requester) {
		this.requester = requester;
		try {
			updateUsers();
		} catch (Exception e) {
			e.printStackTrace();
			stage.close();
		}
		controlPanelTimer.scheduleAtFixedRate(new TimerTask() {
	        @Override
	        public void run() {
	        	updateUsers();
	        }
	    }, 0, 2000);
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				controlPanelTimer.cancel();
				fullUserList.clear();
			}
		});
	}
}

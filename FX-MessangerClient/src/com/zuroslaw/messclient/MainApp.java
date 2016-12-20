package com.zuroslaw.messclient;

import java.io.IOException;
import java.util.Timer;
import com.zuroslaw.messclient.model.Message;
import com.zuroslaw.messclient.model.User;
import com.zuroslaw.messclient.restclient.Requester;
import com.zuroslaw.messclient.view.AccountRequestDialogController;
import com.zuroslaw.messclient.view.ControlPanelController;
import com.zuroslaw.messclient.view.MessangerClientController;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;
	private Requester requester;
	
	private Timer timer = new Timer();
	
	private ObservableList<User> userList = FXCollections.observableArrayList();
	private ObservableList<Message> messageList = FXCollections.observableArrayList();
	
	public void resetData() {
		userList = FXCollections.observableArrayList();
		messageList = FXCollections.observableArrayList();
	}
	
	public void setRequester(Requester requester) {
		this.requester = requester;
	}
	
	//MOCK:
	public MainApp() {}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Messanger Client");
		
		initRootLayout();
		initMessangerClient();
	}
	
	public void initRootLayout() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
		this.rootLayout = (BorderPane) loader.load();
		
		primaryStage.setScene(new Scene(rootLayout));
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				timer.cancel();
				if (Requester.checkRequesterReadiness(requester)) {
					requester.logOff();
				}
			}
		});
		primaryStage.show();
	}

	public void initMessangerClient() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/MessangerClient.fxml"));
		
		this.rootLayout.setCenter((AnchorPane) loader.load());
		
		MessangerClientController controller = loader.getController();
		controller.setMainApp(this);
	}
	
	public void showControlPanel(Requester requester) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/ControlPanel.fxml"));
			AnchorPane pane = (AnchorPane) loader.load();
			
			Stage controlPanelStage = new Stage();
			controlPanelStage.setTitle("Control Panel");
			controlPanelStage.initModality(Modality.NONE);
			controlPanelStage.initOwner(primaryStage);
			Scene controlPanelScene = new Scene(pane);
			controlPanelStage.setScene(controlPanelScene);
			
			ControlPanelController controller = loader.getController();
			controller.setStage(controlPanelStage);
			controller.setRequester(requester);
			
			controlPanelStage.showAndWait();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean showAccountRequestDialog(String serviceURL) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/AccountRequestDialog.fxml"));
			AnchorPane pane = (AnchorPane) loader.load();
			
			Stage AccountRequestDialogStage = new Stage();
			AccountRequestDialogStage.setTitle("Request account");
			AccountRequestDialogStage.initModality(Modality.WINDOW_MODAL);
			AccountRequestDialogStage.initOwner(primaryStage);
			Scene AccountRequestDialogScene = new Scene(pane);
			AccountRequestDialogStage.setScene(AccountRequestDialogScene);
			
			AccountRequestDialogController controller = loader.getController();
			controller.setStage(AccountRequestDialogStage);
			controller.setServiceURL(serviceURL);
			AccountRequestDialogStage.showAndWait();
			
			return controller.getRequestStatus();
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public ObservableList<User> getUsers() {
		return userList;
	}
	
	public ObservableList<String> getStringMessages() {
		ObservableList<String> stringMessages = FXCollections.observableArrayList();
		for (Message m : messageList) stringMessages.add(m.toString());
		return stringMessages;
	}
	
	public ObservableList<Message> getMessages() {
		return messageList;
	}
	
	public Timer getTimer() {
		return timer;
	}
	
	public void resetTimer() {
		timer.cancel();
		timer = new Timer();
	}

	
}

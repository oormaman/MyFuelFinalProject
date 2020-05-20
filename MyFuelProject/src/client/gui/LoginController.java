package client.gui;

import java.io.IOException;

import com.google.gson.JsonObject;

import client.controller.ClientUI;
import client.controller.ObjectContainer;
import entitys.Message;
import entitys.enums.MessageType;
import entitys.enums.UserPermission;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private Pane mainLoginPane;

    @FXML
    private ImageView imgLoginBackground;

    @FXML
    private Pane loginPane;

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private Label lblLoginTitle;

    @FXML
    private ImageView imgUser;

    @FXML
    private ImageView imgPass;

    @FXML
    private Button btnMinimize;

    @FXML
    private Button btnExit;

    //************ LOCAL VARIABLE ************
    private UserPermission userPermission;
    
    @FXML
    void onExit(ActionEvent event) {
    	//TODO - close connection from server here.
    	ObjectContainer.loginStage.close();
    }

    @FXML
    void onLogin(ActionEvent event) {
    	String userName = txtUsername.getText().trim();
    	String password = txtPassword.getText().trim();
    	
    	if(userName.isEmpty() || password.isEmpty()) {
    		System.out.println("Please fill all fields");
    	}else {
    		boolean isValid = checkIfFieldsAreCorrect(userName, password);
    		if(isValid) {
    			MoveToHomeForm();
    		}else {
    			System.out.println("user name or password or incorrect..");
    		}
    	}
    }

    public void MoveToHomeForm() {
		
    	
    	if(ObjectContainer.mainFormController == null) {
    		ObjectContainer.mainFormController = new MainFormController();
    	}
    	ObjectContainer.mainFormController.start(userPermission);
	}

	@FXML
    void onMinimize(ActionEvent event) {
    	ObjectContainer.loginStage.setIconified(true);
    }

    public Pane getMainPane() {
    	return mainLoginPane;
    }
    
	public void start(Stage primaryStage) throws IOException {
		FXMLLoader loader = new  FXMLLoader();
    	loader.setLocation(getClass().getResource("LoginForm.fxml"));
    	
    	Pane root = loader.load();
		ObjectContainer.loginController = loader.getController();
		Pane p = ObjectContainer.loginController.getMainPane();
		ObjectContainer.allowDrag(p, ObjectContainer.loginStage);
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public boolean checkIfFieldsAreCorrect(String userName, String password) {
		boolean isCorrect = false;
		
		JsonObject json = new JsonObject();
		json.addProperty("userName", userName);
		json.addProperty("password", password);
		
		Message msg = new Message(MessageType.CHECK_LOGIN,json.toString());
		ClientUI.clientController.handleMessageFromClient(msg);
		
		Message response = ObjectContainer.currentMessageFromServer;
		JsonObject responseJson = response.getMessageAsJsonObject();
		
		if(responseJson.get("isValid").getAsBoolean()) {
			isCorrect = true;
			userPermission = UserPermission.stringToEnumVal(responseJson.get("permission").getAsString());
		}
		return isCorrect;
	}
}

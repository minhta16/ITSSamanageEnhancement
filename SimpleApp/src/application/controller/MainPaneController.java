package application.controller;

import org.controlsfx.control.textfield.TextFields;

import application.SamanageRequests;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MainPaneController {

	private Stage mainWindow;
	
	@FXML
	private TextArea userTokenField;	
	@FXML
	private TextField incidentNameField;	
	@FXML
	private TextField descField;	
	@FXML
	private TextField timeTrackCmtField;
	@FXML
	private TextField timeElapsedField;
	@FXML
	private TextField userInputField;
	@FXML
	private Button submitBtn;
	
	public void setStageAndSetupListeners(Stage primaryStage) {
		mainWindow = primaryStage;
		TextFields.bindAutoCompletion(userInputField, "");
	}
	
	@FXML
	private void handleSubmitBtn() {
		submitBtn.setText("Loading...");
		submitBtn.setDisable(true);
		new Thread(() -> {
		newIncidentWithTimeTrack(userTokenField.getText(), incidentNameField.getText(), descField.getText(),
				timeTrackCmtField.getText(), Integer.parseInt(timeElapsedField.getText()));
		}).start();
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Incident created!");
		alert.setHeaderText("Incident created!");
		alert.showAndWait();
		submitBtn.setText("Submit");
		submitBtn.setDisable(false);
	}
	
	public void newIncidentWithTimeTrack(String userToken, String incidentName, String description, String trackCmt, int minutesTaken) {
		SamanageRequests.newIncident(userToken, incidentName, description);
		int incidentID = SamanageRequests.getID(userToken);
		SamanageRequests.addTimeTrack(userToken, incidentID, trackCmt, minutesTaken);
		SamanageRequests.updateState(userToken, incidentID, "Closed");
	}
	
	

}

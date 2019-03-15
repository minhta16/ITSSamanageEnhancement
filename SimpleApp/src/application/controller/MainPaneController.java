package application.controller;

import org.controlsfx.control.textfield.TextFields;

import application.SamanageRequests;
import application.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
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
	@FXML
	private Button addEmailBtn;
	@FXML
	private TableView<User> infoTable;
	
	public void setStageAndSetupListeners(Stage primaryStage) {
		mainWindow = primaryStage;
		
		// setup TextFields autocomplete
		setupEmailAutoComplete();
		
		// setup infoTable
		infoTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));
		infoTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("email"));
	}
	
	@FXML
	private void handleSubmitBtn() {
		submitBtn.setText("Loading...");
		submitBtn.setDisable(true);
		new Thread(() -> {
		newIncidentWithTimeTrack(userTokenField.getText(), incidentNameField.getText(), descField.getText(),
				timeTrackCmtField.getText(), Integer.parseInt(timeElapsedField.getText()));
		}).start();
		showAlert("Incident created!", "Incident created!", AlertType.INFORMATION);
		submitBtn.setText("Submit");
		submitBtn.setDisable(false);
	}
	
	@FXML
	private void handleAddEmailButton() {
		User user;
		try {
			user = SamanageRequests.getUserByEmail(userTokenField.getText(), userInputField.getText());
			System.err.println(user);
			addTableItem(user);
		} catch (Exception e) {
			showAlert("Error", "User doesn't exists.", AlertType.ERROR);
		}
	}
	
	private void newIncidentWithTimeTrack(String userToken, String incidentName, String description, String trackCmt, double minutesTaken) {
		SamanageRequests.newIncident(userToken, incidentName, description);
		String incidentID = SamanageRequests.getID(userToken);
		SamanageRequests.addTimeTrack(userToken, incidentID, trackCmt, minutesTaken);
		SamanageRequests.updateState(userToken, incidentID, "Closed");
	}
	
	private void showAlert(String title, String message, AlertType alertType) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(message);
		alert.showAndWait();
	}
	
	private void addTableItem(User user) {
		infoTable.getItems().add(user);
	}
	
	private void setupEmailAutoComplete() {
		TextFields.bindAutoCompletion(userInputField, "");
	}

}

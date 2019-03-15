package application.controller;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.controlsfx.control.textfield.TextFields;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;

import application.data.AppSession;
import application.data.SamanageRequests;
import application.data.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class MainPaneController {

	private Stage mainWindow;

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

	@FXML
	private TextArea userTokenField;
	
	public void setStageAndSetupListeners(Stage primaryStage) throws FileNotFoundException {
		mainWindow = primaryStage;

		AppSession.getSession().loadData("data/data.json");
		userTokenField.setText(AppSession.getSession().getUserToken());
		// setup TextFields autocomplete
		setupEmailAutoComplete();

		// setup infoTable
		infoTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));
		infoTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("email"));
		infoTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("time"));
		infoTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("comment"));
		infoTable.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("removeBtn"));
	}

	@FXML
	private void handleSubmitBtn() {
		submitBtn.setText("Loading...");
		submitBtn.setDisable(true);
		new Thread(() -> {
			newIncidentWithTimeTrack(userTokenField.getText(), incidentNameField.getText(), descField.getText());
		}).start();
		showAlert("Incident created", "Incident created", AlertType.INFORMATION);
		clearInputFields();
		submitBtn.setText("Submit");
		submitBtn.setDisable(false);
	}

	@FXML
	private void handleAddEmailButton() {
		if (!AppSession.getSession().containTrackedUser(userInputField.getText())) {
			if (userInputField.getText().equals("")) {
				showAlert("Error", "Email empty", AlertType.ERROR);
			} else if (timeElapsedField.getText().equals("")) {
				showAlert("Error", "Time elapsed empty", AlertType.ERROR);
			} else {
				User user;
				user = SamanageRequests.getUserByEmail(userTokenField.getText(), userInputField.getText());
				System.err.println(user);
				if (user == null) {
					showAlert("Error", "User doesn't exists", AlertType.ERROR);
				} else {
					addTableItem(user);
					userInputField.clear();
					timeTrackCmtField.clear();
					timeElapsedField.clear();
				}
			}
		}
	}

	private void newIncidentWithTimeTrack(String userToken, String incidentName, String description) {
		SamanageRequests.newIncident(userToken, incidentName, description);
		String incidentID = SamanageRequests.getID(userToken);
		ArrayList<User> trackedUsers = AppSession.getSession().getTrackedUsers();
		for (User user: trackedUsers) {
			SamanageRequests.addTimeTrack(userToken, incidentID, user.getComment(), user.getID(), user.getTime());
		}
		SamanageRequests.updateState(userToken, incidentID, "Closed");
		
		// clear the UI
		AppSession.getSession().clearTrackedUsers();
		
	}

	private void showAlert(String title, String message, AlertType alertType) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(message);
		alert.showAndWait();
	}

	private void addTableItem(User user) {
		user.setComment(timeTrackCmtField.getText());
		user.setTime(Integer.parseInt(timeElapsedField.getText()));
		user.getRemoveBtn().setOnAction((e) -> {
			AppSession.getSession().removeTrackedUser(user.getEmail());
			infoTable.getItems().remove(user);
		});
		infoTable.getItems().add(user);
		AppSession.getSession().addTrackedUser(user);
	}

	private void setupEmailAutoComplete() {
		TextFields.bindAutoCompletion(userInputField, "");
	}

	private void clearInputFields() {
		incidentNameField.clear();
		descField.clear();
		infoTable.getItems().clear();
	}
	
	@FXML
	private void handleUserTokenFieldChange() throws JsonIOException, IOException {
		AppSession.getSession().setUserToken(userTokenField.getText());
		AppSession.getSession().saveData("data/data.json");
	}
}

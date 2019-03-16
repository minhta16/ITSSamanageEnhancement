package application.controller;

import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.JsonIOException;

import application.data.AppSession;
import application.data.SamanageRequests;
import application.data.User;
import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class MainPaneController {

	@FXML
	private ChoiceBox<String> statesChoiceBox;
	@FXML
	private ChoiceBox<String> catChoiceBox;
	@FXML
	private ChoiceBox<String> subcatChoiceBox;
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
	private SuggestionProvider<String> provider;
	@FXML
	private Button submitBtn;
	@FXML
	private Button addEmailBtn;
	@FXML
	private TableView<User> infoTable;

	@FXML
	private TextArea userTokenField;

	public void setStageAndSetupListeners(Stage primaryStage) {

		try {
			AppSession.getSession().loadData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		userTokenField.setText(AppSession.getSession().getUserToken());
		
		// setup state
		setupStatesChoiceBox();
		
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
		if (userTokenField.getText().trim().equals("")) {
			showAlert("Error", "User Token missing", AlertType.ERROR);
		} else if (incidentNameField.getText().equals("")) {
			showAlert("Error", "Incident name missing", AlertType.ERROR);
		} else {
			submitBtn.setText("Loading...");
			submitBtn.setDisable(true);
			new Thread(() -> {
				newIncidentWithTimeTrack(AppSession.getSession().getUserToken(), incidentNameField.getText(),
						descField.getText());
			}).start();
			showAlert("Incident created", "Incident created", AlertType.INFORMATION);
			clearInputFields();
			submitBtn.setText("Submit");
			submitBtn.setDisable(false);
		}
	}

	@FXML
	private void handleAddEmailButton() {
		if (!AppSession.getSession().containTrackedUser(userInputField.getText())) {
			if (userInputField.getText().equals("")) {
				showAlert("Error", "Email empty", AlertType.ERROR);
			} else if (timeElapsedField.getText().equals("")) {
				showAlert("Error", "Time elapsed empty", AlertType.ERROR);
			} else if (timeTrackCmtField.getText().equals("")) {
				showAlert("Error", "Time track comment empty", AlertType.ERROR);
			} else if (userTokenField.getText().trim().equals("")) {
				showAlert("Error", "User Token missing", AlertType.ERROR);
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
		for (User user : trackedUsers) {
			SamanageRequests.addTimeTrack(userToken, incidentID, user.getComment(), user.getID(), user.getTime());
		}
		SamanageRequests.updateState(userToken, incidentID, statesChoiceBox.getValue());

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

		
		try {
			AppSession.getSession().addTrackedUser(user);
		} catch (JsonIOException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		provider.clearSuggestions();
		provider.addPossibleSuggestions(AppSession.getSession().getSavedEmails());
	}

	private void setupEmailAutoComplete() {
		provider = SuggestionProvider.create(AppSession.getSession().getSavedEmails());
		new AutoCompletionTextFieldBinding<>(userInputField, provider);
	}

	private void setupStatesChoiceBox() {
		statesChoiceBox.getItems().addAll(AppSession.getSession().getStates());
	}

	private void clearInputFields() {
		incidentNameField.clear();
		descField.clear();
		infoTable.getItems().clear();
	}

	@FXML
	private void handleUserTokenFieldChange() {
		AppSession.getSession().setUserToken(userTokenField.getText());
		try {
			AppSession.getSession().saveData();
		} catch (JsonIOException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

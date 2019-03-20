package application.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.controlsfx.control.textfield.TextFields;

import com.google.gson.JsonIOException;

import application.data.AppSession;
import application.data.SamanageRequests;
import application.data.User;
import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class MainPaneController {

	@FXML
	private ChoiceBox<String> statesChoiceBox;
	@FXML
	private ComboBox<String> catChoiceBox;
	@FXML
	private ComboBox<String> subcatChoiceBox;
	@FXML
	private ChoiceBox<String> priorityChoiceBox;
	@FXML
	private ChoiceBox<String> assigneeChoiceBox;
	@FXML
	private ComboBox<String> deptChoiceBox;
	@FXML
	private ComboBox<String> siteChoiceBox;
	@FXML
	private DatePicker datePicker;
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	@FXML
	private TextField incidentNameField;
	@FXML
	private TextArea descField;
	@FXML
	private TextField timeTrackCmtField;
	@FXML
	private TextField timeElapsedField;
	@FXML
	private TextField userInputField;
	private SuggestionProvider<String> provider;
	@FXML
	private TextField requesterField;
	private SuggestionProvider<String> requesterProvider;
	@FXML
	private TextField assigneeField;
	private SuggestionProvider<String> assigneeProvider;
	@FXML
	private Button submitBtn;
	@FXML
	private Button addEmailBtn;
	@FXML
	private Button updateDataBtn;
	@FXML
	private TableView<User> infoTable;

	@FXML
	private TextArea userTokenField;
	@FXML
	private TextField domainField;
	@FXML
	private TextField defaultAssigneeField;
	@FXML
	private TextField defaultRequesterField;
	
	private boolean isUpToDate;
	
	@SuppressWarnings("serial")
	private final Map<Integer, String> calendar = new HashMap<Integer, String>() {{
		put(1,"Jan"); put(2,"Feb"); put(3,"Mar"); put(4,"Apr"); put(5,"May"); put(6,"June"); 
		put(7,"July"); put(8,"Aug"); put(9,"Sept"); put(10,"Oct"); put(11,"Nov"); put(12, "Dec");
		}};
		

	public void setStageAndSetupListeners(Stage primaryStage) {

		try {
			AppSession.getSession().loadData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// setup setting page
		setupSettingTab();
		
		// setup category
		setupCatChoiceBox();
		
		// setup state
		setupStatesChoiceBox();
		
		// setup priority
		setupPriorityChoiceBox();
		
		// setup date picker
		initializeDatePicker();
		
		// setup dept and site
		setupDeptAndSiteChoiceBox();
		
		// setup TextFields autocomplete
		setupEmailAutoComplete();
		
		isUpToDate = AppSession.getSession().isUpToDate();

		// setup infoTable
		infoTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));
		infoTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("email"));
		infoTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("time"));
		infoTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("comment"));
		infoTable.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("removeBtn"));
	}
	
	public void showPrompt() {
		if (!isUpToDate) {
			showAlert("Database outdated", "Database is outdated. Please update in Settings", AlertType.WARNING);
		}
	}
	
	private void setupSettingTab() {
		userTokenField.setText(AppSession.getSession().getUserToken());
		domainField.setText(AppSession.getSession().getDefaultDomain());
		defaultAssigneeField.setText(AppSession.getSession().getDefaultAssignee());
		defaultRequesterField.setText(AppSession.getSession().getDefaultRequester());
	}
	
	private void setupCatChoiceBox() {
		AppSession.getSession().setCategories(AppSession.getSession().getCategories());
		catChoiceBox.getSelectionModel().select(0);
		subcatChoiceBox.getSelectionModel().select(0);
		subcatChoiceBox.setDisable(true);
		catChoiceBox.getItems().addAll(AppSession.getSession().getCategories().keySet());
		catChoiceBox.getSelectionModel().selectedItemProperty()
	    		.addListener( (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
	    			updateSubcatChoiceBox();
	    		});
	}
	
	private void updateSubcatChoiceBox() {
		subcatChoiceBox.getItems().clear();
		
		if (AppSession.getSession().getCategories().get(catChoiceBox.getValue()).isEmpty()) {
			subcatChoiceBox.setDisable(true);
		} else {
			subcatChoiceBox.setDisable(false);
			subcatChoiceBox.getItems().addAll(AppSession.getSession().getCategories().get(catChoiceBox.getValue()));
		}		
	}
	
	private void setupStatesChoiceBox() {
		statesChoiceBox.getItems().addAll(AppSession.getSession().getStates());
		statesChoiceBox.getSelectionModel().select(0);
	}

	private void setupPriorityChoiceBox() {
		priorityChoiceBox.getItems().addAll(AppSession.getSession().getPriorities());
		priorityChoiceBox.getSelectionModel().select(2);
	}
	
	private void initializeDatePicker() {
		datePicker.setValue(LocalDate.now());
		datePicker.setConverter(new StringConverter<LocalDate>() {
			@Override
			public String toString(LocalDate t) {
				if (t != null) {
					return formatter.format(t);
				}
				return null;
			}

			@Override
			public LocalDate fromString(String string) {
				if (string != null && !string.trim().isEmpty()) {
					return LocalDate.parse(string, formatter);
				}
				return null;
			}
		});
	}
	
	private void setupDeptAndSiteChoiceBox() {
		deptChoiceBox.getItems().addAll(AppSession.getSession().getDepartments());
		deptChoiceBox.getSelectionModel().select(AppSession.getSession()
				.getDepartments().indexOf(AppSession.getSession().getRequesterInfo().getDept()));
		TextFields.bindAutoCompletion(deptChoiceBox.getEditor(), deptChoiceBox.getItems());
		
		siteChoiceBox.getItems().addAll(AppSession.getSession().getSites());
		siteChoiceBox.getSelectionModel().select(AppSession.getSession()
				.getSites().indexOf(AppSession.getSession().getRequesterInfo().getSite()));
		TextFields.bindAutoCompletion(siteChoiceBox.getEditor(), siteChoiceBox.getItems());
	}
	
	private void setupEmailAutoComplete() {
		provider = SuggestionProvider.create(AppSession.getSession().getSavedEmails());
		new AutoCompletionTextFieldBinding<>(userInputField, provider);
		assigneeField.setText(AppSession.getSession().getDefaultAssignee());
		assigneeProvider = SuggestionProvider.create(AppSession.getSession().getAssigneeEmails());
		new AutoCompletionTextFieldBinding<>(assigneeField, assigneeProvider);
		requesterField.setText(AppSession.getSession().getDefaultRequester());
		requesterProvider = SuggestionProvider.create(AppSession.getSession().getSavedEmails());
		new AutoCompletionTextFieldBinding<>(requesterField, requesterProvider);
	}
	
	@FXML
	private void handleSubmitBtn() {
		if (userTokenField.getText().trim().equals("")) {
			showAlert("Error", "Please enter user token (Setting)", AlertType.WARNING);
		} else if (incidentNameField.getText().equals("")) {
			showAlert("Error", "Please enter incident name", AlertType.WARNING);
		} else if (requesterField.getText().trim().equals("")) {
			showAlert("Error", "Please enter a requester email", AlertType.WARNING);
		} else if (catChoiceBox.getValue() == null) {
			showAlert("Error", "Please select a category", AlertType.WARNING);
		} else if (!subcatChoiceBox.isDisable() && subcatChoiceBox.getValue() == null) {
			showAlert("Error", "Please select a subcategory", AlertType.WARNING);
			
//		} else if (datePicker.getValue() == null) {
//			showAlert("Error", "Please choose a due date", AlertType.WARNING);
		} else if (assigneeField.getText().trim().equals("")) {
			showAlert("Error", "Please enter an assignee email", AlertType.WARNING);
		} else if (SamanageRequests.getUserByEmail(AppSession.getSession().getUserToken(), toCorrectDomain(requesterField.getText())) == null) {
			showAlert("Error", "Cannot find any requester with that email. Try again", AlertType.ERROR);
		} else if (SamanageRequests.getUserByEmail(AppSession.getSession().getUserToken(), toCorrectDomain(assigneeField.getText())) == null) {
			showAlert("Error", "Cannot find any assignee with that email. Try again", AlertType.ERROR);
		} else {
			submitBtn.setText("Loading...");
			submitBtn.setDisable(true);
			new Thread(() -> {
				try {
					SamanageRequests.newIncidentWithTimeTrack(AppSession.getSession().getUserToken(), incidentNameField.getText(), 
						priorityChoiceBox.getValue(), catChoiceBox.getValue(), 
						subcatChoiceBox.getValue(), descField.getText(),
						convertDate(datePicker.getValue()), statesChoiceBox.getValue(),
						toCorrectDomain(assigneeField.getText()), toCorrectDomain(defaultRequesterField.getText()));
				
				} catch (IOException e) {
					showAlert("Error", e.getMessage(), AlertType.ERROR);
					e.printStackTrace();
				}
			}).start();
			showAlert("Incident created", "Incident created", AlertType.INFORMATION);
			clearInputFields();
			submitBtn.setText("Submit");
			submitBtn.setDisable(false);
			incidentNameField.requestFocus();
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
				user = SamanageRequests.getUserByEmail(userTokenField.getText(), toCorrectDomain(userInputField.getText()));
				if (user == null) {
					showAlert("Error", "Cannot find any users with that email. Try again", AlertType.ERROR);
				} else {
					addTableItem(user);
					userInputField.requestFocus();
					userInputField.clear();
					timeTrackCmtField.clear();
					timeElapsedField.clear();
				}
			}
		}
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
			e1.printStackTrace();
		}
		provider.clearSuggestions();
		provider.addPossibleSuggestions(AppSession.getSession().getSavedEmails());
		assigneeProvider.clearSuggestions();
		assigneeProvider.addPossibleSuggestions(AppSession.getSession().getAssigneeEmails());
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
			e.printStackTrace();
		}
	}

	@FXML
	private void handleDefaultDomainFieldChange() {
		AppSession.getSession().setDefaultDomain(domainField.getText());
		try {
			AppSession.getSession().saveData();
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleDefaultRequesterFieldChange() {
		AppSession.getSession().setDefaultRequester(defaultRequesterField.getText());
		try {
			AppSession.getSession().saveData();
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleDefaultAssigneeFieldChange() {
		AppSession.getSession().setDefaultAssignee(defaultAssigneeField.getText());
		try {
			AppSession.getSession().saveData();
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleDefaultDomainChange() {
		AppSession.getSession().setDefaultDomain(domainField.getText());
		try {
			AppSession.getSession().saveData();
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleUpdateDataBtn() {
		if (isUpToDate) {
			showAlert("Already Up-to-date", "The data is already up to date.", AlertType.INFORMATION);
		} else {
			// https://stackoverflow.com/questions/45863687/javafx-progress-bar-to-show-the-progress-of-the-process
			Task<Parent> update = new Task<Parent>() {
			    @Override
			    public Parent call() throws JsonIOException, IOException {
					updateMessage("Updating Depts...");
			    	AppSession.getSession().updateDepts();
			    	
					updateMessage("Updating Sites...");
					AppSession.getSession().updateSites();
			    	
			    	updateMessage("Updating Categories...");
			    	AppSession.getSession().updateCategories();
			    	
					updateMessage("Saving Data...");
					AppSession.getSession().saveData();
					return null;
			    }
			};
		
		    //method to set labeltext
		    updateDataBtn.textProperty().bind(Bindings.convert(update.messageProperty()));
		    updateDataBtn.setDisable(true);
			update.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				
				@Override
				public void handle(WorkerStateEvent event) {
					showAlert("Updated", "Update Complete!", AlertType.INFORMATION);
					updateDataBtn.textProperty().unbind();
					updateDataBtn.setText("Update Data");
			        updateDataBtn.setDisable(false);
					
				}
			});
			Thread updateThread = new Thread(update);
			updateThread.start();
		}
	}
	
	private String convertDate(LocalDate date) {
		return calendar.get(date.getMonthValue()) + date.getDayOfMonth() + ", " + date.getYear();
	}
	
	private String toCorrectDomain(String email) {
		if (!email.contains("@") && !email.trim().equals("")) {
			return email + "@" + AppSession.getSession().getDefaultDomain();
		}
		else {
			return email;
		}
	}
}

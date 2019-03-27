package application.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.controlsfx.control.textfield.TextFields;

import com.google.gson.JsonIOException;

import application.data.AppSession;
import application.data.SamanageRequests;
import application.data.User;
import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class MainPaneController {
	
	@FXML
	private TabPane tabPane;
	@FXML 
	private Tab mainMenuTab;
	@FXML
	private Tab incidentEditTab;
	@FXML
	private Tab settingsTab;
	
	@FXML
	private Button createNewIncidentBtn;

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
	private ComboBox<String> deptComboBox;
	@FXML
	private ComboBox<String> siteComboBox;
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
	private SuggestionProvider<String> savedEmailprovider;
	@FXML
	private TextField requesterField;
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
	@FXML
	private CheckBox autoUpdateCheckBox;
	
	
	private boolean isUpToDate;
	
	@SuppressWarnings("serial")
	private final Map<Integer, String> calendar = new HashMap<Integer, String>() {{
		put(1,"Jan"); put(2,"Feb"); put(3,"Mar"); put(4,"Apr"); put(5,"May"); put(6,"June"); 
		put(7,"July"); put(8,"Aug"); put(9,"Sept"); put(10,"Oct"); put(11,"Nov"); put(12, "Dec");
		}};
		

	public void setStageAndSetupListeners(Stage primaryStage) {
		System.err.println("Loading...");
		try {
			AppSession.getSession().loadData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		savedEmailprovider = SuggestionProvider.create(AppSession.getSession().getSavedEmails());
		assigneeProvider = SuggestionProvider.create(AppSession.getSession().getAssigneeEmails());
		
		
		// setup setting tab
		System.err.println("Setting up Setting Tab");
		setupSettingTab();
		
		
		// setup priority
		System.err.println("Setting up Priority");
		setupPriorityChoiceBox();
		
		// setup date picker
		System.err.println("Setting up Date Picker");
		initializeDatePicker();
		
		// setup dept and site
		System.err.println("Setting up Depts and Sites");
		setupDeptAndSiteChoiceBox();
		
		// setup TextFields autocomplete
		System.err.println("Setting up Autocomplete");

		setupEmailAutoComplete();
		// setup main menu tab
		System.err.println("Setting up Main Menu");
		setupMainMenuTab();
		
		// setup tabs
		System.err.println("Setting up tabs");
		setupTabs();
		
		if (autoUpdateCheckBox.isSelected()) {
			System.err.println("Checking for newer database version...");
			isUpToDate = AppSession.getSession().isUpToDate();
		}

		System.err.println("Load Complete!");
		System.err.println("Please do not close this console!");
		System.err.println("Booting up App...");
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
	
	private void setupTabs() {
		//mainMenu.setDisable(false);
		
		
		if (tabPane.getSelectionModel().getSelectedIndex() == 0) {
			incidentEditTab.setDisable(true);
		}
		
		createNewIncidentBtn.setOnAction((e) -> {
			incidentEditTab.setDisable(false);
			tabPane.getSelectionModel().select(incidentEditTab);
		});
		
		
	}
	
	private void setupMainMenuTab() {
		// setup state
		System.err.println("Setting up State");
		setupStatesChoiceBox();
		
		// setup category
		System.err.println("Setting up Categories");
		setupCatChoiceBox();
		
		// setup incident name
		System.err.println("Setting up Incident Name Prompt");
		updateIncidentNamePrompt();
	}

	
	private void setupSettingTab() {
		userTokenField.setText(AppSession.getSession().getUserToken());
		domainField.setText(AppSession.getSession().getDefaultDomain());
		defaultAssigneeField.setText(AppSession.getSession().getDefaultAssignee());
		defaultRequesterField.setText(AppSession.getSession().getDefaultRequester());
		
		autoUpdateCheckBox.setSelected(AppSession.getSession().getDefaultAutoUpdateCheck());
		
		new AutoCompletionTextFieldBinding<>(defaultAssigneeField, savedEmailprovider);
		new AutoCompletionTextFieldBinding<>(defaultRequesterField, savedEmailprovider);
		
		//autoUpdateCheck.get
	}
	
	
	private void setupCatChoiceBox() {
		AppSession.getSession().setCategories(AppSession.getSession().getCategories());
		catChoiceBox.getSelectionModel().select(0);
		subcatChoiceBox.getSelectionModel().select(0);
		subcatChoiceBox.setDisable(true);
		catChoiceBox.getItems().addAll(AppSession.getSession().getCategories().keySet());
		catChoiceBox.getSelectionModel().selectedItemProperty()
	    		.addListener(new ChangeListener<String>() {
	    			@Override
	    			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		    			updateSubcatChoiceBox();
	    			}
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
	
	private void updateIncidentNamePrompt() {
		if (AppSession.getSession().hasUser(requesterField.getText().toLowerCase())) {
			incidentNameField.setPromptText(getDefaultIncidentName());
		}
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
		deptComboBox.getItems().addAll(AppSession.getSession().getDepartments());
		TextFields.bindAutoCompletion(deptComboBox.getEditor(), deptComboBox.getItems());
		
		siteComboBox.getItems().addAll(AppSession.getSession().getSites());
		TextFields.bindAutoCompletion(siteComboBox.getEditor(), siteComboBox.getItems());
		
		updateDefaultDeptSite();
	}
	
	private void updateDefaultDeptSite() {
		String email = requesterField.getText().toLowerCase();
		if (AppSession.getSession().hasUser(email)) {
			deptComboBox.getSelectionModel().select(AppSession.getSession()
					.getDepartments().indexOf(AppSession.getSession().getRequesterInfo(email).getDept()));
			siteComboBox.getSelectionModel().select(AppSession.getSession()
					.getSites().indexOf(AppSession.getSession().getRequesterInfo(email).getSite()));
		} else {
			deptComboBox.getSelectionModel().select(AppSession.getSession()
					.getDepartments().indexOf(AppSession.getSession().getRequesterInfo().getDept()));
			siteComboBox.getSelectionModel().select(AppSession.getSession()
					.getSites().indexOf(AppSession.getSession().getRequesterInfo().getSite()));
		
		}
	}
	
	private void setupEmailAutoComplete() {
		new AutoCompletionTextFieldBinding<>(userInputField, savedEmailprovider);
		assigneeField.setText(AppSession.getSession().getDefaultAssignee());
		handleAssigneeFieldChange();
		new AutoCompletionTextFieldBinding<>(assigneeField, assigneeProvider);
		requesterField.setText(AppSession.getSession().getDefaultRequester());
		new AutoCompletionTextFieldBinding<>(requesterField, savedEmailprovider);
	}
	
	@FXML
	private void handleSubmitBtn() {
		if (userTokenField.getText().trim().equals("")) {
			showAlert("Error", "Please enter user token (Setting)", AlertType.WARNING);
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
		} else if (!AppSession.getSession().getSavedEmails().contains(toCorrectDomain(requesterField.getText()))) {
			showAlert("Error", "Cannot find any requester with that email. Try again", AlertType.ERROR);
		} else if (!AppSession.getSession().getSavedEmails().contains(toCorrectDomain(assigneeField.getText()))) {
			showAlert("Error", "Cannot find any assignee with that email. Try again", AlertType.ERROR);
		} else {
			submitBtn.setText("Loading...");
			submitBtn.setDisable(true);

		
			new Thread(() -> {
				try {
					String incidentName;
					if (incidentNameField.getText().equals("")) {
						incidentName = getDefaultIncidentName();
					} else {
						incidentName = incidentNameField.getText();
					}
					SamanageRequests.newIncidentWithTimeTrack(AppSession.getSession().getUserToken(), incidentName, 
						priorityChoiceBox.getValue(), catChoiceBox.getValue(), 
						subcatChoiceBox.getValue(), descField.getText(),
						convertDate(datePicker.getValue()), statesChoiceBox.getValue(),
						toCorrectDomain(assigneeField.getText()), toCorrectDomain(requesterField.getText()));
				
				} catch (IOException e) {
					showAlert("Error", e.getMessage(), AlertType.ERROR);
					e.printStackTrace();
				}
			}).start();
			showAlert("Incident created", "Incident created", AlertType.INFORMATION);
			clearInputFields();
			submitBtn.setText("Submit");
			//submitBtn.setDisable(false);
			incidentNameField.requestFocus();
			
			
			tabPane.getSelectionModel().select(mainMenuTab);
			incidentEditTab.setDisable(true);
			
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

	private String getDefaultIncidentName() {
		return getDefaultCategoryIncidentName();
	}
	
	private String getDefaultDeptSiteIncidentName() {
		User req;
		if (requesterField.getText() == "") {
			req = AppSession.getSession().getRequesterInfo();
		} else {
			req = AppSession.getSession().getRequesterInfo(toCorrectDomain(requesterField.getText()));
		}
		return req.getDept() + " " + req.getSite();
	}
	
	private String getDefaultCategoryIncidentName() {
		String retStr = "";
		if (catChoiceBox.getValue() != null) {
			retStr += catChoiceBox.getValue();
			if (subcatChoiceBox.getValue() != null) {
				retStr += " " + subcatChoiceBox.getValue();
			}
		}
		return retStr;
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
		savedEmailprovider.clearSuggestions();
		savedEmailprovider.addPossibleSuggestions(AppSession.getSession().getSavedEmails());
		assigneeProvider.clearSuggestions();
		assigneeProvider.addPossibleSuggestions(AppSession.getSession().getAssigneeEmails());
	}


	private void clearInputFields() {
		incidentNameField.clear();
		descField.clear();
		infoTable.getItems().clear();
	}

	@FXML
	private void handleIncidentNameType() {
		
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
	private void handleRequesterFieldChange() {
		updateDefaultDeptSite();
		updateIncidentNamePrompt();
	}
	
	@FXML
	private void handleDefaultRequesterFieldChange() {
		String initDefault = AppSession.getSession().getDefaultRequester();
		AppSession.getSession().setDefaultRequester(defaultRequesterField.getText());
		if (!initDefault.equalsIgnoreCase(AppSession.getSession().getDefaultRequester())) {
			requesterField.setText(AppSession.getSession().getDefaultRequester());
			handleRequesterFieldChange();
			try {
				AppSession.getSession().saveData();
			} catch (JsonIOException | IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	private void handleDefaultAssigneeFieldChange() {
		AppSession.getSession().setDefaultAssignee(defaultAssigneeField.getText());
		assigneeField.setText(toShortDomain(AppSession.getSession().getDefaultAssignee()));
		handleAssigneeFieldChange();
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
	private void handleAssigneeFieldChange() {
		userInputField.setText(assigneeField.getText());
	}
	
	@FXML
	private void handleUpdateDataBtn() {
		if (isUpToDate) {
			showAlert("Already Up-to-date", "The data is already up to date.", AlertType.INFORMATION);
		} else {
			Alert alert = new Alert(AlertType.WARNING,
					"The process is going to take about 4 mins. Proceed?",
					ButtonType.OK, ButtonType.CANCEL);
			alert.setTitle(	"Warning");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				// https://stackoverflow.com/questions/45863687/javafx-progress-bar-to-show-the-progress-of-the-process
				Task<Parent> update = new Task<Parent>() {
				    @Override
				    public Parent call() throws JsonIOException, IOException {
				    	updateMessage("Updating Users...");
				    	AppSession.getSession().updateUsers();
				    	
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
	}
	
	@FXML
	private void handleAutoUpdateCheckBox() {
		AppSession.getSession().setDefaultAutoUpdateCheck(autoUpdateCheckBox.selectedProperty().getValue());
		try {
			AppSession.getSession().saveData();
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}

	}
	
	private String convertDate(LocalDate date) {
		return calendar.get(date.getMonthValue()) + date.getDayOfMonth() + ", " + date.getYear();
	}
	
	private String toCorrectDomain(String email) {
		if (!email.contains("@") && !email.trim().equals("")) {
			return (email + "@" + AppSession.getSession().getDefaultDomain()).toLowerCase();
		}
		else {
			return email.toLowerCase();
		}
	}

	private String toShortDomain(String email) {
		if (email.contains("@")) {
			if (email.split("@")[1].substring(1).equalsIgnoreCase("@" + AppSession.getSession().getDefaultDomain())) {
				return email.split("@")[0];
			}
		}
		return email;
	}
}

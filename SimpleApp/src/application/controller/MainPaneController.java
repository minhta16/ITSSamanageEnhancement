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
import application.data.Incident;
import application.data.IncidentEditType;
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
import javafx.scene.control.TableColumn;
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
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
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
	private TableView<Incident> incidentTable;

	@FXML
	private TextField userEmailField;
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
	@FXML
	private Button checkForUpdateBtn;
	
	
	private String updatePrompt = "";
	
	private String curUpdateIncidentID = "";
		
	public void setStageAndSetupListeners(Stage primaryStage) {
		
		System.out.println("Loading...");
		try {
			AppSession.getSession().loadData();
		} catch (IOException e) {
			e.printStackTrace();
		}

		savedEmailprovider = SuggestionProvider.create(AppSession.getSession().getSavedEmails());
		assigneeProvider = SuggestionProvider.create(AppSession.getSession().getAssigneeEmails());
		
		
		// setup setting tab
		System.out.print("Setting up Setting Tab\t\t\t\r");
		setupSettingTab();
		updatePrompt = AppSession.getSession().getUpdatePrompt();
		
		
		// setup priority
		System.out.print("Setting up Priority\t\t\t\r");
		setupPriorityChoiceBox();
		
		// setup date picker
		System.out.print("Setting up Date Picker\t\t\t\r");
		initializeDatePicker();
		
		// setup dept and site
		System.out.print("Setting up Depts and Sites\t\t\t\r");
		setupDeptAndSiteChoiceBox();
		
		// setup TextFields autocomplete
		System.out.print("Setting up Autocomplete\t\t\t\r");

		setupEmailAutoComplete();
		// setup main menu tab
		System.out.print("Setting up Main Menu\t\t\t\r");
		setupMainMenuTab();

		System.out.print("Setting up Incident Editor\t\t\t\r");
		setupIncidentEditTab();
		
		// setup tabs
		System.out.print("Setting up tabs\t\t\t\r");
		setupTabs();
		
		autoUpdateCheckBox.setSelected(AppSession.getSession().getDefaultAutoUpdateCheck());
		if (AppSession.getSession().getDefaultAutoUpdateCheck()) {
			System.out.print("Checking for newer database version...\t\t\t\r");
			updatePrompt = AppSession.getSession().getUpdatePrompt();
		}

		System.out.println("Load Complete!\t\t\t");
		System.out.println("DO NOT CLOSE THIS CONSOLE! THE APP WILL CLOSE ALONG WITH IT!");
		System.out.println("Booting up App...");
	}
	
	public void showPrompt() {
		if (AppSession.getSession().getDefaultAutoUpdateCheck()) {
			if (!updatePrompt.equals("")) {
				showAlert("Database outdated", "Database Outdated. Details:\n" + updatePrompt, AlertType.WARNING);
			}
		} else {
			showAlert("Database Update Check Disabled", "If you want to check for database changes (users, categories,...)"
					+ " upon startup,\nplease enable 'Check for Updates at Startup' in Settings/Database.", AlertType.WARNING);
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
			AppSession.getSession().setEditType(IncidentEditType.NEW);
			clearInputFields();
		});
		
		
	}
	
	private void setupMainMenuTab() {
		// setup incidentTable
		incidentTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("number"));
		incidentTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("state"));
		incidentTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("title"));
		incidentTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("priority"));
		incidentTable.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("cat"));
		incidentTable.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("subcat"));
		incidentTable.getColumns().get(6).setCellValueFactory(new PropertyValueFactory<>("assignee"));
		incidentTable.getColumns().get(7).setCellValueFactory(new PropertyValueFactory<>("requester"));
		incidentTable.getColumns().get(8).setCellValueFactory(new PropertyValueFactory<>("site"));
		incidentTable.getColumns().get(9).setCellValueFactory(new PropertyValueFactory<>("dept"));
		incidentTable.getColumns().get(10).setCellValueFactory(new PropertyValueFactory<>("trackedUsersNum"));
		incidentTable.getColumns().get(11).setCellValueFactory(new PropertyValueFactory<>("editBtn"));
	}
	
	private void setupIncidentEditTab() {
		// setup state
		System.out.print("Setting up State\r");
		setupStatesChoiceBox();
		
		// setup category
		System.out.print("Setting up Categories\r");
		setupCatChoiceBox();
		
		// setup incident name
		System.out.print("Setting up Incident Name Prompt\r");
		updateIncidentNamePrompt();
	}

	
	private void setupSettingTab() {
		userEmailField.setText(AppSession.getSession().getUserEmail());
		userTokenField.setText(AppSession.getSession().getUserToken());
		domainField.setText(AppSession.getSession().getDefaultDomain());
		defaultAssigneeField.setText(AppSession.getSession().getDefaultAssignee());
		defaultRequesterField.setText(AppSession.getSession().getDefaultRequester());

		new AutoCompletionTextFieldBinding<>(userEmailField, savedEmailprovider);
		new AutoCompletionTextFieldBinding<>(defaultAssigneeField, savedEmailprovider);
		new AutoCompletionTextFieldBinding<>(defaultRequesterField, savedEmailprovider);
		
		autoUpdateCheckBox.setSelected(AppSession.getSession().getDefaultAutoUpdateCheck());
		
		// setup infoTable
		infoTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));
		infoTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("email"));
		infoTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("time"));
		infoTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("comment"));
		infoTable.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("removeBtn"));
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
		requesterField.setText(AppSession.getSession().getDefaultRequester());
		
		new AutoCompletionTextFieldBinding<>(assigneeField, assigneeProvider);
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
			if (AppSession.getSession().getTrackedUsers().isEmpty()) {
				Alert alert = new Alert(AlertType.WARNING,
						"No time tracks have been entered. Proceed?",
						ButtonType.OK, ButtonType.CANCEL);
				alert.setTitle(	"Warning");
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK) {
					submitIncident();
					timeElapsedField.clear();
					timeTrackCmtField.clear();
				}
			} else {
				submitIncident();
			}
			
			
		}
	}
	
	private void submitIncident() {
		submitBtn.setText("Loading...");
		submitBtn.setDisable(true);
	
		if (AppSession.getSession().getEditType() == IncidentEditType.NEW) {
			Task<Parent> newIncident = new Task<Parent>() {
			    @Override
			    public Parent call() throws JsonIOException, IOException {
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
							datePicker.getValue().toString(), statesChoiceBox.getValue(),
							toCorrectDomain(assigneeField.getText()), toCorrectDomain(requesterField.getText()),
							deptComboBox.getValue(),
							siteComboBox.getValue());
					
					} catch (IOException e) {
						showAlert("Error", e.getMessage(), AlertType.ERROR);
						e.printStackTrace();
					}
					return null;
			    }
			};
		
		    //method to set labeltext
			submitBtn.textProperty().bind(Bindings.convert(newIncident.messageProperty()));
			submitBtn.setDisable(true);
			newIncident.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				
				@Override
				public void handle(WorkerStateEvent event) {
					showAlert("Incident created", "Incident created", AlertType.INFORMATION);
				}
			});
			Thread newIncidentThread = new Thread(newIncident);
			newIncidentThread.start();
			
		// TODO: IMPLEMENT EDIT
		} else if (AppSession.getSession().getEditType() == IncidentEditType.EDIT) {
			Task<Parent> editIncident = new Task<Parent>() {
			    @Override
			    public Parent call() throws JsonIOException, IOException {
			    	try {
						//String incidentName;
						/*
						 * if (incidentNameField.getText().equals("")) { incidentName =
						 * getDefaultIncidentName(); } else { incidentName =
						 * incidentNameField.getText(); }
						 */

						String incidentName;
						if (incidentNameField.getText().equals("")) {
							incidentName = getDefaultIncidentName();
						} else {
							incidentName = incidentNameField.getText();
						}
						SamanageRequests.updateIncidentWithTimeTrack(AppSession.getSession().getUserToken(), incidentName, curUpdateIncidentID,
							priorityChoiceBox.getValue(), catChoiceBox.getValue(), 
							subcatChoiceBox.getValue(), descField.getText(),
							datePicker.getValue().toString(), statesChoiceBox.getValue(),
							toCorrectDomain(assigneeField.getText()), toCorrectDomain(requesterField.getText()),
							deptComboBox.getValue(),
							siteComboBox.getValue());
						
					
					} catch (IOException e) {
						showAlert("Error", e.getMessage(), AlertType.ERROR);
						e.printStackTrace();
					}
					return null;
			    }
			};
		    //method to set labeltext
			submitBtn.textProperty().bind(Bindings.convert(editIncident.messageProperty()));
			submitBtn.setDisable(true);
			editIncident.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				
				@Override
				public void handle(WorkerStateEvent event) {
					showAlert("Incident updated", "Incident updated", AlertType.INFORMATION);
				}
			});
			Thread editIncidentThread = new Thread(editIncident);
			editIncidentThread.start();
		}
		clearInputFields();
		submitBtn.setText("Submit");
		
		
		tabPane.getSelectionModel().select(mainMenuTab);
		incidentEditTab.setDisable(true);
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
	
	@SuppressWarnings("unused")
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
				retStr += ", " + subcatChoiceBox.getValue();
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
		updateDefaultDeptSite();
		submitBtn.setDisable(false);
	}

	@FXML
	private void handleUpdateListBtn() {
		if (AppSession.getSession().getUserEmail().equals("")) {
			showAlert("User Email not set", "Please setup your user email in Settings", AlertType.WARNING);
		} else {
			incidentTable.getItems().clear();
			AppSession.getSession().updateTodayIncidents();
			addIncidentsToTable();
		}
	}
	
	private void addIncidentsToTable() {
		for (String incidentNum: AppSession.getSession().getCurrentIncidents().keySet()) {
			Incident incident = AppSession.getSession().getCurrentIncidents().get(incidentNum);
			incident.getEditBtn().setOnAction((e) -> {
				incidentEditTab.setDisable(false);
				curUpdateIncidentID = incident.getID();
				tabPane.getSelectionModel().select(incidentEditTab);
				preFetchIncidentInfo(incident.getNumber());
				AppSession.getSession().setEditType(IncidentEditType.EDIT);
			});
			incidentTable.getItems().add(incident);
		}
		
		// DEFAULT SORT BY NUMBER
		incidentTable.getColumns().get(0).setSortType(TableColumn.SortType.DESCENDING);
		incidentTable.getSortOrder().add(incidentTable.getColumns().get(0));
	}
	
	private void preFetchIncidentInfo(String number) {
		Incident incident = AppSession.getSession().getCurrentIncidents().get(number);
		statesChoiceBox.setValue(incident.getState());
		requesterField.setText(incident.getRequester());
		incidentNameField.setText(incident.getTitle());
		catChoiceBox.setValue(incident.getCategory());
		subcatChoiceBox.setValue(incident.getSubcategory());
		assigneeField.setText(incident.getAssignee());
		descField.setText(incident.getDescription());
		priorityChoiceBox.setValue(incident.getPriority());
		deptComboBox.setValue(incident.getDept());
		siteComboBox.setValue(incident.getSite());
		if (incident.getDueOn() != null) {
			datePicker.setValue(incident.getDueOn());
		}
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
	public void handleCatChange() {
		updateIncidentNamePrompt();
	}
	
	@FXML
	private void handleUserEmailFieldChange() {
		String initDefault = AppSession.getSession().getUserEmail();
		AppSession.getSession().setUserEmail(userEmailField.getText());
		if (!initDefault.equalsIgnoreCase(AppSession.getSession().getUserEmail())) {
			userEmailField.setText(AppSession.getSession().getUserEmail());
			try {
				AppSession.getSession().saveData();
			} catch (JsonIOException | IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	private void handleDefaultRequesterFieldChange() {
		String initDefault = AppSession.getSession().getDefaultRequester();
		AppSession.getSession().setDefaultRequester(defaultRequesterField.getText());
		if (!initDefault.equalsIgnoreCase(AppSession.getSession().getDefaultRequester())) {
			requesterField.setText(toShortDomain(AppSession.getSession().getDefaultRequester()));
			defaultRequesterField.setText(AppSession.getSession().getDefaultRequester());
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

		String initDefault = AppSession.getSession().getDefaultAssignee();
		AppSession.getSession().setDefaultAssignee(defaultAssigneeField.getText());
		if (!initDefault.equalsIgnoreCase(AppSession.getSession().getDefaultAssignee())) {
			assigneeField.setText(toShortDomain(AppSession.getSession().getDefaultAssignee()));
			defaultAssigneeField.setText(AppSession.getSession().getDefaultAssignee());
			handleAssigneeFieldChange();
			try {
				AppSession.getSession().saveData();
			} catch (JsonIOException | IOException e) {
				e.printStackTrace();
			}
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
	private void handleAutoupdateCheck() {
		AppSession.getSession().setDefaultAutoUpdateCheck(autoUpdateCheckBox.isSelected());
		try {
			AppSession.getSession().saveData();
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleUpdateDataBtn() {
		if (!updatePrompt.equals("")) {
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
					    updatePrompt = "";
						
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
	
	@FXML
	private void handleCheckForUpdateBtn() {
		Task<Parent> updateCheck = new Task<Parent>() {
		    @Override
		    public Parent call() throws JsonIOException, IOException {
		    	updateMessage("Checking...");
				updatePrompt = AppSession.getSession().getUpdatePrompt();
		    	
				return null;
		    }
		};
	
	    //method to set labeltext
	    checkForUpdateBtn.textProperty().bind(Bindings.convert(updateCheck.messageProperty()));
	    checkForUpdateBtn.setDisable(true);
		updateCheck.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			
			@Override
			public void handle(WorkerStateEvent event) {
				if (updatePrompt.equals("")) {
					showAlert("Check complete", "Database is Up-to-date." + updatePrompt, AlertType.INFORMATION);
				} else {
					showAlert("Check complete", "Database Outdated. Press Update to update.\nDetails:\n" + updatePrompt, AlertType.WARNING);
					updateDataBtn.setDisable(false);
				}
				checkForUpdateBtn.textProperty().unbind();
				checkForUpdateBtn.setText("Check for Updates");
				checkForUpdateBtn.setDisable(false);
			}
		});
		Thread updateThread = new Thread(updateCheck);
		updateThread.start();
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
			if (email.split("@")[1].equalsIgnoreCase(AppSession.getSession().getDefaultDomain())) {
				return email.split("@")[0];
			}
		}
		return email;
	}
}

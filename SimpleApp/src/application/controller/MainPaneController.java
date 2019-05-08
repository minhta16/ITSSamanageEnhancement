package application.controller;

import java.awt.Desktop;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

import javax.xml.parsers.ParserConfigurationException;

import org.controlsfx.control.textfield.TextFields;
import org.xml.sax.SAXException;

import com.google.gson.JsonIOException;

import application.data.AppSession;
import application.data.Incident;
import application.data.IncidentEditType;
import application.data.SamanageRequests;
import application.data.TimeTrack;
import application.data.User;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
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
	private ComboBox<String> templateComboBox;

	@FXML
	private Button createNewIncidentBtn;

	@FXML
	private ChoiceBox<String> statesChoiceBox;
	@FXML
	private ComboBox<String> catChoiceBox;
	@FXML
	private Label subcatLabel;
	@FXML
	private Hyperlink incidentLink;
	@FXML
	private Hyperlink allIncidentsLink;
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
	private Label softwareLabel;
	@FXML
	private ComboBox<String> softwareComboBox;
	@FXML
	private CheckBox notifyCheckBox;
	@FXML
	private DatePicker updateFromDatePicker;
	@FXML
	private DatePicker updateToDatePicker;

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
	private TableView<TimeTrack> trackTable;
	@FXML
	private TableView<Incident> incidentTable;
	@FXML
	private Button updateListBtn;


	@FXML
	private TextArea userTokenField;
	@FXML
	private TextField domainField;
	@FXML
	private TextField defaultAssigneeField;
	@FXML
	private TextField defaultRequesterField;
	@FXML
	private Button updateDtbBtn;
	@FXML
	private ComboBox<String> filterComboBox;
	@FXML
	private TextField filterField;
	@FXML
	private ComboBox<String> filterComboBox2;
	@FXML
	private TextField filterField2;
	@FXML
	private Button addRmvBtn;


	private String updatePrompt = "";

	private String curUpdateIncidentID = "";

	private ObservableList<Incident> updatedListOfIncidents = FXCollections.observableArrayList();



	public void setStageAndSetupListeners(Stage primaryStage) throws FileNotFoundException {


		savedEmailprovider = SuggestionProvider.create(AppSession.getSession().getSavedEmails());
		assigneeProvider = SuggestionProvider.create(AppSession.getSession().getAssignees());

		
		// setup setting tab
		System.out.print("Setting up Setting Tab\t\t\t\r");
		setupSettingTab();

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

		System.out.print("Setting up Template Editor\t\t\t\r");
		setupTemplateTab();
		System.out.println("Load Complete!\t\t\t");
		System.out.println("DO NOT CLOSE THIS CONSOLE! THE APP WILL CLOSE ALONG WITH IT!");
		System.out.println("Booting up App...");
	}

	/**
	 * 
	 */
	public void showPrompt() {
	}

	private void setupTabs() {
		// mainMenu.setDisable(false);

		if (tabPane.getSelectionModel().getSelectedIndex() == 0) {
			incidentEditTab.setDisable(true);
		}

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
		incidentTable.getColumns().get(11).setCellValueFactory(new PropertyValueFactory<>("date"));
		incidentTable.getColumns().get(12).setCellValueFactory(new PropertyValueFactory<>("editBtn"));
		incidentTable.setPlaceholder(new Label("Please update list to see the lastest incidents."));

		// setupFilterComboBox
		filterField.setDisable(true);
		filterField2.setDisable(true);
		for (int i = 0; i < incidentTable.getColumns().size() - 1; i++) {
			// filterComboBox.getItems().addAll((Collection<? extends String>)
			// incidentTable.getColumns().get(i));
			// System.out.println(incidentTable.getProperties().keySet());
			filterComboBox.getItems().add(incidentTable.getColumns().get(i).getText());
			filterComboBox2.getItems().add(incidentTable.getColumns().get(i).getText());
		}

		allIncidentsLink.setOnAction((ev) -> {
			try {
				//https://stackoverflow.com/questions/16604341/how-can-i-open-the-default-system-browser-from-a-java-fx-application
				Desktop.getDesktop().browse(new URL("https://augustanacollege.samanage.com/incidents").toURI());
			} catch (IOException | URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
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

		userTokenField.setText(AppSession.getSession().getUserToken());
		domainField.setText(AppSession.getSession().getDefaultDomain());
		defaultAssigneeField.setText(AppSession.getSession().getDefaultAssignee());
		defaultRequesterField.setText(AppSession.getSession().getDefaultRequester());


		TextFields.bindAutoCompletion(defaultAssigneeField, savedEmailprovider);
		defaultAssigneeField.textProperty().addListener((o, oV, nV) -> {
			handleDefaultAssigneeFieldChange();
		});
		TextFields.bindAutoCompletion(defaultRequesterField, savedEmailprovider);
		defaultRequesterField.textProperty().addListener((o, oV, nV) -> {
			handleDefaultRequesterFieldChange();
		});


		// setup infoTable
		trackTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));
		trackTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("email"));
		trackTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("time"));
		trackTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("comment"));
		trackTable.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("removeBtn"));
	}

	private void setupCatChoiceBox() {
		AppSession.getSession().setCategories(AppSession.getSession().getCategories());
		catChoiceBox.getSelectionModel().select(0);
		subcatChoiceBox.getSelectionModel().select(0);
		setDisableSubcatChoiceBox(true);
		catChoiceBox.getItems().addAll(AppSession.getSession().getCategories().keySet());
		catChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				updateSubcatChoiceBox();
			}
		});
	}

	private void setDisableSubcatChoiceBox(boolean disable) {
		subcatChoiceBox.setDisable(disable);
		subcatLabel.setDisable(disable);
//		subcatLabel.setVisible(!disable);
//		subcatChoiceBox.setVisible(!disable);
	}

	private void setDisableSoftwareComboBox(boolean disable) {
		softwareComboBox.setDisable(disable);
		softwareLabel.setDisable(disable);
//		softwareLabel.setVisible(!disable);
//		softwareComboBox.setVisible(!disable);
	}

	private void updateSubcatChoiceBox() {
		subcatChoiceBox.getItems().clear();
		if (catChoiceBox.getValue() == null) {
			setDisableSubcatChoiceBox(true);
		} else if (AppSession.getSession().getCategories().get(catChoiceBox.getValue()).isEmpty()) {
			setDisableSubcatChoiceBox(true);
		} else {
			setDisableSubcatChoiceBox(false);
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

//		updateFromDatePicker.get
		updateFromDatePicker.setValue(LocalDate.now());
		updateFromDatePicker.setConverter(new StringConverter<LocalDate>() {
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

		updateToDatePicker.setValue(LocalDate.now());
		updateToDatePicker.setConverter(new StringConverter<LocalDate>() {
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

		softwareComboBox.getItems().addAll(AppSession.getSession().getSoftwares().keySet());
		TextFields.bindAutoCompletion(softwareComboBox.getEditor(), softwareComboBox.getItems());
		setDisableSoftwareComboBox(true);

		updateDefaultDeptSite();
	}

	private void updateDefaultDeptSite() {
		String email = requesterField.getText().toLowerCase();
		if (AppSession.getSession().hasUser(email)) {
			deptComboBox.getSelectionModel().select(AppSession.getSession().getDepartments()
					.indexOf(AppSession.getSession().getRequesterInfo(email).getDept()));
			siteComboBox.getSelectionModel().select(AppSession.getSession().getSites()
					.indexOf(AppSession.getSession().getRequesterInfo(email).getSite()));
		} else {
			deptComboBox.getSelectionModel().select(AppSession.getSession().getDepartments()
					.indexOf(AppSession.getSession().getRequesterInfo().getDept()));
			siteComboBox.getSelectionModel().select(
					AppSession.getSession().getSites().indexOf(AppSession.getSession().getRequesterInfo().getSite()));

		}
	}

	private void setupEmailAutoComplete() {
		TextFields.bindAutoCompletion(userInputField, savedEmailprovider);
		userInputField.textProperty().addListener((o, oV, nV) -> {
			handleUserInputFieldChange();
		});
		assigneeField.setText(AppSession.getSession().getDefaultAssignee());
		handleAssigneeFieldChange();
		requesterField.setText(AppSession.getSession().getDefaultRequester());

		TextFields.bindAutoCompletion(assigneeField, assigneeProvider);
		assigneeField.textProperty().addListener((o, oV, nV) -> {
			handleAssigneeFieldChange();
		});
		TextFields.bindAutoCompletion(requesterField, savedEmailprovider);
		requesterField.textProperty().addListener((o, oV, nV) -> {
			handleRequesterFieldChange();
		});
	}

	@FXML
	private void handleNewIncidentBtn() {
		incidentEditTab.setDisable(false);
		incidentEditTab.setText("New Incident");
		incidentLink.setVisible(false);
		tabPane.getSelectionModel().select(incidentEditTab);
		AppSession.getSession().setEditType(IncidentEditType.NEW);
		clearInputFields();
	}

	@FXML
	private void handleClearAllFieldsBtn() {
		clearInputFields();
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
		} else if (!softwareComboBox.isDisable() && softwareComboBox.getValue() == null) {
			showAlert("Error", "Please select a software", AlertType.WARNING);
		} else if (!AppSession.getSession().getSavedEmails().contains(toCorrectDomain(requesterField.getText()))) {
			showAlert("Error", "Cannot find any requester with that email. Try again", AlertType.ERROR);
		} else if (!AppSession.getSession().getSavedEmails().contains(toCorrectDomain(assigneeField.getText()))
				&& !AppSession.getSession().hasGroup(assigneeField.getText())) {
			showAlert("Error", "Cannot find any assignee with that information. Try again", AlertType.ERROR);
		} else {
			// TODO: handle remove all time tracks in EDIT mode
			if (AppSession.getSession().getTimeTracks().isEmpty()
					&& AppSession.getSession().getEditType() == IncidentEditType.NEW) {
				Alert alert = new Alert(AlertType.WARNING, "No time tracks have been entered. Proceed?", ButtonType.OK,
						ButtonType.CANCEL);
				alert.setTitle("Warning");
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
	
	@FXML
	private void handleCancelBtn() {
		clearInputFields();
		incidentEditTab.setDisable(true);
		incidentEditTab.setText("Incident Edit");
		tabPane.getSelectionModel().select(mainMenuTab);
	}

	private void submitIncident() {
		submitBtn.setDisable(true);

		if (AppSession.getSession().getEditType() == IncidentEditType.NEW) {
			Task<Parent> newIncident = new Task<Parent>() {
				@Override
				public Parent call() throws JsonIOException, IOException {
					try {
						updateMessage("Loading...");
						String incidentName;
						if (incidentNameField.getText().equals("")) {
							incidentName = getDefaultIncidentName();
						} else {
							incidentName = incidentNameField.getText();
						}
						String assignee = assigneeField.getText();
						if (!AppSession.getSession().hasGroup(assignee)) {
							assignee = toCorrectDomain(assignee);
						} else {
							assignee = AppSession.getSession().getGroupId(assignee);
						}
						incidentEditTab.setDisable(true);
						SamanageRequests.newIncidentWithTimeTrack(AppSession.getSession().getUserToken(), incidentName,
								priorityChoiceBox.getValue(), catChoiceBox.getValue(), subcatChoiceBox.getValue(),
								descField.getText(), datePicker.getValue().toString(), statesChoiceBox.getValue(),
								assignee, toCorrectDomain(requesterField.getText()), deptComboBox.getValue(),
								siteComboBox.getValue(), softwareComboBox.getValue(), notifyCheckBox.isSelected());

						updateMessage("Submit");
					} catch (IOException | ParserConfigurationException | SAXException e) {
						printError(e);
					}
					return null;
				}
			};

			// method to set labeltext
			submitBtn.textProperty().bind(Bindings.convert(newIncident.messageProperty()));
			submitBtn.setDisable(true);
			newIncident.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

				@Override
				public void handle(WorkerStateEvent event) {
					submitBtn.textProperty().unbind();
					clearInputFields();
					showAlert("Incident Created", "Incident Created", AlertType.INFORMATION);
					tabPane.getSelectionModel().select(mainMenuTab);
					incidentEditTab.setText("Incident Edit");
					handleUpdateListBtn();
				}
			});
			Thread newIncidentThread = new Thread(newIncident);
			newIncidentThread.start();

		} else if (AppSession.getSession().getEditType() == IncidentEditType.EDIT) {
			Task<Parent> editIncident = new Task<Parent>() {
				@Override
				public Parent call() throws JsonIOException, IOException {
					try {
						// String incidentName;
						/*
						 * if (incidentNameField.getText().equals("")) { incidentName =
						 * getDefaultIncidentName(); } else { incidentName =
						 * incidentNameField.getText(); }
						 */

						updateMessage("Loading...");
						String incidentName;
						if (incidentNameField.getText().equals("")) {
							incidentName = getDefaultIncidentName();
						} else {
							incidentName = incidentNameField.getText();
						}
						String assignee = assigneeField.getText();
						if (!AppSession.getSession().hasGroup(assignee)) {
							assignee = toCorrectDomain(assignee);
						} else {
							assignee = AppSession.getSession().getGroupId(assignee);
						}
						incidentEditTab.setDisable(true);
						SamanageRequests.updateIncidentWithTimeTrack(AppSession.getSession().getUserToken(),
								incidentName, curUpdateIncidentID, priorityChoiceBox.getValue(),
								catChoiceBox.getValue(), subcatChoiceBox.getValue(), descField.getText(),
								datePicker.getValue().toString(), statesChoiceBox.getValue(), assignee,
								toCorrectDomain(requesterField.getText()), deptComboBox.getValue(),
								siteComboBox.getValue(), softwareComboBox.getValue(), notifyCheckBox.isSelected());

						updateMessage("Submit");
					} catch (IOException e) {
						printError(e);
					}
					return null;
				}
			};
			// method to set labeltext
			submitBtn.textProperty().bind(Bindings.convert(editIncident.messageProperty()));
			submitBtn.setDisable(true);
			editIncident.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

				@Override
				public void handle(WorkerStateEvent event) {
					submitBtn.textProperty().unbind();
					clearInputFields();
					showAlert("Incident Updated", "Incident Updated", AlertType.INFORMATION);
					tabPane.getSelectionModel().select(mainMenuTab);
					incidentEditTab.setText("Incident Edit");
					handleUpdateListBtn();
				}
			});
			Thread editIncidentThread = new Thread(editIncident);
			editIncidentThread.start();
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
				if (!AppSession.getSession().getUsers().keySet().contains(toCorrectDomain(userInputField.getText()))) {
					showAlert("Error", "Cannot find any users with that email. Try again", AlertType.ERROR);
				} else {
					addTableItem(AppSession.getSession().getUsers().get(toCorrectDomain(userInputField.getText())));
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

	private void addTableItem(TimeTrack track) {
		track.getRemoveBtn().setOnAction((e) -> {
			AppSession.getSession().removeTimeTrackByEmail(track.getEmail());
			trackTable.getItems().remove(track);
		});
		trackTable.getItems().add(track);

		try {
			AppSession.getSession().addTrackedUser(track);
		} catch (JsonIOException | IOException e1) {
			e1.printStackTrace();
		}
		savedEmailprovider.clearSuggestions();
		savedEmailprovider.addPossibleSuggestions(AppSession.getSession().getSavedEmails());
		assigneeProvider.clearSuggestions();
		assigneeProvider.addPossibleSuggestions(AppSession.getSession().getAssignees());
	}
	
	private void addTableItem(User user) {
		TimeTrack track = new TimeTrack(user, Integer.parseInt(timeElapsedField.getText()),
				timeTrackCmtField.getText());
		addTableItem(track);
	}

	private void clearInputFields() {
		templateComboBox.setValue(null);
		incidentNameField.clear();
		descField.clear();
		trackTable.getItems().clear();
		catChoiceBox.setValue(null);
		subcatChoiceBox.setValue(null);
		softwareComboBox.setValue(null);
		requesterField.setText(AppSession.getSession().getDefaultRequester());
		assigneeField.setText(AppSession.getSession().getDefaultAssignee());
		updateDefaultDeptSite();
		// reset tracked email to be default
		handleAssigneeFieldChange();
		submitBtn.setDisable(false);
	}

	@FXML
	private void handleUpdateListBtn() {
		incidentTable.setPlaceholder(new Label("Updating incidents..."));
		incidentTable.setItems(null);
		Task<Parent> updateIncidentList = new Task<Parent>() {
			@Override
			public Parent call() throws JsonIOException, IOException {
				updateMessage("Loading...");
				
				try {
					AppSession.getSession().updateListIncidents(updateFromDatePicker.getValue(),
							updateToDatePicker.getValue());
				} catch (SAXException | ParserConfigurationException | InterruptedException e) {
					printError(e);
				}
				updateMessage("Update List");
				return null;
			}
		};
		// method to set labeltext
		updateListBtn.textProperty().bind(Bindings.convert(updateIncidentList.messageProperty()));
		updateListBtn.setDisable(true);
		updateIncidentList.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				addIncidentsToTable();
				updateListBtn.textProperty().unbind();
				updateListBtn.setDisable(false);
				incidentTable.setPlaceholder(new Label("No incidents found"));
				filterField.fireEvent(new ActionEvent());
			}
		});
		Thread updateIncidentListThread = new Thread(updateIncidentList);
		updateIncidentListThread.start();

	}

	private void addIncidentsToTable() {
		updatedListOfIncidents.clear();
		for (String incidentNum : AppSession.getSession().getCurrentIncidents().keySet()) {
			Incident incident = AppSession.getSession().getCurrentIncidents().get(incidentNum);
			incident.getEditBtn().setOnAction((e) -> {
				incidentEditTab.setDisable(false);
				incidentEditTab.setText("Edit: " + incident.getTitle());
				incidentLink.setVisible(true);
				incidentLink.setOnAction((event) -> {
					try {
						Desktop.getDesktop().browse(new URL("https://augustanacollege.samanage.com/incidents/" + incident.getID()).toURI());
					} catch (IOException | URISyntaxException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				});
				curUpdateIncidentID = incident.getID();
				tabPane.getSelectionModel().select(incidentEditTab);
				try {
					incident.setTimeTracks(
							SamanageRequests.getTimeTracks(AppSession.getSession().getUserToken(), incident.getID()));
				} catch (IOException | SAXException | ParserConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					showAlert("ERROR", "ERROR:\n" + e1, AlertType.ERROR);
					System.exit(1);
				}
				// templateComboBox.setDisable(true);
				preFetchIncidentInfo(incident.getNumber());
				AppSession.getSession().setEditType(IncidentEditType.EDIT);
			});

			if (AppSession.getSession().getGroups().containsKey(incident.getGroupId())
					&& incident.getAssignee().isEmpty()) {
				// set Assignee to Group Name
				incident.setAssignee(AppSession.getSession().getGroups().get(incident.getGroupId()).getName());
			}
			updatedListOfIncidents.add(incident);

		}
		
		if (AppSession.getSession().getCurrentIncidents().keySet().isEmpty()) {
			incidentTable.setPlaceholder(new Label("You have no assigned incidents today."));
		} else {
			// 1. Wrap the ObservableList in a FilteredList (initially display all data).
			FilteredList<Incident> filteredUpdatedListOfIncidents = new FilteredList<>(updatedListOfIncidents, p -> true);

			// 2. Set the filter Predicate whenever the filter changes.
			filterField.textProperty().addListener((observable, oldValue, newValue) -> {
				filteredUpdatedListOfIncidents.setPredicate(incident -> {

					// Compare first name and last name of every person with filter text.
					String lowerCaseFilter = newValue.toLowerCase();
//					String lowerCaseFilter2 = filterField2.getText().toLowerCase();
					String curFilter = AppSession.getSession().getCurrentFilter();
//					String curFilter2 = AppSession.getSession().getCurrentFilter2();

					boolean match = false;

					// If filter text is empty, display all persons.
					if (newValue == null || newValue.isEmpty()) {
						match = true;
					} else {
						if (incident.getState().toLowerCase().contains(lowerCaseFilter) && curFilter.equals("State")) {
							match = true;
						} else if (incident.getTitle().toLowerCase().contains(lowerCaseFilter) && curFilter.equals("Title")) {
							match = true;
						} else if (incident.getPriority().toLowerCase().contains(lowerCaseFilter) && curFilter.equals("Priority")) {
							match = true;
						} else if (incident.getCat().toLowerCase().contains(lowerCaseFilter)  && curFilter.equals("Category")) {
							match = true;
						} else if (incident.getSubcat().toLowerCase().contains(lowerCaseFilter)  && curFilter.equals("Subcategory")) {
							match = true;
						} else if (incident.getAssignee().toLowerCase().contains(lowerCaseFilter) && curFilter.equals("Assignee")) {
							match = true;
						} else if (incident.getRequester().toLowerCase().contains(lowerCaseFilter) && curFilter.equals("Requester")) {
							match = true;
						} else if (incident.getSite().toLowerCase().contains(lowerCaseFilter) && curFilter.equals("Site")) {
							match = true;
						} else if (incident.getDept().toLowerCase().contains(lowerCaseFilter) && curFilter.equals("Dept")) {
							match = true;
						} else if (Integer.toString(incident.getTrackedUsersNum()).contains(lowerCaseFilter) && curFilter.equals("Tracks #")) {
							match = true;
						} else if (incident.getNumber().contains(lowerCaseFilter) && curFilter.equals("#")) {
							match = true;
						} else if (incident.getDate().contains(lowerCaseFilter) && curFilter.equals("Date")) {
							match = true;
						} else {
							match = false;
						}
					}
					
					// TODO: implement 2 filters
//					if (!lowerCaseFilter2.isEmpty()) {
//						if (incident.getState().toLowerCase().contains(lowerCaseFilter2) && curFilter2.equals("State")) {
//							match = match && true;
//						} else if (incident.getTitle().toLowerCase().contains(lowerCaseFilter2) && curFilter2.equals("Title")) {
//							match = match && true;
//						} else if (incident.getPriority().toLowerCase().contains(lowerCaseFilter2) && curFilter2.equals("Priority")) {
//							match = match && true;
//						} else if (incident.getCat().toLowerCase().contains(lowerCaseFilter2)  && curFilter2.equals("Category")) {
//							match = match && true;
//						} else if (incident.getSubcat().toLowerCase().contains(lowerCaseFilter2)  && curFilter2.equals("Subcategory")) {
//							match = match && true;
//						} else if (incident.getAssignee().toLowerCase().contains(lowerCaseFilter2) && curFilter2.equals("Assignee")) {
//							match = match && true;
//						} else if (incident.getRequester().toLowerCase().contains(lowerCaseFilter2) && curFilter2.equals("Requester")) {
//							match = match && true;
//						} else if (incident.getSite().toLowerCase().contains(lowerCaseFilter2) && curFilter2.equals("Site")) {
//							match = match && true;
//						} else if (incident.getDept().toLowerCase().contains(lowerCaseFilter2) && curFilter2.equals("Dept")) {
//							match = match && true;
//						} else if (Integer.toString(incident.getTrackedUsersNum()).contains(lowerCaseFilter2) && curFilter2.equals("Tracks #")) {
//							match = match && true;
//						} else {
//							match = false;
//						}
//					}

					return match; // Does not match.
				});
			});

			// TODO: implement 2 filters
			/*
			filterField2.textProperty().addListener((observable, oldValue, newValue) -> {
				filteredUpdatedListOfIncidents.setPredicate(incident -> {

					// Compare first name and last name of every person with filter text.
					String lowerCaseFilter2 = newValue.toLowerCase();
					String lowerCaseFilter = filterField.getText().toLowerCase();
					String curFilter2 = AppSession.getSession().getCurrentFilter2();
					String curFilter = AppSession.getSession().getCurrentFilter();

					
					boolean match = false;

					// If filter text is empty, display all persons.
					if (newValue == null || newValue.isEmpty()) {
						match = true;
					} else {
						
						if (incident.getState().toLowerCase().contains(lowerCaseFilter2) && curFilter2.equals("State")) {
							match = true;
						} else if (incident.getTitle().toLowerCase().contains(lowerCaseFilter2) && curFilter2.equals("Title")) {
							match = true;
						} else if (incident.getPriority().toLowerCase().contains(lowerCaseFilter2) && curFilter2.equals("Priority")) {
							match = true;
						} else if (incident.getCat().toLowerCase().contains(lowerCaseFilter2)  && curFilter2.equals("Category")) {
							match = true;
						} else if (incident.getSubcat().toLowerCase().contains(lowerCaseFilter2)  && curFilter2.equals("Subcategory")) {
							match = true;
						} else if (incident.getAssignee().toLowerCase().contains(lowerCaseFilter2) && curFilter2.equals("Assignee")) {
							match = true;
						} else if (incident.getRequester().toLowerCase().contains(lowerCaseFilter2) && curFilter2.equals("Requester")) {
							match = true;
						} else if (incident.getSite().toLowerCase().contains(lowerCaseFilter2) && curFilter2.equals("Site")) {
							match = true;
						} else if (incident.getDept().toLowerCase().contains(lowerCaseFilter2) && curFilter2.equals("Dept")) {
							match = true;
						} else if (Integer.toString(incident.getTrackedUsersNum()).contains(lowerCaseFilter2) && curFilter2.equals("Tracks #")) {
							match = true;
						 else {
							match = false;
						}}
					}
					if (!lowerCaseFilter.isEmpty()) {		
					if (incident.getState().toLowerCase().contains(lowerCaseFilter) && curFilter.equals("State")) {	
						match = match && true;	
					} else if (incident.getTitle().toLowerCase().contains(lowerCaseFilter) && curFilter.equals("Title")) {	
						match = match && true;	
					} else if (incident.getPriority().toLowerCase().contains(lowerCaseFilter) && curFilter.equals("Priority")) {	
						match = match && true;	
					} else if (incident.getCat().toLowerCase().contains(lowerCaseFilter)  && curFilter.equals("Category")) {	
						match = match && true;	
					} else if (incident.getSubcat().toLowerCase().contains(lowerCaseFilter)  && curFilter.equals("Subcategory")) {	
						match = match && true;	
					} else if (incident.getAssignee().toLowerCase().contains(lowerCaseFilter) && curFilter.equals("Assignee")) {	
						match = match && true;	
					} else if (incident.getRequester().toLowerCase().contains(lowerCaseFilter) && curFilter.equals("Requester")) {	
						match = match && true;	
					} else if (incident.getSite().toLowerCase().contains(lowerCaseFilter) && curFilter.equals("Site")) {	
						match = match && true;	
					} else if (incident.getDept().toLowerCase().contains(lowerCaseFilter) && curFilter.equals("Dept")) {	
						match = match && true;	
					} else if (Integer.toString(incident.getTrackedUsersNum()).contains(lowerCaseFilter) && curFilter.equals("Tracks #")) {	
						match = match && true;	
					 else {
							match = false;
						}
					}}

					return match; // Does not match.
				});
			});
			*/

			// 3. Wrap the FilteredList in a SortedList.
			SortedList<Incident> sortedFilterUpdatedListOfIncidents = new SortedList<>(filteredUpdatedListOfIncidents);

			// 4. Bind the SortedList comparator to the TableView comparator.
			sortedFilterUpdatedListOfIncidents.comparatorProperty().bind(incidentTable.comparatorProperty());

			// 5. Add sorted (and filtered) data to the table.
			incidentTable.setItems(sortedFilterUpdatedListOfIncidents);
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
		softwareComboBox.setValue(incident.getSoftware());
		if (incident.getDueOn() != null) {
			datePicker.setValue(incident.getDueOn());
		}
		for (TimeTrack track : incident.getTimeTracks()) {
			track.getRemoveBtn().setDisable(true);
			// TODO: handle this listener to remove tracks
			track.getRemoveBtn().setOnAction((e) -> {
//				AppSession.getSession().removeTimeTrackByEmail(track.getEmail());
//				infoTable.getItems().remove(track);
			});
		}
		trackTable.getItems().clear();
		trackTable.getItems().addAll(incident.getTimeTracks());
	}

	@FXML
	private void handleClearFilterBtn() {
		filterComboBox.setValue(null);
		filterField.setText("");
		handleFilterComboBox();
	}
	@FXML
	private void handleClearFilterBtn2() {
		filterComboBox2.setValue(null);
		filterField2.setText("");
		handleFilterComboBox2();
	}
	
	@FXML
	private void handleIncidentNameType() {

	}

	@FXML
	private void handleFilterComboBox() {
		if (filterComboBox.getValue() == null) {
			AppSession.getSession().setCurrentFilter("");
			filterField.setDisable(true);
		} else {
			AppSession.getSession().setCurrentFilter(filterComboBox.getValue());
			filterField.setDisable(false);
			// TODO
			// filterComboBox.getValue() into some sort of list for smarter filter

		}
	}
	
	@FXML
	private void handleFilterComboBox2() {
		if (filterComboBox2.getValue() == null) {
			AppSession.getSession().setCurrentFilter("");
			filterField2.setDisable(true);
		} else {
			AppSession.getSession().setCurrentFilter(filterComboBox2.getValue());
			filterField2.setDisable(false);
			// TODO
			// filterComboBox.getValue() into some sort of list for smarter filter

		}
	}

	@FXML
	private void handleUserTokenFieldChange() {
		AppSession.getSession().setUserToken(userTokenField.getText());
		AppSession.getSession().saveData();
	}

	@FXML
	private void handleDefaultDomainFieldChange() {
		AppSession.getSession().setDefaultDomain(domainField.getText());
		AppSession.getSession().saveData();
	}

	@FXML
	private void handleRequesterFieldChange() {
		updateDefaultDeptSite();
		updateIncidentNamePrompt();
		requesterField.setText(toShortDomain(requesterField.getText()));
	}

	@FXML
	public void handleCatChange() {
		if (catChoiceBox.getValue() != null) {
			setDisableSoftwareComboBox(!catChoiceBox.getValue().equals("Software"));
			if (!catChoiceBox.getValue().equals("Software")) {
				softwareComboBox.setValue(null);
			}
		}
		updateIncidentNamePrompt();
	}


	@FXML
	private void handleDefaultRequesterFieldChange() {
		AppSession.getSession().setDefaultRequester(defaultRequesterField.getText());
		if (AppSession.getSession().getUsers().keySet().contains(defaultRequesterField.getText())) {
			requesterField.setText(toShortDomain(AppSession.getSession().getDefaultRequester()));
			defaultRequesterField.setText(AppSession.getSession().getDefaultRequester());
			handleRequesterFieldChange();
			AppSession.getSession().saveData();
		}
	}

	@FXML
	private void handleDefaultAssigneeFieldChange() {
		AppSession.getSession().setDefaultAssignee(defaultAssigneeField.getText());
		if (AppSession.getSession().getUsers().keySet().contains(defaultAssigneeField.getText())) {
			assigneeField.setText(toShortDomain(AppSession.getSession().getDefaultAssignee()));
			defaultAssigneeField.setText(AppSession.getSession().getDefaultAssignee());
			handleAssigneeFieldChange();
			AppSession.getSession().saveData();
		}
	}

	@FXML
	private void handleDefaultDomainChange() {
		AppSession.getSession().setDefaultDomain(domainField.getText());
		AppSession.getSession().saveData();
	}
	
	
	@FXML
	private void handleAssigneeFieldChange() {
		userInputField.setText(toShortDomain(assigneeField.getText()));
		assigneeField.setText(toShortDomain(assigneeField.getText()));
	}

	@FXML
	private void handleUserInputFieldChange() {
		userInputField.setText(toShortDomain(userInputField.getText()));
	}


	@FXML
	private void handleUpdateDtbBtn() {
		Task<Parent> updateCheck = new Task<Parent>() {
			@Override
			public Parent call() throws JsonIOException, IOException {
				updateMessage("Updating...");
				try {
					AppSession.getSession().updateAll();
				} catch (ParserConfigurationException | SAXException e) {
					printError(e);
				}
				updateMessage("Done.");

				return null;
			}
		};

		// method to set labeltext
		updateDtbBtn.textProperty().bind(Bindings.convert(updateCheck.messageProperty()));
		updateDtbBtn.setDisable(true);
		updateCheck.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
					showAlert("Check complete", "Database is Up-to-date." + updatePrompt, AlertType.INFORMATION);
			

			}
		});
		Thread updateThread = new Thread(updateCheck);
		updateThread.start();
	}
	
	
	//the following 3 methods are from https://code.makery.ch/blog/javafx-dialogs-official/
	@FXML
	private void handleAddRmvBtn() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation");
		alert.setHeaderText("You're about to edit the native (local) data!");
		alert.setContentText("Choose your option.");

		ButtonType buttonTypeOne = new ButtonType("Add");
		ButtonType buttonTypeTwo = new ButtonType("Remove");
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() != buttonTypeCancel) {
			String actionName = result.get().getText();
			String dataName = chooseDataToEditAlert(actionName);
			editDataAlert(actionName, dataName);
			AppSession.getSession().saveData();
			setupPriorityChoiceBox();
			setupStatesChoiceBox();
			showAlert("Confirmation", "Done!", AlertType.INFORMATION);
		} else {
		   alert.close();
		}
	}
	// how to make this method depends on handleAddRmvBtn()?
	private String chooseDataToEditAlert(String actionName) {
		
		//
		ArrayList<String> choices = new ArrayList<>();
		choices.addAll(AppSession.getSession().getNatives());

		ChoiceDialog<String> dialog = new ChoiceDialog<>("Native data:", choices);
		dialog.setTitle("Choice Dialog");
		dialog.setHeaderText("Choose the type of native data you want to " + actionName.toUpperCase() + " :");
		dialog.setContentText("Available native data: ");

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			return result.get();
		}
		return null;
	}

	// how to make this method depends on handleAddRmvBtn()?
	private void editDataAlert(String actionName, String dataName) {
		if (actionName.equalsIgnoreCase("add")) {
			TextInputDialog dialog = new TextInputDialog("");
			dialog.setTitle("Text Input Dialog");
			dialog.setHeaderText("Adding data for " + dataName);
			dialog.setContentText("Please enter the name:");

			// Traditional way to get the response value.
			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()) {

				if (dataName.equalsIgnoreCase("priority")) {
					boolean containsSearchStr = AppSession.getSession().getPriorities().stream()
							.anyMatch(result.get()::equalsIgnoreCase);

					if (containsSearchStr == true) {
						showAlert("Error", result.get() + " in " + dataName.toUpperCase() + " already existed",
								AlertType.WARNING);
						editDataAlert(actionName, dataName);
					} else {
						AppSession.getSession().getPriorities().add(result.get());
					}

				} else if (dataName.equalsIgnoreCase("state")) {
					boolean containsSearchStr = AppSession.getSession().getPriorities().stream()
							.anyMatch(result.get()::equalsIgnoreCase);
					if (containsSearchStr == true) {
						showAlert("Error", "\"" + result.get() + "\" in " + dataName.toUpperCase() + " already existed!",
								AlertType.WARNING);
						editDataAlert(actionName, dataName);
					} else {
						AppSession.getSession().getStates().add(result.get());
					}
				}
			}
		} else if (actionName.equalsIgnoreCase("remove")) {
			ArrayList<String> choices = new ArrayList<>();
			if (dataName.equalsIgnoreCase("priority")) {
				choices.addAll(AppSession.getSession().getPriorities());
			} else if (dataName.equalsIgnoreCase("state")) {
				choices.addAll(AppSession.getSession().getStates());
			}

			ChoiceDialog<String> dialog = new ChoiceDialog<>("", choices);
			dialog.setTitle("Choice Dialog");
			dialog.setHeaderText("Choose a data to remove from " + dataName.toUpperCase());
			Optional<String> result = dialog.showAndWait();

			if (result.isPresent()) {
				// System.out.println("Your choice: " + result.get());
				AppSession.getSession().getPriorities().remove(result.get());
				AppSession.getSession().getStates().remove(result.get());
			}

		}
	}

	// LINEBREAK TEMPLATES ------------------------------------------------
	@FXML
	private TableView<Incident> templateTable;
	@FXML
	private ComboBox<String> tempStateComboBox;
	@FXML
	private ComboBox<String> tempCatComboBox;
	@FXML
	private Label tempSubcatLabel;
	@FXML
	private ComboBox<String> tempSubcatComboBox;
	@FXML
	private Label tempSoftwareLabel;
	@FXML
	private ComboBox<String> tempSoftwareComboBox;

	@FXML
	private TextField tempNameField;
	@FXML
	private TextField tempReqField;
	@FXML
	private TextField tempAsgField;
	@FXML
	private TextField tempIncidentNameField;
	@FXML
	private TextArea tempDescField;

	@FXML
	private TextField tempTrackEmailField;
	@FXML
	private TextField tempTrackCmtField;
	@FXML
	private TextField tempTrackTimeField;

	@FXML
	private TableView<TimeTrack> tempTrackTable;

	@FXML
	private ChoiceBox<String> tempPriorityChoiceBox;
	@FXML
	private ComboBox<String> tempDeptComboBox;
	@FXML
	private ComboBox<String> tempSiteComboBox;
	@FXML
	private DatePicker tempDatePicker;
	@FXML
	private BorderPane templatePane;
	@FXML
	private Tab templateTab;

	private IncidentEditType templateEdit;

	private Incident currentTemplate;

	private void setupTemplateTab() {
		templatePane.setDisable(true);
		templateTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("ID"));
		templateTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("editBtn"));
		templateTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("rmBtn"));

		tempTrackTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));
		tempTrackTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("email"));
		tempTrackTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("time"));
		tempTrackTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("comment"));
		tempTrackTable.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("removeBtn"));

		tempDatePicker.setConverter(new StringConverter<LocalDate>() {
			@Override
			public String toString(LocalDate t) {
				if (t != null) {
					return formatter.format(t);
				}
				return "";
			}

			@Override
			public LocalDate fromString(String string) {
				if (string != null && !string.trim().isEmpty()) {
					return LocalDate.parse(string, formatter);
				}
				return null;
			}
		});
		tempStateComboBox.getItems().addAll(AppSession.getSession().getStates());
		tempCatComboBox.getItems().addAll(AppSession.getSession().getCategories().keySet());
		tempSoftwareComboBox.getItems().addAll(AppSession.getSession().getSoftwares().keySet());
		TextFields.bindAutoCompletion(tempSoftwareComboBox.getEditor(), tempSoftwareComboBox.getItems());
		tempPriorityChoiceBox.getItems().addAll(AppSession.getSession().getPriorities());
		tempDeptComboBox.getItems().addAll(AppSession.getSession().getDepartments());
		TextFields.bindAutoCompletion(tempDeptComboBox.getEditor(), tempDeptComboBox.getItems());
		tempSiteComboBox.getItems().addAll(AppSession.getSession().getSites());
		TextFields.bindAutoCompletion(tempSiteComboBox.getEditor(), tempSiteComboBox.getItems());

		TextFields.bindAutoCompletion(tempReqField, savedEmailprovider);
		TextFields.bindAutoCompletion(tempAsgField, savedEmailprovider);
		TextFields.bindAutoCompletion(tempTrackEmailField, savedEmailprovider);
		updateTemplatesTable();

		tempCatComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				updateTempSubcatChoiceBox();
				if (newValue != null) {
					setDisableTempSoftwareComboBox(!newValue.equals("Software"));
				}
			}
		});
	}

	private void setDisableTempSubcatChoiceBox(boolean disable) {
		tempSubcatComboBox.setDisable(disable);
		tempSubcatLabel.setDisable(disable);
	}

	private void updateTempSubcatChoiceBox() {
		tempSubcatComboBox.getItems().clear();
		if (tempCatComboBox.getValue() == null) {
			setDisableTempSubcatChoiceBox(true);
		} else if (AppSession.getSession().getCategories().get(tempCatComboBox.getValue()).isEmpty()) {
			setDisableTempSubcatChoiceBox(true);
		} else {
			setDisableTempSubcatChoiceBox(false);
			tempSubcatComboBox.getItems()
					.addAll(AppSession.getSession().getCategories().get(tempCatComboBox.getValue()));
		}
	}

	private void setDisableTempSoftwareComboBox(boolean disable) {
		tempSoftwareComboBox.setDisable(disable);
		tempSoftwareLabel.setDisable(disable);
	}

	@FXML
	private void handleTemplateSaveBtn() {
		ArrayList<TimeTrack> newTracks = new ArrayList<TimeTrack>();
		newTracks.addAll(AppSession.getSession().getTemplateTimeTracks());
		Incident template = new Incident(tempNameField.getText(), "0", tempStateComboBox.getValue(),
				tempIncidentNameField.getText(), tempPriorityChoiceBox.getValue(), tempCatComboBox.getValue(),
				tempSubcatComboBox.getValue(), toShortDomain(tempAsgField.getText()),
				toShortDomain(tempReqField.getText()), tempSiteComboBox.getValue(), tempDeptComboBox.getValue(),
				tempDescField.getText(), tempDatePicker.getValue(), null, tempSoftwareComboBox.getValue(), newTracks, "");
		if ((templateEdit == IncidentEditType.NEW
				|| (templateEdit == IncidentEditType.EDIT && (!template.getID().equals(currentTemplate.getID()))))
				&& AppSession.getSession().getTemplates().keySet().contains(tempNameField.getText())) {
			showAlert("Warning", "A template called " + tempNameField.getText()
					+ " already exists.\nPlease choose a different name.", AlertType.WARNING);
		} else {
			AppSession.getSession().addTemplate(tempNameField.getText(), template);
		}
		clearTemplate();
		updateTemplatesTable();

	}

	@FXML
	private void handleTemplateChange() {
		// awdas
		if (templateComboBox.getValue() != null) {
			if (AppSession.getSession().getTemplates().keySet().contains(templateComboBox.getValue())) {
				System.err.println("Chosen template: " + templateComboBox.getValue());
				Incident chosenTemplate = AppSession.getSession().getTemplates().get(templateComboBox.getValue());
				System.err.println(chosenTemplate.toString());
				if (chosenTemplate.getState() != null) {
					statesChoiceBox.setValue(chosenTemplate.getState());
				}

				if (chosenTemplate.getRequester() != null) {
					requesterField.setText(chosenTemplate.getRequester());
				}

				if (chosenTemplate.getTitle() != null) {
					incidentNameField.setText(chosenTemplate.getTitle());
				}
				if (chosenTemplate.getCategory() != null) {
					catChoiceBox.setValue(chosenTemplate.getCategory());
					if (chosenTemplate.getSubcategory() != null) {
						subcatChoiceBox.setValue(chosenTemplate.getSubcategory());
					}
				}

				if (chosenTemplate.getAssignee() != null) {
					assigneeField.setText(chosenTemplate.getAssignee());
				}
				if (chosenTemplate.getDescription() != null) {
					descField.setText(chosenTemplate.getDescription());
				}
				if (chosenTemplate.getPriority() != null) {
					priorityChoiceBox.setValue(chosenTemplate.getPriority());
				}
				if (chosenTemplate.getDept() != null) {
					deptComboBox.setValue(chosenTemplate.getDept());
				}
				if (chosenTemplate.getSite() != null) {
					siteComboBox.setValue(chosenTemplate.getSite());
				}
				if (chosenTemplate.getSoftware() != null) {
					softwareComboBox.setValue(chosenTemplate.getSoftware());
				}

				if (chosenTemplate.getDueOn() != null) {
					datePicker.setValue(chosenTemplate.getDueOn());
				}

				if (!chosenTemplate.getTimeTracks().isEmpty()) {
					AppSession.getSession().getTimeTracks().clear();
					AppSession.getSession().getTimeTracks().addAll(chosenTemplate.getTimeTracks());
					trackTable.getItems().clear();
					for (TimeTrack track: chosenTemplate.getTimeTracks()) {
						addTableItem(track);
					}
				}

			} else {
				System.err.println("There's no such template in the library.");
			}

		}
	}

	@FXML
	private void handleNewTemplateBtn() {
		clearTemplate();
		templateEdit = IncidentEditType.NEW;
		templateTab.setText("+ Create");
		templatePane.setDisable(false);
	}

	@FXML
	private void handleTempAddTrackBtn() {

		if (!AppSession.getSession().containTrackedUser(tempTrackEmailField.getText())) {
			if (tempTrackEmailField.getText().equals("")) {
				showAlert("Error", "Email empty", AlertType.ERROR);
			} else if (tempTrackTimeField.getText().equals("")) {
				showAlert("Error", "Time elapsed empty", AlertType.ERROR);
			} else if (tempTrackCmtField.getText().equals("")) {
				showAlert("Error", "Time track comment empty", AlertType.ERROR);
			} else if (userTokenField.getText().trim().equals("")) {
				showAlert("Error", "User Token missing", AlertType.ERROR);
			} else {
				if (!AppSession.getSession().getUsers().keySet()
						.contains(toCorrectDomain(tempTrackEmailField.getText()))) {
					showAlert("Error", "Cannot find any users with that email. Try again", AlertType.ERROR);
				} else {
					User user = AppSession.getSession().getUsers().get(toCorrectDomain(tempTrackEmailField.getText()));
					TimeTrack track = new TimeTrack(user, Integer.parseInt(tempTrackTimeField.getText()),
							tempTrackCmtField.getText());
					track.getRemoveBtn().setOnAction((e) -> {
						AppSession.getSession().getTemplateTimeTracks().remove(track);
						tempTrackTable.getItems().remove(track);
					});
					tempTrackTable.getItems().add(track);
					AppSession.getSession().getTemplateTimeTracks().add(track);
					tempTrackEmailField.requestFocus();
					tempTrackEmailField.clear();
					tempTrackTimeField.clear();
					tempTrackCmtField.clear();
				}
			}
		}

	}

	

	private void updateTemplatesTable() {
		templateTable.getItems().clear();
		for (String number : AppSession.getSession().getTemplates().keySet()) {
			Incident template = AppSession.getSession().getTemplates().get(number);
			template.getEditBtn().setOnAction((e) -> {
				initializeEditTemplate(template);

			});
			template.getRmBtn().setOnAction((e) -> {
				AppSession.getSession().removeTemplate(number);

				updateTemplatesTable();
			});
			templateTable.getItems().add(template);
		}
		if (AppSession.getSession().getCurrentIncidents().keySet().isEmpty()) {
			templateTable.setPlaceholder(new Label("You have no templates."));
		}
		// DEFAULT SORT BY NAME
		templateTable.getColumns().get(0).setSortType(TableColumn.SortType.ASCENDING);
		templateTable.getSortOrder().add(templateTable.getColumns().get(0));
		templateComboBox.getItems().clear();
		templateComboBox.getSelectionModel().select(0);
		templateComboBox.getItems().addAll(AppSession.getSession().getTemplates().keySet());

	}

	private void initializeEditTemplate(Incident template) {
		templateEdit = IncidentEditType.EDIT;
		currentTemplate = template;
		fetchTemplateInfo();
		templateTab.setText("Edit: " + currentTemplate.getID());
		templatePane.setDisable(false);
	}

	private void fetchTemplateInfo() {
		clearTemplate();
		tempNameField.setText(currentTemplate.getID());
		tempStateComboBox.setValue(currentTemplate.getState());
		tempCatComboBox.setValue(currentTemplate.getCategory());
		tempSubcatComboBox.setValue(currentTemplate.getSubcategory());
		tempSoftwareComboBox.setValue(currentTemplate.getSoftware());
		tempReqField.setText(currentTemplate.getRequester());
		tempAsgField.setText(currentTemplate.getAssignee());
		tempIncidentNameField.setText(currentTemplate.getTitle());
		tempDescField.setText(currentTemplate.getDescription());

		for (int i = 0; i < currentTemplate.getTimeTracks().size(); i++) {
			TimeTrack track = currentTemplate.getTimeTracks().get(i);
			int index = i;
			track.getRemoveBtn().setOnAction((e) -> {
				currentTemplate.getTimeTracks().remove(index);
			});
			tempTrackTable.getItems().add(track);
		}

		tempPriorityChoiceBox.setValue(currentTemplate.getPriority());
		tempDeptComboBox.setValue(currentTemplate.getDept());
		tempSiteComboBox.setValue(currentTemplate.getSite());
		tempDatePicker.setValue(currentTemplate.getDueOn());
	}

	private void clearTemplate() {
		tempNameField.setText("");
		tempStateComboBox.setValue(null);
		tempCatComboBox.setValue(null);
		tempSubcatComboBox.setValue(null);
		tempSoftwareComboBox.setValue(null);
		tempReqField.setText("");
		tempAsgField.setText("");
		tempIncidentNameField.setText("");
		tempDescField.setText("");

		tempTrackEmailField.setText("");
		tempTrackCmtField.setText("");
		tempTrackTimeField.setText("");

		tempTrackTable.getItems().clear();

		tempPriorityChoiceBox.setValue(null);
		tempDeptComboBox.setValue(null);
		tempSiteComboBox.setValue(null);
		tempDatePicker.setValue(null);
		templatePane.setDisable(true);
	}

	private void printError(Exception e) {
		showAlert("ERROR", "Please refer to log/error.txt for debugging:\n" + e.getStackTrace(), AlertType.ERROR);
		
		System.err.println("\n" + LocalDate.now() + "------\n");
		e.printStackTrace();
		System.exit(1);
	}

	private String toCorrectDomain(String email) {
		if (!email.contains("@") && !email.trim().equals("")) {
			return (email + "@" + AppSession.getSession().getDefaultDomain()).toLowerCase();
		} else {
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
	
	@FXML
	private void handleAboutMenuItem() {
		
		String title = "About";
		String msg = "Collaborators: Nathan Truong @nguyentruong17, Minh Ta @minhta16  \r\n" + 
				"This project aims to solve ITS Department's work order processing insufficiency.  \r\n" + 
				"It implements JavaFX and RESTful for an user-friendly application designed for fast and on-the-fly ticket entry. \r\n"+
				"© Augustana ITS Department 2019\n"
				+ "Version: 0.0.8";
		
		showAlert(title, msg, AlertType.INFORMATION);
	}
}

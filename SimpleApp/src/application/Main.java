package application;
	
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Optional;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import application.controller.MainPaneController;
import application.data.AppSession;
import application.data.SamanageRequests;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/MainPane.fxml"));
        Parent root = (Parent)loader.load();
        Scene scene = new Scene(root);
               
        MainPaneController controller = (MainPaneController)loader.getController();

        primaryStage.setTitle("Customized Samanage Experience");
        primaryStage.getIcons().add(new Image("file:./resources/img/appIcon32.ico"));
        primaryStage.setScene(scene);
        
    	PrintStream erroutStream;
		erroutStream = new PrintStream(new FileOutputStream("./logs/error.txt", true));
		
		// TODO: reenable
		System.setErr(erroutStream);
		
        
        System.out.println("Loading...");
		AppSession.getSession().loadData();
		if (AppSession.getSession().getUserToken().isEmpty()) {
			AppSession.getSession().setUserToken(getToken());
			AppSession.getSession().saveData();
		}
		try {
			AppSession.getSession().updateEasyStuff();
		} catch (IOException | ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}

        
        String token = AppSession.getSession().getUserToken();
        int realUserSize = 0;
        int realSiteSize = 0;
		try {
			realUserSize = SamanageRequests.getTotalElements(token, "users");
			realSiteSize = SamanageRequests.getTotalElements(token, "sites");
	        AppSession.getSession().updateUsersMultiThreads();  
	        AppSession.getSession().updateSitesMultiThreads(); 
		} catch (ParserConfigurationException | SAXException e1) {

			printError(e1);
		}
        while(AppSession.getSession().getUsers().size() < realUserSize && AppSession.getSession().getSites().size() < realSiteSize) {
        	try {
				Thread.sleep((long) 5);
			} catch (InterruptedException e) {
				printError(e);
			}
        }
        
        controller.setStageAndSetupListeners(primaryStage);
        primaryStage.show();
        controller.showPrompt();
	
	}
	public static void main(String[] args) {
		launch(args);
	}
	
	private String getToken() {
		String userToken = "";
		// https://code.makery.ch/blog/javafx-dialogs-official/
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("User Token Missing");
		dialog.setHeaderText("You need an user token to use the application");
		dialog.setContentText("Please enter your user token: ");

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			userToken = result.get().trim();
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Error");
			alert.setContentText("You need to enter a valid user token!");
			alert.showAndWait();
			System.exit(0);
		}
		
		boolean tokenIsWrong = true;
		while (tokenIsWrong) {
			tokenIsWrong = false;
			try {
				SamanageRequests.getID(userToken);
			} catch (IOException | ParserConfigurationException | SAXException e) {
				tokenIsWrong = true;
				dialog = new TextInputDialog();
				dialog.setTitle("User Token Missing");
				dialog.setHeaderText("Invalid user token");
				dialog.setContentText("Please enter your user token: ");
				result = dialog.showAndWait();
				if (result.isPresent()) {
					userToken = result.get().trim();
				} else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Error");
					alert.setContentText("You need to enter a valid user token!");
					alert.showAndWait();
					System.exit(0);
				}
			}
		}

		return userToken;
	}
	private void printError(Exception e) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("ERROR");
		alert.setHeaderText("Please refer to log/error.txt for debugging:\n" + e.getStackTrace());
		alert.showAndWait();
		
		System.err.println("\n" + LocalDate.now() + "------\n");
		e.printStackTrace();
		System.exit(1);
	}
}

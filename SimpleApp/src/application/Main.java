package application;
	
import java.io.IOException;

import application.controller.MainPaneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/MainPane.fxml"));
        Parent root = (Parent)loader.load();
        Scene scene = new Scene(root);
        
        MainPaneController controller = (MainPaneController)loader.getController();
        controller.setStageAndSetupListeners(primaryStage);
        
        primaryStage.setTitle("Simple App");
        primaryStage.setScene(scene);
        primaryStage.show();
	
	}
	public static void main(String[] args) {
		launch(args);
	}
}

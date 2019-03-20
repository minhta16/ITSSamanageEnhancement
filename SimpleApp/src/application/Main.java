package application;
	
import java.io.IOException;

import application.controller.MainPaneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/MainPane.fxml"));
        Parent root = (Parent)loader.load();
        Scene scene = new Scene(root);
        
        MainPaneController controller = (MainPaneController)loader.getController();
        controller.setStageAndSetupListeners(primaryStage);
        
        primaryStage.setTitle("Customized Samanage Experience");
//        primaryStage.getIcons().add(new Image("file:/resources/img/favicon.jpg"));
        primaryStage.setScene(scene);
        primaryStage.show();
        controller.showPrompt();
	
	}
	public static void main(String[] args) {
		launch(args);
	}
}

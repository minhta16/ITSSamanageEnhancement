package application;
	
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import application.controller.MainPaneController;
import application.data.AppSession;
import application.data.SamanageRequests;
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

        primaryStage.setTitle("Customized Samanage Experience");
        primaryStage.getIcons().add(new Image("file:./resources/img/appIcon32.ico"));
        primaryStage.setScene(scene);
        
    	PrintStream erroutStream;
		erroutStream = new PrintStream(new FileOutputStream("./logs/error.txt", true));
		
//		TODO: re-enable this
//		System.setErr(erroutStream);
        
        System.out.println("Loading...");
		AppSession.getSession().loadData();
		try {
			AppSession.getSession().updateEasyStuff();
		} catch (IOException e) {
			e.printStackTrace();
		}

        
        String token = AppSession.getSession().getUserToken();
        int realUserSize = SamanageRequests.getTotalElements(token, "users");
        int realSiteSize = SamanageRequests.getTotalElements(token, "sites");
        AppSession.getSession().updateUsersMultiThreads();  
        AppSession.getSession().updateSitesMultiThreads(); 
        while(AppSession.getSession().getUsers().size() < realUserSize && AppSession.getSession().getSites().size() <realSiteSize) {
        	try {
				Thread.sleep((long) 1.5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        controller.setStageAndSetupListeners(primaryStage);
        primaryStage.show();
        controller.showPrompt();
	
	}
	public static void main(String[] args) {
		launch(args);
	}
}

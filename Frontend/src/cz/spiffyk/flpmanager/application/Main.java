package cz.spiffyk.flpmanager.application;

import java.io.IOException;
import java.util.List;

import cz.spiffyk.flpmanager.ManagerFileHandler;
import cz.spiffyk.flpmanager.application.screens.main.MainScreen;
import cz.spiffyk.flpmanager.data.Workspace;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		MainScreen mainScreen = new MainScreen();
		Scene scene = new Scene(mainScreen, 640, 600);
		
		List<String> stylesheets = scene.getStylesheets();
		stylesheets.add(getClass().getResource("application.css").toExternalForm());
		stylesheets.add(getClass().getResource("controls/controls.css").toExternalForm());
		
		try {
			final Workspace workspace = ManagerFileHandler.loadWorkspace("test_workspace");
			mainScreen.setWorkspace(workspace);
		
			primaryStage.setScene(scene);
			primaryStage.setTitle("FLP Manager");
			primaryStage.setOnCloseRequest((e) -> {
				ManagerFileHandler.saveWorkspace(workspace);
				Platform.exit();
			});
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
			Platform.exit();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

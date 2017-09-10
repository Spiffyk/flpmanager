package cz.spiffyk.flpmanager.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import cz.spiffyk.flpmanager.*;
import org.apache.commons.lang3.SystemUtils;

import cz.spiffyk.flpmanager.application.screens.MainScreen;
import cz.spiffyk.flpmanager.application.screens.SetupDialog;
import cz.spiffyk.flpmanager.data.Workspace;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * The application entry point
 * @author spiffyk
 */
public class Main extends Application {
	
	/**
	 * App configuration
	 */
	private static final AppConfiguration appConfiguration = AppConfiguration.get();
	
	/**
	 * Text manager
	 */
	private static final Text text = Text.get();
	


	@Override
	public void start(Stage primaryStage) {
		if (appConfiguration.isLoaded()) {
			startApplication(primaryStage);
		} else {
			SetupDialog setupDialog = new SetupDialog();
			setupDialog.showAndWait().ifPresent((b) -> {
				if (b.booleanValue()) {
					appConfiguration.save();
					startApplication(primaryStage);
				}
			});
		}
	}
	
	/**
	 * Gets the main application ready and starts it
	 * @param primaryStage The main window
	 */
	public void startApplication(Stage primaryStage) {
		Platform.setImplicitExit(false);
		
		MainScreen mainScreen = new MainScreen(primaryStage);
		Scene scene = new Scene(mainScreen, 640, 600);
		
		scene.getStylesheets().add(getClass().getClassLoader().getResource("css/style.css").toExternalForm());
		
		try {
			final Workspace workspace = ManagerFileHandler.loadWorkspace(appConfiguration.getWorkspacePath());
			if (!workspace.lock()) {
				final Alert alert = new Alert(AlertType.WARNING);
				alert.setHeaderText(text.get("init.last_exit_not_graceful"));
				alert.setContentText(text.get("init.last_exit_not_graceful_content"));
				alert.showAndWait();
			}
			mainScreen.setWorkspace(workspace);
		
			primaryStage.setScene(scene);
			primaryStage.setTitle(text.get("application.name"));
			primaryStage.setOnCloseRequest((e) -> {
				appConfiguration.save();
				ManagerFileHandler.saveWorkspace(workspace);
				workspace.unlock();
				Platform.exit();
			});
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
			
			final Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText(text.get("init.startup_error"));
			alert.setContentText(e.getMessage());
			alert.showAndWait();
			Platform.exit();
		} catch (ManagerFileException e) {
			e.printStackTrace();
			
			final Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText(text.get("init.workspace_error"));
			alert.setContentText(e.getMessage());
			alert.showAndWait();
			Platform.exit();
		} catch (WorkspaceInUseException e) {
			e.printStackTrace();

			final Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText(null);
			alert.setContentText(text.get("init.workspace_in_use"));
			alert.showAndWait();
			Platform.exit();
		}
	}
	
	/**
	 * Application entry point
	 * @param args Unused (for now)
	 */
	public static void main(String[] args) {
		appConfiguration.load();
		launch(args);
	}
}

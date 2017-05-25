package cz.spiffyk.flpmanager.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.SystemUtils;

import cz.spiffyk.flpmanager.AppConfiguration;
import cz.spiffyk.flpmanager.ManagerFileException;
import cz.spiffyk.flpmanager.ManagerFileHandler;
import cz.spiffyk.flpmanager.application.screens.main.MainScreen;
import cz.spiffyk.flpmanager.data.Workspace;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Main extends Application {
	
	public static final String CONFIG_DIRECTORY_NAME = "flpmanager";
	
	public static final File CONFIG_DIRECTORY;
	public static final File CONFIG_FILE;
	
	static {
		if (SystemUtils.IS_OS_WINDOWS) {
			CONFIG_DIRECTORY = new File(System.getenv("AppData") + File.separator + CONFIG_DIRECTORY_NAME);
		} else if (SystemUtils.IS_OS_MAC) {
			CONFIG_DIRECTORY = new File(SystemUtils.getUserHome(), "Library/Application Support/" + CONFIG_DIRECTORY_NAME);
		} else {
			CONFIG_DIRECTORY = new File(SystemUtils.getUserHome(), "." + CONFIG_DIRECTORY_NAME);
		}
		
		if (!CONFIG_DIRECTORY.exists()) {
			CONFIG_DIRECTORY.mkdir();
		}
		
		CONFIG_FILE = new File(CONFIG_DIRECTORY, "flpmanager.properties");
	}
	
	private static AppConfiguration appConfiguration = AppConfiguration.get();
	
	private static void loadConfiguration() {
		Properties props = new Properties();
		
		if (CONFIG_FILE.exists()) {
			try {
				FileInputStream fis = new FileInputStream(CONFIG_FILE);
				props.load(fis);
				fis.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		appConfiguration.fromProperties(props);
	}
	
	private static void saveConfiguration() {
		Properties props = appConfiguration.toProperties();
		try {
			FileOutputStream fos = new FileOutputStream(CONFIG_FILE);
			props.store(fos, "FLPManager config file");
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void start(Stage primaryStage) {
		MainScreen mainScreen = new MainScreen();
		Scene scene = new Scene(mainScreen, 640, 600);
		
		List<String> stylesheets = scene.getStylesheets();
		stylesheets.add(getClass().getResource("application.css").toExternalForm());
		stylesheets.add(getClass().getResource("controls/controls.css").toExternalForm());
		
		try {
			final Workspace workspace = ManagerFileHandler.loadWorkspace(appConfiguration.getWorkspacePath());
			mainScreen.setWorkspace(workspace);
		
			primaryStage.setScene(scene);
			primaryStage.setTitle("FLP Manager");
			primaryStage.setOnCloseRequest((e) -> {
				saveConfiguration();
				ManagerFileHandler.saveWorkspace(workspace);
				Platform.exit();
			});
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
			
			final Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Startup error");
			alert.setContentText(e.getMessage());
			alert.showAndWait();
			Platform.exit();
		} catch (ManagerFileException e) {
			e.printStackTrace();
			
			final Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Error in workspace file");
			alert.setContentText(e.getMessage());
			alert.showAndWait();
			Platform.exit();
		}
	}
	
	public static void main(String[] args) {
		loadConfiguration();
		launch(args);
	}
}

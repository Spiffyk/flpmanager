package cz.spiffyk.flpmanager.application.screens.main;

import java.io.IOException;

import cz.spiffyk.flpmanager.AppConfiguration;
import cz.spiffyk.flpmanager.ManagerFileHandler;
import cz.spiffyk.flpmanager.application.controls.SongsView;
import cz.spiffyk.flpmanager.application.screens.about.AboutDialog;
import cz.spiffyk.flpmanager.application.screens.helper.ProjectHelperDialog;
import cz.spiffyk.flpmanager.application.screens.settings.SettingsDialog;
import cz.spiffyk.flpmanager.application.screens.songeditor.SongEditorDialog;
import cz.spiffyk.flpmanager.data.Project;
import cz.spiffyk.flpmanager.data.Workspace;
import cz.spiffyk.flpmanager.util.Messenger;
import cz.spiffyk.flpmanager.util.Messenger.MessageType;
import cz.spiffyk.flpmanager.util.Messenger.Subscriber;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The controller for the main screen
 * @author spiffyk
 */
public class MainScreen extends VBox implements Subscriber {
	
	/**
	 * App configuration
	 */
	private static final AppConfiguration appConfiguration = AppConfiguration.get();
	
	/**
	 * Messenger
	 */
	private static final Messenger messenger = Messenger.get();
	
	/**
	 * The current opened workspace
	 */
	private Workspace workspace;
	
	/**
	 * The main window
	 */
	private Stage primaryStage;
	
	/**
	 * Project helper
	 */
	private ProjectHelperDialog projectHelper = new ProjectHelperDialog();
	
	

	/**
	 * The treeview showing the songs and underlying projects
	 */
	@FXML private SongsView songsView;
	
	
	
	/**
	 * Creates a new main screen
	 * @param primaryStage
	 */
	public MainScreen(Stage primaryStage) {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("MainScreen.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		this.primaryStage = primaryStage;
		
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			Platform.exit();
		}
	}

	
	
	/**
	 * Sets the workspace to work with
	 * @param workspace
	 */
	public void setWorkspace(Workspace workspace) {
		this.workspace = workspace;
		songsView.setWorkspace(workspace);
	}
	
	/**
	 * Initializes the window
	 */
	@FXML protected void initialize() {
		messenger.addListener(this);
	}
	
	/**
	 * Fired when New Song menu item is selected
	 */
	@FXML protected void newSongAction() {
		SongEditorDialog.createNewSong(workspace);
	}
	
	/**
	 * Fired when About menu item is selected
	 */
	@FXML protected void showAbout() {
		final AboutDialog dialog = new AboutDialog();
		dialog.showAndWait();
	}
	
	/**
	 * Fired when Settings menu item is selected
	 */
	@FXML protected void openSettings() {
		final SettingsDialog dialog = new SettingsDialog();
		dialog.showAndWait();
	}
	
	/**
	 * Fired when Save Workspace menu item is selected
	 */
	@FXML protected void saveWorkspace() {
		ManagerFileHandler.saveWorkspace(workspace);
	}
	
	/**
	 * Fired when Quit menu item is selected
	 * @param e Event
	 */
	@FXML protected void quitMenuAction(ActionEvent e) {
		Platform.exit();
	}
	
	
	
	/**
	 * Creates an info box with the specified content
	 * @param content The content
	 */
	private void info(String content) {
		info(null, content);
	}
	
	/**
	 * Creates an info box with the specified header and content
	 * @param header The header
	 * @param content The content
	 */
	private void info(String header, String content) {
		alert(AlertType.INFORMATION, header, content);
	}
	
	/**
	 * Creates a warning box with the specified content
	 * @param content The content
	 */
	private void warning(String content) {
		warning(null, content);
	}
	
	/**
	 * Creates a warning box with the specified header and content
	 * @param header The header
	 * @param content The content
	 */
	private void warning(String header, String content) {
		alert(AlertType.WARNING, header, content);
	}
	
	/**
	 * Creates an error box with the specified content
	 * @param content The content
	 */
	private void error(String content) {
		error(null, content);
	}
	
	/**
	 * Creates an error box with the specified header and content
	 * @param header The header
	 * @param content The content
	 */
	private void error(String header, String content) {
		alert(AlertType.ERROR, header, content);
	}
	
	/**
	 * Creates a alert with the specified type, header and content
	 * @param alertType The alert type
	 * @param header The header
	 * @param content The content
	 */
	private void alert(AlertType alertType, String header, String content) {
		final Alert alert = new Alert(alertType);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	@Override
	public void onMessage(MessageType type, Object... args) {
		switch (type) {
		case HIDE_STAGE:
			primaryStage.hide();
			break;
		case SHOW_STAGE:
			primaryStage.show();
			break;
		case INFO:
			if (args.length >= 2) {
				info((String) args[0], (String) args[1]);
			} else if (args.length == 1) {
				info((String) args[0]);
			}
			break;
		case WARNING:
			if (args.length >= 2) {
				warning((String) args[0], (String) args[1]);
			} else if (args.length == 1) {
				warning((String) args[0]);
			}
			break;
		case ERROR:
			if (args.length >= 2) {
				error((String) args[0], (String) args[1]);
			} else if (args.length == 1) {
				error((String) args[0]);
			}
			break;
		case PROJECT_OPEN:
			if (args.length < 1) {
				throw new IllegalArgumentException("Project needs to be specified");
			}
			
			if (args[0] instanceof Project) {
				projectHelper.setProject((Project) args[0]);
				projectHelper.show();
			}
			primaryStage.hide();
			break;
		case PROJECT_CLOSE:
			projectHelper.hide();
			primaryStage.show();
			break;
		}
	}
}

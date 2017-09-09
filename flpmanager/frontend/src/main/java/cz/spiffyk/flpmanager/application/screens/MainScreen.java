package cz.spiffyk.flpmanager.application.screens;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import cz.spiffyk.flpmanager.AppConfiguration;
import cz.spiffyk.flpmanager.ManagerFileHandler;
import cz.spiffyk.flpmanager.Text;
import cz.spiffyk.flpmanager.UpdateChecker;
import cz.spiffyk.flpmanager.UpdateChecker.UpdateInfo;
import cz.spiffyk.flpmanager.application.views.songs.SongsView;
import cz.spiffyk.flpmanager.data.Project;
import cz.spiffyk.flpmanager.data.Workspace;
import cz.spiffyk.flpmanager.util.Messenger;
import cz.spiffyk.flpmanager.util.Messenger.MessageType;
import cz.spiffyk.flpmanager.util.Messenger.Subscriber;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
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
	 * Text manager
	 */
	private static final Text text = Text.get();
	
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
	private ProjectHelper projectHelper = new ProjectHelper();
	
	

	/**
	 * The treeview showing the songs and underlying projects
	 */
	@FXML private SongsView songsView;

	@FXML private CheckBox hideNotFavoritedCheckBox;
	
	
	
	/**
	 * Creates a new main screen
	 * @param primaryStage
	 */
	public MainScreen(Stage primaryStage) {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/MainScreen.fxml"));
		loader.setResources(text.getResourceBundle());
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
		
		if (appConfiguration.isAutoUpdateCheck()) {
			checkForUpdates(true);
		}

		hideNotFavoritedCheckBox.setSelected(appConfiguration.isHideNotFavorited());
	}
	
	/**
	 * Fired when About menu item is selected
	 */
	@FXML protected void showAbout() {
		final AboutDialog dialog = new AboutDialog();
		dialog.initOwner(primaryStage);
		dialog.showAndWait();
	}
	
	/**
	 * Fired when Settings menu item is selected
	 */
	@FXML protected void openSettings() {
		final SettingsDialog dialog = new SettingsDialog();
		dialog.initOwner(primaryStage);
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
	 */
	@FXML protected void quitMenuAction() {
		Platform.exit();
	}
	
	/**
	 * Fired when Open workspace directory menu item is selected
	 */
	@FXML protected void openWorkspaceDir() {
		workspace.openInSystemBrowser();
	}
	
	/**
	 * In a separate thread, checks for updates.
	 */
	@FXML protected void checkForUpdates() {
		checkForUpdates(false);
	}

	/**
	 * Fired when Hide Unfavorited checkbox is clicked
	 * @param event The event fired by the checkbox
	 */
	@FXML protected void unfavoritedCheck(final ActionEvent event) {
		if (event.getSource() instanceof CheckBox) {
			final boolean selected = ((CheckBox) event.getSource()).isSelected();
			songsView.setHidingUnfavorited(selected);
			appConfiguration.setHideNotFavorited(selected);
		}
	}
	
	/**
	 * In a separate thread, checks for updates and opens a dialog if a new version has been released.<br />
	 * If quiet mode is set to {@code true}, the user won't be notified of the check until an update is available.
	 * @param quiet
	 */
	private void checkForUpdates(final boolean quiet) {
		final Stage status = new StatusWindow(text.get("main_screen.update_check"));
		status.initOwner(primaryStage);
		
		if (!quiet) {
			status.show();
		}
		
		final Task<UpdateInfo> task = new Task<UpdateInfo>() {
			@Override
			protected UpdateInfo call() throws Exception {
				UpdateInfo info = UpdateChecker.getUpdate();
				return info;
			}
		};
		
		task.setOnSucceeded((event) -> {
			UpdateInfo info = null;
			try {
				info = task.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			
			if (status.isShowing()) {
				status.hide();
			}
			
			if (info != null) {
				Dialog<Boolean> dialog = new UpdateDialog(info);
				dialog.initOwner(primaryStage);
				dialog.showAndWait();
			} else if (!quiet) {
				info(text.get("main_screen.up_to_date"));
			}
		});
		
		task.setOnCancelled((event) -> {
			if (status.isShowing()) {
				status.hide();
			}
		});
		
		task.setOnFailed((event) -> {
			if (status.isShowing()) {
				status.hide();
			}
			
			if (!quiet) {
				error(text.get("main_screen.update_fail"), event.getSource().getException().toString());
			}
		});
		
		new Thread(task).start();
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
		alert.initOwner(primaryStage);
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
				
				double x = primaryStage.getX() + primaryStage.getWidth() / 2 - projectHelper.getWidth() / 2;
				double y = primaryStage.getY() + primaryStage.getHeight() / 2 - projectHelper.getHeight() / 2;
				projectHelper.setX(x);
				projectHelper.setY(y);
			}
			primaryStage.hide();
			break;
		case PROJECT_CLOSE:
			primaryStage.show();
			
			double x = projectHelper.getX() + projectHelper.getWidth() / 2 - primaryStage.getWidth() / 2;
			double y = projectHelper.getY() + projectHelper.getHeight() / 2 - primaryStage.getHeight() / 2;
			primaryStage.setX(x);
			primaryStage.setY(y);
			
			projectHelper.hide();
			break;
		}
	}
}

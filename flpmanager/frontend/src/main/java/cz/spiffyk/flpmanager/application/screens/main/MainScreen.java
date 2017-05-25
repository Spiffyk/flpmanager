package cz.spiffyk.flpmanager.application.screens.main;

import java.io.IOException;

import cz.spiffyk.flpmanager.AppConfiguration;
import cz.spiffyk.flpmanager.application.controls.SongsView;
import cz.spiffyk.flpmanager.application.screens.generator.SongEditorDialog;
import cz.spiffyk.flpmanager.data.Song;
import cz.spiffyk.flpmanager.data.Workspace;
import cz.spiffyk.flpmanager.util.Messenger;
import cz.spiffyk.flpmanager.util.Messenger.MessageType;
import cz.spiffyk.flpmanager.util.Messenger.Subscriber;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainScreen extends BorderPane implements Subscriber {
	
	private static final AppConfiguration appConfiguration = AppConfiguration.get();
	private static final Messenger messenger = Messenger.get();
	
	private Workspace songManager;
	private Stage primaryStage;
	
	public MainScreen(Stage primaryStage) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
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

	@FXML private MenuBar menuBar;
	@FXML private BorderPane mainPane;
	@FXML private Label songName;
	@FXML private SongsView songsView;
	
	/**
	 * Sets the workspace to work with
	 * @param workspace
	 */
	public void setWorkspace(Workspace workspace) {
		this.songManager = workspace;
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
	 * @param e Event
	 */
	@FXML protected void newSongAction(ActionEvent e) {
		Song song = new Song();
		SongEditorDialog dialog = new SongEditorDialog(song);
		dialog.showAndWait().ifPresent(s -> {
			if (s.booleanValue()) {
				songManager.getSongs().add(song);
			}
		});
	}
	
	@FXML protected void openSettings(ActionEvent e) {
		error("Not yet implemented");
	}
	
	/**
	 * Fired when Quit menu item is selected
	 * @param e Event
	 */
	@FXML protected void quitMenuAction(ActionEvent e) {
		Platform.exit();
	}
	
	private void info(String content) {
		info(null, content);
	}
	
	private void info(String header, String content) {
		alert(AlertType.INFORMATION, header, content);
	}
	
	private void warning(String content) {
		warning(null, content);
	}
	
	private void warning(String header, String content) {
		alert(AlertType.WARNING, header, content);
	}
	
	private void error(String content) {
		error(null, content);
	}
	
	private void error(String header, String content) {
		alert(AlertType.ERROR, header, content);
	}
	
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
		}
	}
}

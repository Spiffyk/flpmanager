package cz.spiffyk.flpmanager.application.screens.main;

import java.io.IOException;

import cz.spiffyk.flpmanager.application.controls.SongsView;
import cz.spiffyk.flpmanager.application.screens.generator.SongGeneratorDialog;
import cz.spiffyk.flpmanager.data.Workspace;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;

public class MainScreen extends BorderPane {
	
	private Workspace songManager;
	
	public MainScreen() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		
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
	}
	
	/**
	 * Fired when New Song menu item is selected
	 * @param e Event
	 */
	@FXML protected void newSongAction(ActionEvent e) {
		SongGeneratorDialog dialog = new SongGeneratorDialog();
		dialog.showAndWait().ifPresent(s -> {
			songManager.getSongs().add(s);
		});
	}
	
	/**
	 * Fired when Quit menu item is selected
	 * @param e Event
	 */
	@FXML protected void quitMenuAction(ActionEvent e) {
		Platform.exit();
	}
}

package cz.spiffyk.flpmanager.application.screens.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.spiffyk.flpmanager.SongManager;
import cz.spiffyk.flpmanager.application.controls.SongTreeItem;
import cz.spiffyk.flpmanager.application.screens.generator.SongGeneratorDialog;
import cz.spiffyk.flpmanager.application.screens.songs.SongsView;
import cz.spiffyk.flpmanager.data.Song;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;

public class MainScreen extends BorderPane {
	
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
	 * Sets the song manager to work with
	 * @param songManager
	 */
	public void setSongManager(SongManager songManager) {
		songsView.setSongManager(songManager);
	}
	
	/**
	 * Initializes the window
	 */
	@FXML protected void initialize() {
		/* Set treeview listener */
		songsView.getSelectionModel().selectedItemProperty().addListener(this::songSelectedAction);
	}
	
	/**
	 * Fired when a tree item is selected
	 * @param observable
	 * @param oldValue Deselected tree item
	 * @param newValue Selected tree item
	 */
	protected void songSelectedAction(ObservableValue<?> observable, Object oldValue, Object newValue) {
		if (newValue instanceof SongTreeItem) {
			final SongTreeItem item = (SongTreeItem) newValue;
			final Song song = item.getSong();
			System.out.println("Selected: " + song.toString() + " (favorite: " + song.isFavorite() + ")");
		}
	}
	
	/**
	 * Fired when New Song menu item is selected
	 * @param e Event
	 */
	@FXML protected void newSongAction(ActionEvent e) {
		SongGeneratorDialog dialog = new SongGeneratorDialog();
		dialog.showAndWait().ifPresent(s -> {
			System.out.println(s);
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

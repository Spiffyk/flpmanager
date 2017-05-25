package cz.spiffyk.flpmanager.application.screens.generator;

import java.io.IOException;

import cz.spiffyk.flpmanager.data.Song;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;

public class SongEditorDialog extends Dialog<Boolean> {
	
	@FXML private TextField name;
	@FXML private TextField author;
	
	private Song song;
	
	public SongEditorDialog(Song song) {
		super();
		this.song = song;
		this.setTitle("Edit song...");
		
		final FXMLLoader loader = new FXMLLoader(getClass().getResource("SongGenerator.fxml"));
		loader.setController(this);
		this.setResultConverter(this::convertResult);
		
		try {
			this.setDialogPane((DialogPane) loader.load());
		} catch (IOException e) {
			e.printStackTrace();
			Platform.exit();
		}
	}
	
	@FXML private void initialize() {
		this.name.setText(song.getName());
		this.author.setText(song.getAuthor());
	}
	
	/**
	 * Creates a song from the values in the dialog
	 * @param b
	 * @return
	 */
	private boolean convertResult(ButtonType b) {
		if (b.equals(ButtonType.OK)) {
			song.setName(name.getText());
			song.setAuthor(author.getText());
			return true;
		} else {
			return false;
		}
	}
	
}

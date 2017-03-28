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

public class SongGeneratorDialog extends Dialog<Song> {
	
	@FXML private TextField name;
	@FXML private TextField author;
	
	public SongGeneratorDialog() {
		super();
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
	
	/**
	 * Creates a song from the values in the dialog
	 * @param b
	 * @return
	 */
	private Song convertResult(ButtonType b) {
		if (b.equals(ButtonType.OK)) {
			final Song song = new Song();
			song.setName(name.getText());
			song.setAuthor(author.getText());
			return song;
		} else {
			return null;
		}
	}
	
}

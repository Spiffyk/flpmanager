package cz.spiffyk.flpmanager.application.screens;

import java.io.IOException;

import cz.spiffyk.flpmanager.data.Song;
import cz.spiffyk.flpmanager.data.Workspace;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class SongEditorDialog extends Dialog<Boolean> {
	
	@FXML private TextField name;
	@FXML private TextField author;
	
	private Song song;
	
	public SongEditorDialog(Song song) {
		super();
		this.song = song;
		this.setTitle("Edit song...");
		
		final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("SongEditor.fxml"));
		loader.setController(this);
		this.setResultConverter(this::convertResult);
		
		try {
			DialogPane pane = new DialogPane();
			
			pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
			pane.setContent(loader.load());
			
			final Button btOk = (Button) pane.lookupButton(ButtonType.OK);
			btOk.addEventFilter(ActionEvent.ACTION, this::onOk);
			
			this.setDialogPane(pane);
		} catch (IOException e) {
			e.printStackTrace();
			Platform.exit();
		}
	}
	
	
	
	public static void createNewSong(Workspace workspace) {
		Song song = new Song();
		SongEditorDialog dialog = new SongEditorDialog(song);
		dialog.showAndWait().ifPresent(s -> {
			if (s.booleanValue()) {
				song.setParent(workspace);
				workspace.getSongs().add(song);
			}
		});
	}
	
	
	
	@FXML private void initialize() {
		this.name.setText(song.getName());
		this.author.setText(song.getAuthor());
		
		
	}
	
	private void onOk(ActionEvent event) {
		if (name.getText().trim().isEmpty()) {
			event.consume();
			
			final Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText(null);
			alert.setContentText("The song name cannot be empty!");
			alert.showAndWait();
			return;
		}
		
		song.setName(name.getText());
		song.setAuthor(author.getText());
	}
	
	private boolean convertResult(ButtonType b) {
		return b.equals(ButtonType.OK);
	}
	
}

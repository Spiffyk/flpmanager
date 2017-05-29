package cz.spiffyk.flpmanager.application.screens;

import java.io.IOException;

import cz.spiffyk.flpmanager.data.Tag;
import cz.spiffyk.flpmanager.data.Workspace;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class TagEditorDialog extends Dialog<Boolean> {
	
	@FXML private TextField name;
	@FXML private ColorPicker color;
	
	private final Tag tag;
	
	public TagEditorDialog(Tag tag) {
		super();
		this.tag = tag;
		this.setTitle("Edit song...");
		
		final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("TagEditor.fxml"));
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
	
	public static TagEditorDialog newTagDialog(Workspace workspace) {
		Tag tag = new Tag(workspace);
		TagEditorDialog dialog = new TagEditorDialog(tag);
		dialog.setOnHidden((event) -> {
			if (dialog.getResult().booleanValue()) {
				workspace.addTag(tag);
			}
		});
		
		return dialog;
	}
	
	
	
	@FXML private void initialize() {
		name.setText(tag.getName());
		color.setValue(tag.getColor());
	}
	
	private void onOk(ActionEvent event) {
		if (name.getText().trim().isEmpty()) {
			event.consume();
			
			final Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(this.getDialogPane().getScene().getWindow());
			alert.setHeaderText(null);
			alert.setContentText("The tag name cannot be empty!");
			alert.showAndWait();
			return;
		}
		
		tag.setName(name.getText().trim());
		tag.setColor(color.getValue());
	}
	
	private Boolean convertResult(ButtonType buttonType) {
		return buttonType == ButtonType.OK;
	}
}

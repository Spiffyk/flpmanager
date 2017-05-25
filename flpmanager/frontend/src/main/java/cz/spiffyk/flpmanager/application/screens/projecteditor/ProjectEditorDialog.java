package cz.spiffyk.flpmanager.application.screens.projecteditor;

import java.io.IOException;

import cz.spiffyk.flpmanager.data.Project;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;

public class ProjectEditorDialog extends Dialog<Boolean> {
	
	private final Project project;
	
	@FXML TextField name;
	
	public ProjectEditorDialog(Project project) {
		super();
		this.project = project;
		this.setTitle("Edit project...");
		
		final FXMLLoader loader = new FXMLLoader(getClass().getResource("ProjectEditor.fxml"));
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
		name.setText(project.getName());
	}
	
	private boolean convertResult(ButtonType b) {
		if (b.equals(ButtonType.OK)) {
			project.setName(name.getText());
			return true;
		} else {
			return false;
		}
	}
}

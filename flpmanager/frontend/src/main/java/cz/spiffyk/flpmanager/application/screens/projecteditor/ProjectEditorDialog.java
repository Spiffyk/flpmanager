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

/**
 * The controller for the project editing dialog
 * @author spiffyk
 */
public class ProjectEditorDialog extends Dialog<Boolean> {
	
	/**
	 * The project to edit
	 */
	private final Project project;
	
	/**
	 * The text field containing the name of the project
	 */
	@FXML TextField name;
	
	/**
	 * Creates a new dialog for editing the specified project
	 * @param project The project to edit
	 */
	public ProjectEditorDialog(Project project) {
		super();
		this.project = project;
		this.setTitle("Edit project...");
		
		final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ProjectEditor.fxml"));
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
	 * Initializes the name field with the current project's name
	 */
	@FXML private void initialize() {
		name.setText(project.getName());
	}
	
	/**
	 * Called when a dialog button is clicked.<br />
	 * If {@code OK} is clicked, the name of the project gets set.
	 * @param b
	 * @return {@code true} if {@code OK} was clicked, otherwise {@code false}
	 */
	private boolean convertResult(ButtonType b) {
		if (b.equals(ButtonType.OK)) {
			project.setName(name.getText());
			return true;
		} else {
			return false;
		}
	}
}

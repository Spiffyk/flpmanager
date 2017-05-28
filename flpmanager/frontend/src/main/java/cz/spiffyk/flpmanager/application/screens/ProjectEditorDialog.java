package cz.spiffyk.flpmanager.application.screens;

import java.io.IOException;

import cz.spiffyk.flpmanager.data.Project;
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
			final DialogPane pane = new DialogPane();
			pane.setContent(loader.load());
			pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
			
			final Button btOk = (Button) pane.lookupButton(ButtonType.OK);
			btOk.addEventFilter(ActionEvent.ACTION, this::onOk);
			
			this.setDialogPane(pane);
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
	
	private void onOk(ActionEvent event) {
		if (name.getText().trim().isEmpty()) {
			event.consume();
			
			final Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(this.getDialogPane().getScene().getWindow());
			alert.setHeaderText(null);
			alert.setContentText("The project name cannot be empty!");
			alert.showAndWait();
			return;
		}
		
		project.setName(name.getText());
	}
	
	/**
	 * Called when a dialog button is clicked.<br />
	 * If {@code OK} is clicked, the name of the project gets set.
	 * @param b
	 * @return {@code true} if {@code OK} was clicked, otherwise {@code false}
	 */
	private boolean convertResult(ButtonType b) {
		return b == ButtonType.OK;
	}
}

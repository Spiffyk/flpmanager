package cz.spiffyk.flpmanager.application.screens.settings;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.lang3.SystemUtils;

import com.sun.javafx.scene.shape.PathUtils;

import cz.spiffyk.flpmanager.AppConfiguration;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * The controller for the settings screen
 * @author spiffyk
 */
public class SettingsDialog extends Dialog<Boolean> {
	
	/**
	 * App configuration
	 */
	private static final AppConfiguration appConfiguration = AppConfiguration.get();
	
	
	
	/**
	 * Path to FL Studio executable
	 */
	File pathToExeFile = new File(appConfiguration.getFlExecutablePath());
	
	/**
	 * Text field containing path to FL Studio executable
	 */
	@FXML private TextField pathToExe;
	
	/**
	 * File chooser for FL Studio executable
	 */
	private final FileChooser exeFileChooser = new FileChooser();
	{
		exeFileChooser.setTitle("Select path to FL Studio executable");
		exeFileChooser.setInitialDirectory(pathToExeFile);
		exeFileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("Executable", "*.exe", "*.bat", "*.sh"),
				new ExtensionFilter("Windows Executable", "*.exe"),
				new ExtensionFilter("Batch file", "*.bat"),
				new ExtensionFilter("Shell script", "*.sh"),
				new ExtensionFilter("All files", "*.*"));
	}
	
	
	
	/**
	 * Path to FLP template
	 */
	File pathToTemplateFile = new File(appConfiguration.getFlpTemplatePath());
	
	/**
	 * Text field containing path to FLP template
	 */
	@FXML private TextField pathToTemplate;
	
	/**
	 * File chooser for FLP template
	 */
	private final FileChooser templateFileChooser = new FileChooser();
	{
		templateFileChooser.setTitle("Select path to the default template");
		templateFileChooser.setInitialDirectory(pathToTemplateFile);
		templateFileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("FL Studio project file", "*.flp"),
				new ExtensionFilter("All files", "*.*"));
	}
	
	
	
	/**
	 * Text field containing path to workspace
	 */
	@FXML private TextField pathToWorkspace;
	
	/**
	 * Directory chooser for workspace
	 */
	private final DirectoryChooser workspaceDirChooser = new DirectoryChooser();
	{
		workspaceDirChooser.setTitle("Select path to your workspace");
	}
	
	
	/**
	 * Is set to {@code true} if workspace directory is modified by the user
	 */
	private boolean workspaceModified = false;
	
	
	
	
	
	/**
	 * Creates a new settings dialog
	 */
	public SettingsDialog() {
		super();
		this.setTitle("Settings");
		
		final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("SettingsDialog.fxml"));
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
	 * Initializes the fields to the current configured values
	 */
	@FXML private void initialize() {
		pathToExe.setText(appConfiguration.getFlExecutablePath());
		pathToTemplate.setText(appConfiguration.getFlpTemplatePath());
		pathToWorkspace.setText(appConfiguration.getWorkspacePath());
	}
	
	/**
	 * Called when path to executable button is clicked
	 */
	@FXML private void setPathToExe() {
		File f = exeFileChooser.showOpenDialog(null);
		if (f != null) {
			pathToExe.setText(f.getAbsolutePath());
		}
	}
	
	/**
	 * Called when path to template button is clicked
	 */
	@FXML private void setPathToTemplate() {
		File f = templateFileChooser.showOpenDialog(null);
		if (f != null) {
			pathToTemplate.setText(f.getAbsolutePath());
		}
	}
	
	/**
	 * Called when the user types in the workspace text field
	 */
	@FXML private void changedWorkspace() {
		workspaceModified = true;
	}
	
	/**
	 * Called when the path to workspace button is clicked
	 */
	@FXML private void setPathToWorkspace() {
		File f = workspaceDirChooser.showDialog(null);
		if (f != null) {
			pathToWorkspace.setText(f.getAbsolutePath());
			workspaceModified = true;
		}
	}
	
	/**
	 * Called when one of the dialog buttons is clicked.<br />
	 * If {@code OK} is clicked, saves the settings.<br />
	 * If the workspace directory has been modified, shows an alert about the necessity of a restart for the changes
	 * to take effect.
	 * @param buttonType
	 * @return
	 */
	private Boolean convertResult(ButtonType buttonType) {
		if (buttonType == ButtonType.OK) {
			if (workspaceModified) {
				final Alert alert = new Alert(AlertType.WARNING);
				alert.setHeaderText("Workspace directory has been changed");
				alert.setContentText("Restart for the changes to take effect");
				alert.showAndWait();
			}
			
			appConfiguration.setFlExecutablePath(pathToExe.getText());
			appConfiguration.setFlpTemplatePath(pathToTemplate.getText());
			appConfiguration.setWorkspacePath(pathToWorkspace.getText());
			return true;
		}
		
		return false;
	}
}
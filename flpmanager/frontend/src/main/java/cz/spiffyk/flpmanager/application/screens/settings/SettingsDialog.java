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

public class SettingsDialog extends Dialog<Boolean> {
	
	private static final AppConfiguration appConfiguration = AppConfiguration.get();
	
	File pathToExeFile = new File(appConfiguration.getFlExecutablePath());
	@FXML private TextField pathToExe;
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
	
	File pathToTemplateFile = new File(appConfiguration.getFlpTemplatePath());
	@FXML private TextField pathToTemplate;
	private final FileChooser templateFileChooser = new FileChooser();
	{
		templateFileChooser.setTitle("Select path to the default template");
		templateFileChooser.setInitialDirectory(pathToTemplateFile);
		templateFileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("FL Studio project file", "*.flp"),
				new ExtensionFilter("All files", "*.*"));
	}
	
	@FXML private TextField pathToWorkspace;
	private final DirectoryChooser workspaceDirChooser = new DirectoryChooser();
	{
		workspaceDirChooser.setTitle("Select path to your workspace");
	}
	
	private boolean workspaceModified = false;
	
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
	
	@FXML private void initialize() {
		pathToExe.setText(appConfiguration.getFlExecutablePath());
		pathToTemplate.setText(appConfiguration.getFlpTemplatePath());
		pathToWorkspace.setText(appConfiguration.getWorkspacePath());
	}
	
	@FXML private void setPathToExe() {
		File f = exeFileChooser.showOpenDialog(null);
		if (f != null) {
			pathToExe.setText(f.getAbsolutePath());
		}
	}
	
	@FXML private void setPathToTemplate() {
		File f = templateFileChooser.showOpenDialog(null);
		if (f != null) {
			pathToTemplate.setText(f.getAbsolutePath());
		}
	}
	
	@FXML private void changedWorkspace() {
		workspaceModified = true;
	}
	
	@FXML private void setPathToWorkspace() {
		File f = workspaceDirChooser.showDialog(null);
		if (f != null) {
			pathToWorkspace.setText(f.getAbsolutePath());
			workspaceModified = true;
		}
	}
	
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

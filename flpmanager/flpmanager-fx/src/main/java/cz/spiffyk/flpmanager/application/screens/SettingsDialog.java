package cz.spiffyk.flpmanager.application.screens;

import java.io.File;
import java.io.IOException;

import cz.spiffyk.flpmanager.AppConfiguration;
import cz.spiffyk.flpmanager.Text;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
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
	 * Language API
	 */
	private static final Text text = Text.get();
	
	
	
	/**
	 * Text field containing path to FL Studio executable
	 */
	@FXML protected TextField pathToExe;
	
	/**
	 * File chooser for FL Studio executable
	 */
	private final FileChooser exeFileChooser = new FileChooser();
	{
		exeFileChooser.setTitle(text.get("settings.fl_exe_title"));
		if (!appConfiguration.getFlExecutablePath().isEmpty()) {
			exeFileChooser.setInitialDirectory(new File(appConfiguration.getFlExecutablePath()));
		}
		exeFileChooser.getExtensionFilters().addAll(
				new ExtensionFilter(text.get("file_type.executable"), "*.exe", "*.bat", "*.sh"),
				new ExtensionFilter(text.get("file_type.exe"), "*.exe"),
				new ExtensionFilter(text.get("file_type.bat"), "*.bat"),
				new ExtensionFilter(text.get("file_type.sh"), "*.sh"),
				new ExtensionFilter(text.get("file_type.all"), "*.*"));
	}
	
	
	
	/**
	 * Text field containing path to FLP template
	 */
	@FXML protected TextField pathToTemplate;
	
	/**
	 * File chooser for FLP template
	 */
	private final FileChooser templateFileChooser = new FileChooser();
	{
		templateFileChooser.setTitle(text.get("settings.template_title"));
		if (!appConfiguration.getFlpTemplatePath().isEmpty()) {
			templateFileChooser.setInitialDirectory(new File(appConfiguration.getFlpTemplatePath()));
		}
		templateFileChooser.getExtensionFilters().addAll(
				new ExtensionFilter(text.get("file_type.flp"), "*.flp"),
				new ExtensionFilter(text.get("file_type.all"), "*.*"));
	}
	
	
	
	/**
	 * Text field containing path to workspace
	 */
	@FXML protected TextField pathToWorkspace;
	
	/**
	 * Directory chooser for workspace
	 */
	private final DirectoryChooser workspaceDirChooser = new DirectoryChooser();
	{
		workspaceDirChooser.setTitle(text.get("settings.workspace_title"));
	}
	
	/**
	 * Is set to {@code true} if workspace directory is modified by the user
	 */
	private boolean workspaceModified = false;
	
	
	
	/**
	 * Check box for automatic update check on startup
	 */
	@FXML private CheckBox autoUpdateCheck;
	
	
	
	/**
	 * Check box for ignoring pre-releases when checking for updates
	 */
	@FXML private CheckBox doUpdatePreReleases;
	
	/**
	 * Is set to {@code true} if ignoring pre-releases state was modified by the user
	 */
	private boolean doUpdatePreReleasesModified = false;
	
	
	
	
	
	/**
	 * Creates a new settings dialog
	 */
	public SettingsDialog() {
		super();
		this.setTitle(text.get("settings.title"));
		
		final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/dialogs/SettingsDialog.fxml"));
		loader.setResources(text.getResourceBundle());
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
	 * Initializes the fields to the current configured values
	 */
	@FXML protected void initialize() {
		pathToExe.setText(appConfiguration.getFlExecutablePath());
		pathToTemplate.setText(appConfiguration.getFlpTemplatePath());
		pathToWorkspace.setText(appConfiguration.getWorkspacePath());
		autoUpdateCheck.setSelected(appConfiguration.isAutoUpdateCheck());
		doUpdatePreReleases.setSelected(!appConfiguration.isIgnoreUpdatePreReleases());
	}
	
	private void onOk(ActionEvent event) {
		File exe = new File(pathToExe.getText());
		File template = new File(pathToTemplate.getText());
		File workspace = new File(pathToWorkspace.getText());
		
		if (!exe.isFile()) {
			event.consume();
			
			final Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(this.getDialogPane().getScene().getWindow());
			alert.setHeaderText(null);
			alert.setContentText(text.get("settings.fl_exe_invalid"));
			alert.showAndWait();
			return;
		}
		
		if (!template.isFile()) {
			event.consume();
			
			final Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(this.getDialogPane().getScene().getWindow());
			alert.setHeaderText(null);
			alert.setContentText(text.get("settings.template_invalid"));
			alert.showAndWait();
			return;
		}
		
		if (workspace.exists() && !workspace.isDirectory()) {
			event.consume();
			
			final Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(this.getDialogPane().getScene().getWindow());
			alert.setHeaderText(null);
			alert.setContentText(text.get("settings.workspace_invalid"));
			alert.showAndWait();
			return;
		}
		
		if (doUpdatePreReleasesModified && doUpdatePreReleases.isSelected()) {
			final Alert alert = new Alert(AlertType.WARNING, null, ButtonType.CANCEL, ButtonType.OK);
			alert.initOwner(this.getDialogPane().getScene().getWindow());
			alert.setHeaderText(text.get("settings.prerelease_warning"));
			alert.setContentText(text.get("settings.prerelease_confirmation"));
			ButtonType b = alert.showAndWait().orElse(ButtonType.CANCEL);
			
			if (b == ButtonType.CANCEL) {
				event.consume();
				return;
			}
		}
		
		if (workspaceModified) {
			final Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(this.getDialogPane().getScene().getWindow());
			alert.setHeaderText(text.get("settings.workspace_warning"));
			alert.setContentText(text.get("settings.workspace_restart"));
			alert.showAndWait();
		}
		
		appConfiguration.setFlExecutablePath(exe.getAbsolutePath());
		appConfiguration.setFlpTemplatePath(template.getAbsolutePath());
		appConfiguration.setWorkspacePath(workspace.getAbsolutePath());
		appConfiguration.setAutoUpdateCheck(autoUpdateCheck.isSelected());
		appConfiguration.setIgnoreUpdatePreReleases(!doUpdatePreReleases.isSelected());
	}
	
	/**
	 * Called when path to executable button is clicked
	 */
	@FXML protected void setPathToExe() {
		File f = exeFileChooser.showOpenDialog(null);
		if (f != null) {
			pathToExe.setText(f.getAbsolutePath());
		}
	}
	
	/**
	 * Called when path to template button is clicked
	 */
	@FXML protected void setPathToTemplate() {
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
	 * Called when the ignore update pre-releases check box is clicked
	 */
	@FXML private void changedDoUpdatePreReleases() {
		doUpdatePreReleasesModified = true;
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
		return buttonType == ButtonType.OK;
	}
}

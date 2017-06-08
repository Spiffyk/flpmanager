package cz.spiffyk.flpmanager.application.screens;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.SystemUtils;

import cz.spiffyk.flpmanager.AppConfiguration;
import cz.spiffyk.flpmanager.Text;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class SetupDialog extends Dialog<Boolean> {
	
	private static final AppConfiguration appConfiguration = AppConfiguration.get();
	
	private static final Text text = Text.get();
	
	private static final String FL_EXE_NAME = "FL.exe";
	private static final String DEFAULT_TEMPLATE_NAME =
			"Data" + File.separator +
			"Projects" + File.separator +
			"Templates" + File.separator +
			"Minimal" + File.separator +
			"Empty" + File.separator +
			"Empty.flp";
	
	@FXML private TextField pathToFl;
	private File pathToFlFile;
	private final DirectoryChooser flDirChooser = new DirectoryChooser();
	{
		flDirChooser.setTitle(text.get("settings.fl_directory_title"));
	}
	
	@FXML private TextField pathToExe;
	private final FileChooser exeFileChooser = new FileChooser();
	{
		exeFileChooser.setTitle(text.get("settings.fl_exe_title"));
		exeFileChooser.setInitialDirectory(pathToFlFile);
		exeFileChooser.getExtensionFilters().addAll(
				new ExtensionFilter(text.get("file_type.executable"), "*.exe", "*.bat", "*.sh"),
				new ExtensionFilter(text.get("file_type.exe"), "*.exe"),
				new ExtensionFilter(text.get("file_type.bat"), "*.bat"),
				new ExtensionFilter(text.get("file_type.sh"), "*.sh"),
				new ExtensionFilter(text.get("file_type.all"), "*.*"));
	}
	
	@FXML private TextField pathToTemplate;
	private final FileChooser templateFileChooser = new FileChooser();
	{
		templateFileChooser.setTitle(text.get("settings.template_title"));
		templateFileChooser.setInitialDirectory(pathToFlFile);
		templateFileChooser.getExtensionFilters().addAll(
				new ExtensionFilter(text.get("file_type.flp"), "*.flp"),
				new ExtensionFilter(text.get("file_type.all"), "*.*"));
	}
	
	@FXML private TextField pathToWorkspace;
	private final DirectoryChooser workspaceDirChooser = new DirectoryChooser();
	{
		workspaceDirChooser.setTitle(text.get("settings.workspace_title"));
	}
	
	
	
	public SetupDialog() {
		super();
		this.setTitle(text.get("settings.first_time_setup_title"));
		
		final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/dialogs/SetupDialog.fxml"));
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
	
	
	
	private void updateFlPaths() {
		pathToExe.setText(new File(pathToFlFile, FL_EXE_NAME).getAbsolutePath());
		pathToTemplate.setText(new File(pathToFlFile, DEFAULT_TEMPLATE_NAME).getAbsolutePath());
	}
	
	@FXML private void initialize() {
		pathToWorkspace.setText(SystemUtils.getUserHome() + File.separator + "FLWorkspace");
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
		
		appConfiguration.setFlExecutablePath(exe.getAbsolutePath());
		appConfiguration.setFlpTemplatePath(template.getAbsolutePath());
		appConfiguration.setWorkspacePath(workspace.getAbsolutePath());
	}
	
	@FXML private void changePathToFlText() {
		pathToFlFile = new File(pathToFl.getText());
		updateFlPaths();
	}
	
	@FXML private void setPathToFl() {
		File f = flDirChooser.showDialog(null);
		if (f != null) {
			pathToFlFile = f;
			pathToFl.setText(f.getAbsolutePath());
			
			updateFlPaths();
		}
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
	
	@FXML private void setPathToWorkspace() {
		File f = workspaceDirChooser.showDialog(null);
		if (f != null) {
			pathToWorkspace.setText(f.getAbsolutePath());
		}
	}
	
	private Boolean convertResult(ButtonType buttonType) {
		return buttonType == ButtonType.OK;
	}
}

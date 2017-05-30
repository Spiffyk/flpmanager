package cz.spiffyk.flpmanager.application.screens;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.SystemUtils;

import cz.spiffyk.flpmanager.AppConfiguration;
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
		flDirChooser.setTitle("Select path to FL Studio");
	}
	
	@FXML private TextField pathToExe;
	private final FileChooser exeFileChooser = new FileChooser();
	{
		exeFileChooser.setTitle("Select path to FL Studio executable");
		exeFileChooser.setInitialDirectory(pathToFlFile);
		exeFileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("Executable", "*.exe", "*.bat", "*.sh"),
				new ExtensionFilter("Windows Executable", "*.exe"),
				new ExtensionFilter("Batch file", "*.bat"),
				new ExtensionFilter("Shell script", "*.sh"),
				new ExtensionFilter("All files", "*.*"));
	}
	
	@FXML private TextField pathToTemplate;
	private final FileChooser templateFileChooser = new FileChooser();
	{
		templateFileChooser.setTitle("Select path to the default template");
		templateFileChooser.setInitialDirectory(pathToFlFile);
		templateFileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("FL Studio project file", "*.flp"),
				new ExtensionFilter("All files", "*.*"));
	}
	
	@FXML private TextField pathToWorkspace;
	private final DirectoryChooser workspaceDirChooser = new DirectoryChooser();
	{
		workspaceDirChooser.setTitle("Select path to your workspace");
	}
	
	
	
	public SetupDialog() {
		super();
		this.setTitle("First time setup");
		
		final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/dialogs/SetupDialog.fxml"));
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
			alert.setContentText("The executable must be a valid file!");
			alert.showAndWait();
			return;
		}
		
		if (!template.isFile()) {
			event.consume();
			
			final Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(this.getDialogPane().getScene().getWindow());
			alert.setHeaderText(null);
			alert.setContentText("The template must be a valid file!");
			alert.showAndWait();
			return;
		}
		
		if (workspace.exists() && !workspace.isDirectory()) {
			event.consume();
			
			final Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(this.getDialogPane().getScene().getWindow());
			alert.setHeaderText(null);
			alert.setContentText("The workspace must be a valid directory or must not exist!");
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

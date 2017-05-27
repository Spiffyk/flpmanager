package cz.spiffyk.flpmanager.application.screens.helper;

import java.io.IOException;

import cz.spiffyk.flpmanager.data.Project;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import lombok.Getter;
import lombok.NonNull;

public class ProjectHelperDialog extends Dialog<Void> {
	
	@Getter private Project project;
	
	@FXML private Label songName;
	@FXML private Label projectName;
	
	public ProjectHelperDialog() {
		this.setOnCloseRequest((e) -> {
			e.consume();
		});
		
		final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ProjectHelperDialog.fxml"));
		loader.setController(this);
		this.setTitle("Current project");
		
		try {
			this.setDialogPane((DialogPane) loader.load());
		} catch (IOException e) {
			e.printStackTrace();
			Platform.exit();
		}
	}
	
	public void setProject(@NonNull Project project) {
		this.project = project;
		update();
	}
	
	private void update() {
		songName.setText(project.getParent().getName());
		projectName.setText(project.getName());
	}
	
	@FXML private void openDirectory() {
		project.getParent().openInSystemBrowser();
	}
}

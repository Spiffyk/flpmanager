package cz.spiffyk.flpmanager.application.screens;

import java.io.IOException;

import cz.spiffyk.flpmanager.data.Project;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.NonNull;

public class ProjectHelper extends Stage {
	
	@Getter private Project project;
	
	@FXML private Label songName;
	@FXML private Label projectName;
	
	public ProjectHelper() {
		super();
		this.initModality(Modality.NONE);
		this.setResizable(false);
		this.setOnCloseRequest((e) -> {
			e.consume();
		});
		
		final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ProjectHelper.fxml"));
		loader.setController(this);
		this.setTitle("Current project");
		
		try {
			final Scene scene = new Scene(loader.load());
			this.setScene(scene);
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

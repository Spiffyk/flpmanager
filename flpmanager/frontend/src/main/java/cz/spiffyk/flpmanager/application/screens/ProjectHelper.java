package cz.spiffyk.flpmanager.application.screens;

import java.io.IOException;

import cz.spiffyk.flpmanager.application.controls.tags.TagsViewer;
import cz.spiffyk.flpmanager.data.Project;
import cz.spiffyk.flpmanager.data.Song;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
	@FXML private Node tagsWrapper;
	@FXML private TagsViewer tagsViewer;
	
	public ProjectHelper() {
		super();
		this.initModality(Modality.NONE);
		this.setResizable(false);
		this.setOnCloseRequest((e) -> {
			e.consume();
		});
		
		final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/ProjectHelper.fxml"));
		loader.setController(this);
		this.setTitle("Current project");
		
		try {
			final Scene scene = new Scene(loader.load());
			scene.getStylesheets().add(getClass().getClassLoader().getResource("css/style.css").toExternalForm());
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
		Song song = project.getParent();
		songName.setText(song.getName());
		projectName.setText(project.getName());
		
		if (song.getTags().isEmpty()) {
			tagsWrapper.setVisible(false);
			tagsWrapper.setManaged(false);
		} else {
			tagsViewer.setTags(song.getTags());
			tagsWrapper.setVisible(true);
			tagsWrapper.setManaged(false);
		}
	}
	
	@FXML private void openDirectory() {
		project.getParent().openInSystemBrowser();
	}
}

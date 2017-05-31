package cz.spiffyk.flpmanager.application.screens;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class StatusWindow extends Stage {
	
	private Label statusLabel;
	
	public StatusWindow(String status) {
		super();
		this.initStyle(StageStyle.UNDECORATED);
		
		this.statusLabel = new Label();
		this.statusLabel.setPadding(new Insets(20));
		this.setStatus(status);
		final Scene scene = new Scene(statusLabel);
		this.setScene(scene);
	}
	
	public void setStatus(String status) {
		statusLabel.setText(status);
	}
}

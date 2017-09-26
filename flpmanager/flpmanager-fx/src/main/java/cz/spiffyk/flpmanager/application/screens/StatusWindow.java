package cz.spiffyk.flpmanager.application.screens;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * A small window for the user to get some feedback about a potentially lengthy process
 * @author spiffyk
 */
public class StatusWindow extends Stage {
	
	/**
	 * The text to show in the window
	 */
	private Label statusLabel;
	
	
	
	/**
	 * Creates a new status window with the provided status text.
	 * @param status The status text to show
	 */
	public StatusWindow(String status) {
		super();
		this.initStyle(StageStyle.UNDECORATED);
		
		this.statusLabel = new Label();
		this.statusLabel.setPadding(new Insets(20));
		this.setStatus(status);
		final Scene scene = new Scene(statusLabel);
		this.setScene(scene);
	}
	
	
	
	/**
	 * Sets the status text
	 * @param status The status text
	 */
	public void setStatus(String status) {
		statusLabel.setText(status);
	}
	
	/**
	 * Gets the status text currently showing
	 * @return The status text
	 */
	public String getStatus() {
		return statusLabel.getText();
	}
}

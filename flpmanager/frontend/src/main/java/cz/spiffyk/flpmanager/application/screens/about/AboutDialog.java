package cz.spiffyk.flpmanager.application.screens.about;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;

public class AboutDialog extends Dialog<Boolean> {
	
	@FXML private Label version;
	
	public AboutDialog() {
		super();
		this.setTitle("About");
		
		final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("AboutDialog.fxml"));
		loader.setController(this);
		
		try {
			this.setDialogPane((DialogPane) loader.load());
		} catch (IOException e) {
			e.printStackTrace();
			Platform.exit();
		}
	}
	
	@FXML private void initialize() {
		this.version.setText("Version: " + getClass().getPackage().getImplementationVersion());
	}
}

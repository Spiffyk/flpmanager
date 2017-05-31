package cz.spiffyk.flpmanager.application.screens;

import java.io.IOException;

import cz.spiffyk.flpmanager.util.FXUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;

/**
 * The controller for the about dialog
 * @author spiffyk
 */
public class AboutDialog extends Dialog<Boolean> {
	
	/**
	 * The label containing the program version
	 */
	@FXML private Label version;
	
	
	
	/**
	 * Creates a new about dialog
	 */
	public AboutDialog() {
		super();
		this.setTitle("About");
		
		final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/dialogs/AboutDialog.fxml"));
		loader.setController(this);
		
		try {
			this.setDialogPane((DialogPane) loader.load());
		} catch (IOException e) {
			e.printStackTrace();
			Platform.exit();
		}
	}
	
	
	
	/**
	 * Initializes the program version label with the version (works only when the program is packaged by maven,
	 * otherwise it shows {@code null})
	 */
	@FXML private void initialize() {
		String version = getClass().getPackage().getImplementationVersion();
		if (version != null) {
			this.version.setText("Version: " + version);
		} else {
			this.version.setText("Unpackaged development build");
		}
	}
	
	@FXML private void mitLicense() {
		FXUtils.openWebPage("https://opensource.org/licenses/MIT");
	}
	
	@FXML private void github() {
		FXUtils.openWebPage("https://github.com/Spiffyk/flpmanager");
	}
	
	@FXML private void bug() {
		FXUtils.openWebPage("https://github.com/Spiffyk/flpmanager/issues");
	}
}

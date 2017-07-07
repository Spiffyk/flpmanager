package cz.spiffyk.flpmanager.application.screens;

import java.io.IOException;

import cz.spiffyk.flpmanager.Text;
import cz.spiffyk.flpmanager.util.ManagerUtils;
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
	
	private static final Text text = Text.get();
	
	/**
	 * The label containing the program version
	 */
	@FXML private Label version;
	
	
	
	/**
	 * Creates a new about dialog
	 */
	public AboutDialog() {
		super();
		this.setTitle(text.get("about.title"));
		
		final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/dialogs/AboutDialog.fxml"));
		loader.setResources(text.getResourceBundle());
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
			this.version.setText(text.get("about.version") + version);
		} else {
			this.version.setText(text.get("about.dev_build"));
		}
	}
	
	@FXML private void lombok() {
		ManagerUtils.openWebPage("https://projectlombok.org/");
	}
	
	@FXML private void apacheCommons() {
		ManagerUtils.openWebPage("https://commons.apache.org/");
	}
	
	@FXML private void gson() {
		ManagerUtils.openWebPage("https://github.com/google/gson");
	}
	
	@FXML private void commonmark() {
		ManagerUtils.openWebPage("https://github.com/atlassian/commonmark-java");
	}
	
	@FXML private void apacheLicense() {
		ManagerUtils.openWebPage("http://www.apache.org/licenses/LICENSE-2.0");
	}
	
	@FXML private void bsd2ClauseLicense() {
		ManagerUtils.openWebPage("https://spdx.org/licenses/BSD-2-Clause.html");
	}
	
	@FXML private void mitLicense() {
		ManagerUtils.openWebPage("https://opensource.org/licenses/MIT");
	}
	
	@FXML private void github() {
		ManagerUtils.openWebPage("https://github.com/Spiffyk/flpmanager");
	}
	
	@FXML private void bug() {
		ManagerUtils.openWebPage("https://github.com/Spiffyk/flpmanager/issues");
	}
}

package cz.spiffyk.flpmanager.application.screens;

import cz.spiffyk.flpmanager.Text;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;

public class FirstTimeSettingsDialog extends SettingsDialog {

	private static final String DEFAULT_TEMPLATE_NAME =
					"Data" + File.separator +
					"Projects" + File.separator +
					"Templates" + File.separator +
					"Minimal" + File.separator +
					"Empty" + File.separator +
					"Empty.flp";

	private static final Text text = Text.get();

	public FirstTimeSettingsDialog() {
		super();
		this.setTitle(text.get("settings.first_time_setup_title"));
	}



	@Override
	protected void initialize() {
		super.initialize();
		pathToWorkspace.setText(SystemUtils.getUserHome() + File.separator + "FLWorkspace");
	}

	@Override
	protected void setPathToExe() {
		super.setPathToExe();
		if (pathToExe.getText() != null) {
			final File file = new File(pathToExe.getText());
			if (file.exists()) {
				final File directory = new File(file.getParentFile(), DEFAULT_TEMPLATE_NAME);
				pathToTemplate.setText(directory.getAbsolutePath());
			}
		}
	}
}

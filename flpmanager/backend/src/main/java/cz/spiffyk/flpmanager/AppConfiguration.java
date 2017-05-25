package cz.spiffyk.flpmanager;

import java.util.Properties;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class AppConfiguration {
	
	private static final AppConfiguration singleton = new AppConfiguration();
	
	// Saved configuration
	@Getter @Setter @NonNull private String flpTemplatePath = "";
	@Getter @Setter @NonNull private String flExecutablePath = "";
	@Getter @Setter @NonNull private String workspacePath = "";
	
	@Getter @Setter private boolean loaded = false;
	
	private AppConfiguration() {}
	
	public static AppConfiguration get() {
		return singleton;
	}
	
	public void fromProperties(Properties properties) {
		setFlpTemplatePath(properties.getProperty("flp_template", ""));
		setFlExecutablePath(properties.getProperty("fl_studio_executable", ""));
		setWorkspacePath(properties.getProperty("workspace", ""));
		setLoaded(true);
	}
	
	public Properties toProperties() {
		final Properties properties = new Properties();
		properties.setProperty("flp_template", getFlpTemplatePath());
		properties.setProperty("fl_studio_executable", getFlExecutablePath());
		properties.setProperty("workspace", getWorkspacePath());
		return properties;
	}
	
}

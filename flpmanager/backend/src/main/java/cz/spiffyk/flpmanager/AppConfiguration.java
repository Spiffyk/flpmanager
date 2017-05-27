package cz.spiffyk.flpmanager;

import java.util.Properties;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * A singleton holding the application configuration
 * @author spiffyk
 */
public class AppConfiguration {
	
	/**
	 * The singleton instance
	 */
	private static final AppConfiguration singleton = new AppConfiguration();
	
	
	
	/**
	 * The path to the FL Studio project template
	 */
	@Getter @Setter @NonNull private String flpTemplatePath = "";
	
	/**
	 * The path to the FL Studio executable
	 */
	@Getter @Setter @NonNull private String flExecutablePath = "";
	
	/**
	 * The path to the user's workspace
	 */
	@Getter @Setter @NonNull private String workspacePath = "";
	
	
	
	/**
	 * Whether the configuration was loaded from the config file. Used to determine whether the first time setup screen
	 * should be shown at startup.
	 */
	@Getter @Setter private boolean loaded = false;
	
	
	
	/**
	 * The private singleton constructor
	 */
	private AppConfiguration() {}
	
	
	
	/**
	 * Gets the singleton instance of {@code AppConfiguration}
	 * @return The instance
	 */
	public static AppConfiguration get() {
		return singleton;
	}
	
	
	
	/**
	 * Loads configuration parameters from a {@link Properties} instance.
	 * @param properties The instance to load parameters from
	 */
	public void fromProperties(Properties properties) {
		setFlpTemplatePath(properties.getProperty("flp_template", ""));
		setFlExecutablePath(properties.getProperty("fl_studio_executable", ""));
		setWorkspacePath(properties.getProperty("workspace", ""));
		setLoaded(true);
	}
	
	/**
	 * Gets a {@link Properties} instance representing the configuration
	 * @return The {@link Properties} instance containing configuration parameters
	 */
	public Properties toProperties() {
		final Properties properties = new Properties();
		properties.setProperty("flp_template", getFlpTemplatePath());
		properties.setProperty("fl_studio_executable", getFlExecutablePath());
		properties.setProperty("workspace", getWorkspacePath());
		return properties;
	}
	
}

package cz.spiffyk.flpmanager;

import java.io.*;
import java.util.Properties;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.SystemUtils;

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
	@Getter @Setter @NonNull private String flpTemplatePath;
	
	/**
	 * The path to the FL Studio executable
	 */
	@Getter @Setter @NonNull private String flExecutablePath;
	
	/**
	 * The path to the user's workspace
	 */
	@Getter @Setter @NonNull private String workspacePath;
	
	/**
	 * Whether FLP Manager should check for updates on startup
	 */
	@Getter @Setter private boolean autoUpdateCheck;
	
	/**
	 * Whether pre-release updates should be ignored
	 */
	@Getter @Setter private boolean ignoreUpdatePreReleases;
	
	
	
	/**
	 * Whether the configuration was loaded from the config file. Used to determine whether the first time setup screen
	 * should be shown at startup.
	 */
	@Getter @Setter private boolean loaded = false;

	/**
	 * The name of the configuration directory (in AppData for Windows, Application Support on OS X, home directory
	 * with preceding fullstop on others)
	 */
	public static final String CONFIG_DIRECTORY_NAME = "flpmanager";

	/**
	 * Configuration directory
	 */
	public static final File CONFIG_DIRECTORY;

	/**
	 * Main configuration file
	 */
	public static final File CONFIG_FILE;

	/*
	 * Here the config directory and file are set based on the system running the application
	 */
	static {
		if (SystemUtils.IS_OS_WINDOWS) {
			CONFIG_DIRECTORY = new File(System.getenv("AppData") + File.separator + CONFIG_DIRECTORY_NAME);
		} else if (SystemUtils.IS_OS_MAC) {
			CONFIG_DIRECTORY = new File(SystemUtils.getUserHome(), "Library/Application Support/" + CONFIG_DIRECTORY_NAME);
		} else {
			CONFIG_DIRECTORY = new File(SystemUtils.getUserHome(), "." + CONFIG_DIRECTORY_NAME);
		}

		if (!CONFIG_DIRECTORY.exists()) {
			CONFIG_DIRECTORY.mkdir();
		}

		CONFIG_FILE = new File(CONFIG_DIRECTORY, "flpmanager.properties");
	}


	
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
	 * Loads the configuration file and feeds it into {@code appConfiguration}.
	 */
	public void load() {
		if (CONFIG_FILE.exists()) {
			Properties props = new Properties();
			try {
				FileInputStream fis = new FileInputStream(CONFIG_FILE);
				props.load(fis);
				fis.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			fromProperties(props);
		}
	}

	/**
	 * Takes the {@code appConfiguration} and stores it in the file
	 */
	public void save() {
		Properties props = toProperties();
		try {
			FileOutputStream fos = new FileOutputStream(CONFIG_FILE);
			props.store(fos, "FLP Manager config file");
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads configuration parameters from a {@link Properties} instance.
	 * @param properties The instance to load parameters from
	 */
	public void fromProperties(Properties properties) {
		setFlpTemplatePath(properties.getProperty("flp_template", ""));
		setFlExecutablePath(properties.getProperty("fl_studio_executable", ""));
		setWorkspacePath(properties.getProperty("workspace", ""));
		setAutoUpdateCheck(Boolean.parseBoolean(properties.getProperty("auto_update_check", "true")));
		setIgnoreUpdatePreReleases(Boolean.parseBoolean(properties.getProperty("ignore_update_pre_releases", "true")));
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
		properties.setProperty("auto_update_check", Boolean.toString(isAutoUpdateCheck()));
		properties.setProperty("ignore_update_pre_releases", Boolean.toString(isIgnoreUpdatePreReleases()));
		return properties;
	}
}

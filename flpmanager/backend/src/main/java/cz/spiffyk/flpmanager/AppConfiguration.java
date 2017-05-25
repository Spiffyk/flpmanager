package cz.spiffyk.flpmanager;

import java.util.Properties;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class AppConfiguration {
	
	private static final AppConfiguration singleton = new AppConfiguration();
	
	@Getter @Setter @NonNull private String flExecutablePath = "";
	
	private AppConfiguration() {}
	
	public static AppConfiguration get() {
		return singleton;
	}
	
	public void fromProperties(Properties properties) {
		setFlExecutablePath(properties.getProperty("fl_studio_executable", ""));
	}
	
	public Properties toProperties() {
		final Properties properties = new Properties();
		properties.setProperty("fl_studio_executable", getFlExecutablePath());
		return properties;
	}
	
}

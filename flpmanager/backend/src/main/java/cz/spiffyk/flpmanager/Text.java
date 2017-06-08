package cz.spiffyk.flpmanager;

import java.util.Locale;
import java.util.ResourceBundle;

import lombok.Getter;
import lombok.NonNull;

public class Text {
	private static final Text singleton = new Text();
	
	
	
	@Getter private ResourceBundle resourceBundle;
	
	
	
	private Text() {
		setLocale(Locale.ENGLISH);
	}
	
	
	
	public static Text get() {
		return singleton;
	}
	
	
	
	public void setLocale(@NonNull Locale locale) {
		resourceBundle = ResourceBundle.getBundle("text.text", locale);
	}
	
	public String get(@NonNull String key) {
		return resourceBundle.getString(key);
	}
}

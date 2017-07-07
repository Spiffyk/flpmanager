package cz.spiffyk.flpmanager.util;

import java.awt.Desktop;
import java.net.URI;

import javafx.concurrent.Task;
import javafx.scene.paint.Color;

/**
 * Utility class for FLP Manager
 * @author spiffyk
 */
public class ManagerUtils {
	
	/**
	 * A regex of characters accepted in a filename
	 */
	public static final String FILE_REGEX = "[a-zA-Z0-9-_ ]+";
	
	/**
	 * The threshold for {@code getTagStyle()} to determine whether the text color should be black or white
	 */
	public static final double LUMINANCE_THRESHOLD = 0.6;
	
	/**
	 * Converts the color into HTML hex notation
	 * @param color
	 * @return
	 */
	public static String toRGBCode(Color color) {
		/*
		 * Courtesy of Moe: https://stackoverflow.com/a/18803814
		 */
        return String.format( "#%02X%02X%02X",
            (int)( color.getRed() * 255 ),
            (int)( color.getGreen() * 255 ),
            (int)( color.getBlue() * 255 ) );
    }
	
	/**
	 * Gets the style string for a tag, automatically coloring the text based on the background color's perceived
	 * luminance.
	 * @param backgroundColor 
	 * @return
	 */
	public static String getTagStyle(Color backgroundColor) {
		return "-fx-background-color: "
				+ ManagerUtils.toRGBCode(backgroundColor) + "; "
				+ "-fx-text-fill: "
				+ ((getLuminance(backgroundColor) < LUMINANCE_THRESHOLD) ? "white" : "black") + ";";
	}
	
	/**
	 * Gets the perceived luminance of the specified color
	 * @param color The color
	 * @return Perceived luminance, a number from {@code 0.0} to {@code 1.0}.
	 */
	public static double getLuminance(Color color) {
		// Perceived luminance
		return (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue());
	}
	
	/**
	 * Opens the specified URL in the system's default web browser
	 * @param url The URL to open
	 */
	public static void openWebPage(String url) {
		new Thread(new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Desktop.getDesktop().browse(new URI(url));
				return null;
			}
		}).start();
	}
}

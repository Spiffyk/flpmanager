package cz.spiffyk.flpmanager.util;

import java.awt.Desktop;
import java.net.URI;

import javafx.concurrent.Task;
import javafx.scene.paint.Color;

/**
 * Utility class for JavaFX
 * @author spiffyk
 */
public class FXUtils {
	
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
	 * 
	 * @param backgroundColor
	 * @return
	 */
	public static String getTagStyle(Color backgroundColor) {
		return "-fx-background-color: "
				+ FXUtils.toRGBCode(backgroundColor) + ";"
				+ "-fx-text-fill: "
				+ ((getLuminance(backgroundColor) < LUMINANCE_THRESHOLD) ? "white" : "black") + ";";
	}
	
	public static double getLuminance(Color color) {
		// Perceived luminance
		return (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue());
	}
	
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

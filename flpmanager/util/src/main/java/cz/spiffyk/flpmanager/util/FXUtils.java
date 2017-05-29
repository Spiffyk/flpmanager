package cz.spiffyk.flpmanager.util;

import javafx.scene.paint.Color;

/**
 * Utility class for JavaFX
 * @author spiffyk
 */
public class FXUtils {
	
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
}

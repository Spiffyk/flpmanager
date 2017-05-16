package cz.spiffyk.flpmanager.data;

import java.util.Observable;

import javafx.scene.paint.Color;

public class Tag extends Observable {

	/**
	 * The lower-case tag name
	 */
	final String name;
	
	/**
	 * The color of the tag
	 */
	private Color color;
	
	/**
	 * Creates a new tag with the specified name. The name gets trimmed and converted to lower-case
	 * @param name The name to assign to the tag
	 */
	public Tag(String name, Color color) {
		this.name = name.trim().toLowerCase();
		this.setColor(color);
	}
	
	/**
	 * Gets the tag name
	 * @return
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Sets the tag's color. Cannot be null, if it is, will throw {@link IllegalArgumentException}.
	 * @param color The color
	 */
	public void setColor(Color color) {
		if (color == null) {
			throw new IllegalArgumentException("The color of the tag cannot be null!");
		}
		
		this.color = color;
		this.setChanged();
		this.notifyObservers();
	}
	
	/**
	 * Gets the tag's color
	 * @return The color
	 */
	public Color getColor() {
		return this.color;
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
}

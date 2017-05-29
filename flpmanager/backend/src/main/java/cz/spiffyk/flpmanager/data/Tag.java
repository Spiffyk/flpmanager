package cz.spiffyk.flpmanager.data;

import java.util.Observable;
import java.util.UUID;

import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.NonNull;

public class Tag extends Observable {

	/**
	 * The unique identifier of the tag
	 */
	@Getter private final UUID identifier;
	
	/**
	 * The parent workspace
	 */
	@Getter private final Workspace parent;
	
	/**
	 * The lower-case tag name
	 */
	@Getter private String name;
	
	/**
	 * The color of the tag
	 */
	@Getter private Color color;
	
	/**
	 * Creates a new tag with the specified name. The name gets trimmed and converted to lower-case
	 * @param name The name to assign to the tag
	 */
	public Tag(@NonNull Workspace parent) {
		this(UUID.randomUUID(), parent);
	}
	
	/**
	 * Creates a new tag with the specified name. The name gets trimmed and converted to lower-case
	 * @param name The name to assign to the tag
	 */
	public Tag(@NonNull UUID identifier, @NonNull Workspace parent) {
		this.identifier = identifier;
		this.parent = parent;
	}
	
	/**
	 * Sets the tag's color. Cannot be null, if it is, will throw {@link IllegalArgumentException}.
	 * @param color The color
	 */
	public void setColor(@NonNull Color color) {
		this.color = color;
		this.setChanged();
		this.notifyObservers();
	}
	
	public void setName(@NonNull String name) {
		this.name = name.trim().toLowerCase();
		this.setChanged();
		this.notifyObservers();
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
}

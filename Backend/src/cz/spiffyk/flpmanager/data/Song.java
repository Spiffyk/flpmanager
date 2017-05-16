package cz.spiffyk.flpmanager.data;

import java.util.Observable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

/**
 * Contains song metadata.
 * @author spiffyk
 */
public class Song extends Observable implements WorkspaceNode {

	private final ObservableList<Project> projects = FXCollections.observableArrayList();
	private final ObservableList<Tag> tags = FXCollections.observableArrayList();
	
	private boolean favorite;
	private String name;
	private String author;
	
	/**
	 * Creates a song with empty name, author and not marked as favorite.
	 */
	public Song() {
		this.setName("");
		this.setAuthor("");
		this.setFavorite(false);
	}
	
	/**
	 * Gets the list of contained projects.
	 * @return {@code List} of contained projects
	 */
	public ObservableList<Project> getProjects() {
		return projects;
	}
	
	/**
	 * Gets the set of tags this project is marked with
	 * @return {@code Set} of tags
	 */
	public ObservableList<Tag> getTags() {
		return tags;
	}
	
	/**
	 * Gets the song name.
	 * @return Song name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Sets the song name. If the given value is {@code null}, it will automatically be set to an empty {@code String}.
	 * @param songName The value to set the song name to
	 */
	public void setName(String songName) {
		this.name = (songName == null) ? "" : songName;
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * Gets the author of the song.
	 * @return Author of the song
	 */
	public String getAuthor() {
		return this.author;
	}
	
	/**
	 * Sets the author of the song. If the given value is {@code null}, it will automatically be set to an empty {@code String}.
	 * @param author The value to set the author of the song to
	 */
	public void setAuthor(String author) {
		this.author = (author == null) ? "" : author;
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * Checks whether the song is marked as favourite
	 * @return {@code true} if marked as favourite, otherwise {@code false}
	 */
	public boolean isFavorite() {
		return favorite;
	}
	
	/**
	 * Sets the favourite mark of the song.
	 * @param favorite {@code true} to mark as favourite, {@code false} to unmark as favourite
	 */
	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
		this.setChanged();
		this.notifyObservers();
	}

	@Override
	public String toString() {
		return ((this.author.isEmpty()) ? "" : this.author + " - ") + this.name;
	}
	
	@Override
	public WorkspaceNodeType getType() {
		return WorkspaceNodeType.SONG;
	}
}

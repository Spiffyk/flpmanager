package cz.spiffyk.flpmanager.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Contains song metadata.
 * @author spiffyk
 */
public class Song implements Serializable {

	private static final long serialVersionUID = -7202442899449549401L;

	private final List<Project> projects = new ArrayList<>();
	private final Set<Tag> tags = new HashSet<>();
	
	private boolean favorite = false;
	private String name = "";
	private String author = "";
	
	/**
	 * Creates a song with empty name, author and not marked as favourite.
	 */
	public Song() {
		this.setName("");
		this.setAuthor("");
	}
	
	/**
	 * Gets the list of contained projects.
	 * @return {@code List} of contained projects
	 */
	public List<Project> getProjects() {
		return projects;
	}
	
	/**
	 * Gets the set of tags this project is marked with
	 * @return {@code Set} of tags
	 */
	public Set<Tag> getTags() {
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
	 * @param favourite {@code true} to mark as favourite, {@code false} to unmark as favourite
	 */
	public void setFavorite(boolean favourite) {
		this.favorite = favourite;
	}

	@Override
	public String toString() {
		return ((this.author.isEmpty()) ? "" : this.author + " - ") + this.name;
	}
}

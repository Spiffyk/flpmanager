package cz.spiffyk.flpmanager.data;

import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class Workspace {
	private final File directory;
	private ObservableList<Song> songs = FXCollections.observableArrayList();
	private ObservableMap<String, Tag> tags = FXCollections.observableHashMap();
	
	public Workspace(String path) {
		this(new File(path));
	}
	
	public Workspace(File directory) {
		if (directory == null) {
			throw new IllegalArgumentException("The workspace directory cannot be null!");
		}
		
		this.directory = directory;
	}
	
	/**
	 * Gets the list of songs in this workspace
	 * @return
	 */
	public ObservableList<Song> getSongs() {
		return songs;
	}
	
	/**
	 * Gets the set of tags in this workspace
	 * @return
	 */
	public ObservableMap<String, Tag> getTags() {
		return tags;
	}
	
	/**
	 * Puts tags into the tags map
	 * @param inputTagIterable The iterable containing the tags to put into the map
	 */
	public void addTags(final Iterable<Tag> inputTagIterable) {
		for (final Tag tag : inputTagIterable) {
			tags.put(tag.getName(), tag);
		}
	}
	
	/**
	 * Gets the directory of the workspace
	 * @return The directory
	 */
	public File getDirectory() {
		return this.directory;
	}
}

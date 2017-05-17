package cz.spiffyk.flpmanager.data;

import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import lombok.Getter;

public class Workspace {
	@Getter private final File directory;
	@Getter private ObservableList<Song> songs = FXCollections.observableArrayList();
	@Getter private ObservableMap<String, Tag> tags = FXCollections.observableHashMap();
	
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
	 * Puts tags into the tags map
	 * @param inputTagIterable The iterable containing the tags to put into the map
	 */
	public void addTags(final Iterable<Tag> inputTagIterable) {
		for (final Tag tag : inputTagIterable) {
			tags.put(tag.getName(), tag);
		}
	}
}

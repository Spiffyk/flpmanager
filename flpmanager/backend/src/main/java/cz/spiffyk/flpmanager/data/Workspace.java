package cz.spiffyk.flpmanager.data;

import java.io.File;
import java.util.Observable;
import java.util.UUID;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import lombok.Getter;

public class Workspace extends Observable {
	@Getter private final File directory;
	@Getter private ObservableList<Song> songs = FXCollections.observableArrayList();
	@Getter private ObservableList<Tag> tags = FXCollections.observableArrayList();
	private ObservableMap<UUID, Tag> tagMap = FXCollections.observableHashMap();
	
	public Workspace(String path) {
		this(new File(path));
	}
	
	public Workspace(File directory) {
		if (directory == null) {
			throw new IllegalArgumentException("The workspace directory cannot be null!");
		}
		
		this.directory = directory;
		this.tags.addListener(new TagsListener());
	}
	
	/**
	 * Puts tags into the tags map
	 * @param inputTagIterable The iterable containing the tags to put into the map
	 */
	public void addTags(final Iterable<Tag> inputTagIterable) {
		for (final Tag tag : inputTagIterable) {
			addTag(tag);
		}
	}
	
	public void addTag(final Tag tag) {
		tags.add(tag);
	}
	
	public Tag getTag(final UUID uuid) {
		return tagMap.get(uuid);
	}
	
	void nudge() {
		setChanged();
		notifyObservers();
	}
	
	private class TagsListener implements ListChangeListener<Tag> {
		@Override
		public void onChanged(javafx.collections.ListChangeListener.Change<? extends Tag> c) {
			while(c.next()) {
				for (Tag tag : c.getAddedSubList()) {
					tagMap.put(tag.getIdentifier(), tag);
				}
				
				for (Tag tag : c.getRemoved()) {
					tagMap.remove(tag.getIdentifier());
					for (Song song : songs) {
						song.getTags().remove(tag);
					}
				}
			}
		}
	}
}

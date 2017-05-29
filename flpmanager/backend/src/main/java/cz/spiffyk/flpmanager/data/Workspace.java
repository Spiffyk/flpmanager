package cz.spiffyk.flpmanager.data;

import java.io.File;
import java.util.Observable;
import java.util.UUID;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cz.spiffyk.flpmanager.ManagerFileException;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import lombok.Getter;
import lombok.NonNull;

public class Workspace extends Observable {
	
	public static final String WORKSPACE_TAGNAME = "workspace";
	
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
	
	
	
	public static Workspace fromElement(@NonNull Element root, @NonNull File directory) {
		if (!root.getTagName().toLowerCase().equals(WORKSPACE_TAGNAME)) {
			throw new ManagerFileException("Not tagged as a workspace; " + root.toString());
		}
		
		Workspace workspace = new Workspace(directory);
		boolean hadTags = false;
		
		final NodeList nodeList = root.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node node = nodeList.item(i);
			if (node instanceof Element) {
				final Element e = (Element) node;
				switch(e.getTagName().toLowerCase()) {
					case Tag.TAGS_TAGNAME:
						workspace.addTags(Tag.listFromElement(e, workspace));
						hadTags = true;
						break;
					case Song.SONGS_TAGNAME:
						if (!hadTags) {
							throw new ManagerFileException("Tags must precede Songs");
						}
						workspace.getSongs().addAll(Song.listFromElement(e, workspace));
						break;
				}
			}
		}
		
		return workspace;
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
	
	public Element toElement(@NonNull Document doc) {
		Element root = doc.createElement(WORKSPACE_TAGNAME);
		
		root.appendChild(Tag.listToElement(this.getTags(), doc));
		root.appendChild(Song.listToElement(this.getSongs(), doc));
		
		return root;
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

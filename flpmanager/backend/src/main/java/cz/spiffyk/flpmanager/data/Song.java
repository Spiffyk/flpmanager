package cz.spiffyk.flpmanager.data;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cz.spiffyk.flpmanager.ManagerFileException;
import cz.spiffyk.flpmanager.ManagerFileHandler;
import cz.spiffyk.flpmanager.util.Messenger;
import cz.spiffyk.flpmanager.util.Messenger.MessageType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.NonNull;

/**
 * Contains song metadata.
 * @author spiffyk
 */
public class Song extends Observable implements WorkspaceNode {
	
	private static final Messenger messenger = Messenger.get();
	
	public static final String SONG_TAGNAME = "song";
	public static final String SONGS_TAGNAME = "songs";
	
	private static final String PROJECTS_DIRECTORY = "_projects";
	private static final String RENDER_DIRECTORY = "_render";

	@Getter private final ObservableList<Project> projects = FXCollections.observableArrayList();
	@Getter private final ObservableList<Tag> tags = FXCollections.observableArrayList();
	
	@Getter private final UUID identifier;
	@Getter private final Workspace parent;
	@Getter private boolean favorite;
	@Getter private String name;
	@Getter private String author;
	
	@Getter private File songDir;
	@Getter private File projectsDir;
	@Getter private File renderDir;
	
	public Song(@NonNull Workspace parent) {
		this(UUID.randomUUID(), parent);
	}
	
	/**
	 * Creates a song with empty name, author and not marked as favorite.
	 */
	public Song(@NonNull UUID identifier, @NonNull Workspace parent) {
		this.identifier = identifier;
		this.parent = parent;
		this.setName("");
		this.setAuthor("");
		this.setFavorite(false);
	}
	
	
	
	public static Song fromElement(@NonNull Element root, @NonNull Workspace parent) {
		if (!root.getTagName().toLowerCase().equals(SONG_TAGNAME)) {
			throw new ManagerFileException("Not tagged as a song; "  + root.toString());
		}
		
		final Song song = new Song(UUID.fromString(root.getAttribute(ManagerFileHandler.UUID_ATTRNAME)), parent);
		song.setName(root.getAttribute(ManagerFileHandler.NAME_ATTRNAME));
		song.setAuthor(root.getAttribute(ManagerFileHandler.AUTHOR_ATTRNAME));
		song.updateFiles();
		String favoriteAttribute = root.getAttribute(ManagerFileHandler.FAVORITE_ATTRNAME);
		if (!favoriteAttribute.isEmpty()) {
			song.setFavorite(Boolean.parseBoolean(favoriteAttribute));
		}
		
		final NodeList nodeList = root.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node node = nodeList.item(i);
			if (node instanceof Element) {
				final Element e = (Element) node;
				switch(e.getTagName().toLowerCase()) {
					case Project.PROJECTS_TAGNAME:
						song.getProjects().addAll(Project.listFromElement(e, song));
						break;
					case Tag.TAGS_TAGNAME:
						song.getTags().addAll(Song.linkedTagListFromElement(e, parent));
						break;
				}
			}
		}
		
		return song;
	}
	
	public static List<Song> listFromElement(@NonNull Element root, @NonNull Workspace parent) {
		if (!root.getTagName().toLowerCase().equals(SONGS_TAGNAME)) {
			throw new ManagerFileException("Not tagged as a list of songs; "  + root.toString());
		}
		
		final List<Song> songs = new ArrayList<>();
		
		final NodeList nodeList = root.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node node = nodeList.item(i);
			if (node instanceof Element) {
				final Element e = (Element) node;
				if (e.getTagName().toLowerCase().equals(SONG_TAGNAME)) {
					songs.add(Song.fromElement(e, parent));
				} else {
					throw new ManagerFileException("The tag <" + SONGS_TAGNAME + "> should only contain a list of <" + SONG_TAGNAME + ">.");
				}
			}
		}
		
		return songs;
	}
	
	public static Element listToElement(@NonNull List<Song> songs, @NonNull Document doc) {
		Element root = doc.createElement(SONGS_TAGNAME);
		for (Song song : songs) {
			root.appendChild(song.toElement(doc));
		}
		return root;
	}
	
	private static Tag linkedTagFromElement(@NonNull Element root, @NonNull Workspace workspace) {
		if (!root.getTagName().toLowerCase().equals(Tag.TAG_TAGNAME)) {
			throw new ManagerFileException("Not tagged as a tag; "  + root.toString());
		}
		
		return workspace.getTag(UUID.fromString(root.getTextContent().toLowerCase()));
	}
	
	private static List<Tag> linkedTagListFromElement(@NonNull Element root, @NonNull Workspace workspace) {
		if (!root.getTagName().toLowerCase().equals(Tag.TAGS_TAGNAME)) {
			throw new ManagerFileException("Not tagged as a list of tags; "  + root.toString());
		}
		
		final List<Tag> tags = new ArrayList<>();
		
		final NodeList nodeList = root.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node node = nodeList.item(i);
			if (node instanceof Element) {
				final Element e = (Element) node;
				if (e.getTagName().toLowerCase().equals(Tag.TAG_TAGNAME)) {
					Tag tag = Song.linkedTagFromElement(e, workspace);
					if (tag != null) {
						tags.add(tag);
					}
				} else {
					throw new ManagerFileException("The tag <" + Tag.TAGS_TAGNAME + "> should only contain a list of <" + Tag.TAG_TAGNAME + ">.");
				}
			}
		}
		
		return tags;
	}
	
	private static Element linkedTagToElement(@NonNull Tag tag, @NonNull Document doc) {
		Element root = doc.createElement(Tag.TAG_TAGNAME);
		root.setTextContent(tag.getIdentifier().toString());
		return root;
	}
	
	private static Element linkedTagListToElement(@NonNull List<Tag> tags, @NonNull Document doc) {
		Element root = doc.createElement(Tag.TAGS_TAGNAME);
		for (Tag tag : tags) {
			root.appendChild(linkedTagToElement(tag, doc));
		}
		return root;
	}
	
	
	
	public void updateFiles() {
		File workspaceDir = parent.getDirectory();
		songDir = new File(workspaceDir, identifier.toString());
		if (!songDir.exists()) {
			songDir.mkdir();
		} else if(!songDir.isDirectory()) {
			throw new IllegalStateException("There is a file with the name matching the UUID but is not a directory");
		}
		
		projectsDir = new File(songDir, PROJECTS_DIRECTORY);
		if (!projectsDir.exists()) {
			projectsDir.mkdir();
		} else if(!projectsDir.isDirectory()) {
			throw new IllegalStateException("There is a file called '" + PROJECTS_DIRECTORY + "' but is not a directory");
		}
		
		renderDir = new File(songDir, RENDER_DIRECTORY);
		if (!renderDir.exists()) {
			renderDir.mkdir();
		} else if(!renderDir.isDirectory()) {
			throw new IllegalStateException("There is a file called '" + RENDER_DIRECTORY + "' but is not a directory");
		}
	}
	
	public void delete() {
		try {
			FileUtils.deleteDirectory(songDir);
		} catch (IOException e) {
			messenger.message(MessageType.ERROR, "Could not open delete song directory.", e.getMessage());
		}
	}
	
	public void openInSystemBrowser() {
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				if (Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().open(songDir);
					} catch (IOException e) {
						messenger.message(MessageType.ERROR, "Could not open song directory.", e.getMessage());
					}
				}
				return null;
			}
		};
		
		new Thread(task).start();
	}
	
	public void setName(@NonNull String name) {
		this.name = name;
		this.setChanged();
		this.notifyObservers();
	}
	
	public void setAuthor(@NonNull String author) {
		this.author = author;
		this.setChanged();
		this.notifyObservers();
	}
	
	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
		this.setChanged();
		this.notifyObservers();
	}
	
	public void toggleFavorite() {
		this.favorite = !this.favorite;
		this.setChanged();
		this.notifyObservers();
	}
	
	public Element toElement(@NonNull Document doc) {
		Element root = doc.createElement(SONG_TAGNAME);
		root.setAttribute(ManagerFileHandler.NAME_ATTRNAME, this.getName());
		root.setAttribute(ManagerFileHandler.AUTHOR_ATTRNAME, this.getAuthor());
		root.setAttribute(ManagerFileHandler.UUID_ATTRNAME, this.getIdentifier().toString());
		
		if (this.isFavorite()) {
			root.setAttribute(ManagerFileHandler.FAVORITE_ATTRNAME, "true");
		}
		
		root.appendChild(Project.listToElement(this.getProjects(), doc));
		root.appendChild(linkedTagListToElement(this.getTags(), doc));
		
		return root;
	}
	
	@Override
	public String toString() {
		return ((this.author.isEmpty()) ? "" : this.author + " - ") + this.name;
	}
	
	@Override
	public WorkspaceNodeType getType() {
		return WorkspaceNodeType.SONG;
	}
	
	void nudge() {
		setChanged();
		notifyObservers();
	}
	
	@Override
	public void notifyObservers() {
		super.notifyObservers();
		if (parent != null) parent.nudge();
	}
	
	public static class NameComparator implements Comparator<TreeItem<WorkspaceNode>> {
		@Override
		public int compare(TreeItem<WorkspaceNode> o1, TreeItem<WorkspaceNode> o2) {
			if (o1.getValue().getType() == WorkspaceNodeType.SONG && o2.getValue().getType() == WorkspaceNodeType.SONG) {
				return ((Song) o1.getValue()).getName().compareTo(((Song) o2.getValue()).getName());
			}
			
			return 0;
		}
	}
	
	public static class FavoriteComparator implements Comparator<TreeItem<WorkspaceNode>> {
		@Override
		public int compare(TreeItem<WorkspaceNode> o1, TreeItem<WorkspaceNode> o2) {
			if (o1.getValue().getType() == WorkspaceNodeType.SONG && o2.getValue().getType() == WorkspaceNodeType.SONG) {
				Song s1 = (Song) o1.getValue();
				Song s2 = (Song) o2.getValue();
				if (s1.isFavorite() == s2.isFavorite()) return 0;
				if (s1.isFavorite() && !s2.isFavorite()) return -1;
				return 1;
			}
			
			return 0;
		}
	}
}

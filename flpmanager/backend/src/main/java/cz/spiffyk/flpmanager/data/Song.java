package cz.spiffyk.flpmanager.data;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.UUID;

import cz.spiffyk.flpmanager.util.Messenger;
import cz.spiffyk.flpmanager.util.Messenger.MessageType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Contains song metadata.
 * @author spiffyk
 */
public class Song extends Observable implements WorkspaceNode {
	
	private static final Messenger messenger = Messenger.get();
	
	private static final String PROJECTS_DIRECTORY = "_projects";

	@Getter private final ObservableList<Project> projects = FXCollections.observableArrayList();
	@Getter private final ObservableList<Tag> tags = FXCollections.observableArrayList();
	
	@Getter private final UUID identifier;
	@Getter @Setter private Workspace parent;
	@Getter private boolean favorite;
	@Getter private String name;
	@Getter private String author;
	
	@Getter private File songDir;
	@Getter private File projectsDir;
	
	public Song() {
		this(UUID.randomUUID());
	}
	
	/**
	 * Creates a song with empty name, author and not marked as favorite.
	 */
	public Song(@NonNull UUID identifier) {
		this.identifier = identifier;
		this.setName("");
		this.setAuthor("");
		this.setFavorite(false);
		checkAndCreateDirectories();
	}
	
	public void checkAndCreateDirectories() {
		if (parent != null) {
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
	
	@Override
	public String toString() {
		return ((this.author.isEmpty()) ? "" : this.author + " - ") + this.name;
	}
	
	@Override
	public WorkspaceNodeType getType() {
		return WorkspaceNodeType.SONG;
	}
}

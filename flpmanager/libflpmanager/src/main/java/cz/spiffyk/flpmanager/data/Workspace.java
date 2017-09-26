package cz.spiffyk.flpmanager.data;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.Observable;
import java.util.UUID;

import cz.spiffyk.flpmanager.WorkspaceInUseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cz.spiffyk.flpmanager.ManagerFileException;
import cz.spiffyk.flpmanager.util.Messenger;
import cz.spiffyk.flpmanager.util.Messenger.MessageType;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.NonNull;

/**
 * Contains all the songs, tags and projects. It is the root data unit of the whole program
 * @author spiffyk
 */
public class Workspace extends Observable {
	
	/**
	 * Messenger
	 */
	private static final Messenger messenger = Messenger.get();
	
	/**
	 * The name of the XML tag representing a workspace
	 */
	public static final String WORKSPACE_TAGNAME = "workspace";

	/**
	 * The filename of workspace lockfile
	 */
	private static final String LOCK_FILE_NAME = ".flpm_lock";
	
	/**
	 * The directory containing the workspace
	 */
	@Getter private final File directory;
	
	/**
	 * The list of {@link Song}s the workspace contains
	 */
	@Getter private ObservableList<Song> songs = FXCollections.observableArrayList();
	
	/**
	 * The list of {@link Tag}s the workspace contains
	 */
	@Getter private ObservableList<Tag> tags = FXCollections.observableArrayList();
	
	/**
	 * The map of {@link Tag}s mapped by their UUID.
	 */
	private ObservableMap<UUID, Tag> tagMap = FXCollections.observableHashMap();

	private FileLock fileLock;
	
	
	
	/**
	 * Creates a new workspace in the specified directory
	 * @param path The directory path represented by a string
	 */
	public Workspace(String path) {
		this(new File(path));
	}
	
	/**
	 * Creates a new workspace in the specified directory
	 * @param directory The directory
	 */
	public Workspace(File directory) {
		if (directory == null) {
			throw new IllegalArgumentException("The workspace directory cannot be null!");
		}
		
		this.directory = directory;
		this.tags.addListener(new TagsListener());
	}
	
	
	/**
	 * Creates a workspace represented by the specified DOM {@link Element} stored in the specified directory.
	 * @param root The DOM {@link Element} representing the workspace
	 * @param directory The directory of the workspace
	 * @return The workspace represented by the {@link Element}
	 */
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
	 * Locks the workspace and checks whether the application exited gracefully the last time.
	 *
	 * @return {@code true} if application ended gracefully the last time it was used, otherwise {@code false}
	 */
	public boolean lock() {
		final File lockFile = new File(directory, LOCK_FILE_NAME);
		boolean existed = lockFile.exists();
		if (!existed) {
			try {
				if (!lockFile.createNewFile()) {
					throw new RuntimeException("Could not create lockfile!");
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		try {
			final RandomAccessFile lockRAFile = new RandomAccessFile(lockFile, "rw");
			final FileChannel fileChannel = lockRAFile.getChannel();

			try {
				final FileLock lock = fileChannel.tryLock();
				if (lock == null) {
					throw new WorkspaceInUseException("Workspace is in use!");
				}
				this.fileLock = lock;
			} catch (OverlappingFileLockException e) {
				throw new WorkspaceInUseException("Workspace is in use!", e);
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Lock file not found (even though its creation was successful???)", e);
		} catch (IOException e) {
			throw new RuntimeException("Could not lock file due to an I/O error!", e);
		}

		return !existed;
	}

	/**
	 * Unlocks the workspace and deletes the lockfile to indicate graceful exit.
	 */
	public void unlock() {
		final File lockFile = new File(directory, LOCK_FILE_NAME);
		try {
			fileLock.release();
		} catch (IOException e) {
			throw new RuntimeException("Could not release file due to an I/O error!", e);
		}
		if (!lockFile.delete()) {
			throw new RuntimeException("Could not delete lockfile!");
		}
	}

	/**
	 * Adds tags into the workspace
	 * @param inputTagIterable The iterable containing the tags to add into the workspace
	 */
	public void addTags(final Iterable<Tag> inputTagIterable) {
		for (final Tag tag : inputTagIterable) {
			addTag(tag);
		}
	}
	
	/**
	 * Adds a tag into the workspace
	 * @param tag The tag to add into the workspace
	 */
	public void addTag(final Tag tag) {
		tags.add(tag);
	}
	
	/**
	 * Gets the {@link Tag} with the specified UUID or {@code null} if it does not exist in the workspace.
	 * @param uuid The UUID of the {@link Tag}
	 * @return The {@link Tag} or {@code null} if it does not exist
	 */
	public Tag getTag(final UUID uuid) {
		return tagMap.get(uuid);
	}
	
	/**
	 * Opens the directory of the workspace in the system browser
	 */
	public void openInSystemBrowser() {
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				if (Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().open(directory);
					} catch (IOException e) {
						messenger.message(MessageType.ERROR, "Could not open workspace directory.", e.getMessage());
					}
				}
				return null;
			}
		};
		
		new Thread(task).start();
	}
	
	/**
	 * Creates a DOM {@link Element} representing the workspace.
	 * @param doc The parent DOM {@link Document}
	 * @return An {@link Element} representing the workspace
	 */
	public Element toElement(@NonNull Document doc) {
		Element root = doc.createElement(WORKSPACE_TAGNAME);
		
		root.appendChild(Tag.listToElement(this.getTags(), doc));
		root.appendChild(Song.listToElement(this.getSongs(), doc));
		
		return root;
	}
	
	/**
	 * Notifies observers
	 */
	void nudge() {
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Listens to changes to the list of {@link Tag}s in the workspace and modifies the map of these {@link Tag}s
	 * accordingly.
	 * @author spiffyk
	 */
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

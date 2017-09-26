package cz.spiffyk.flpmanager.data;

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

import cz.spiffyk.flpmanager.AppConfiguration;
import cz.spiffyk.flpmanager.ManagerFileException;
import cz.spiffyk.flpmanager.ManagerFileHandler;
import cz.spiffyk.flpmanager.util.ManagerUtils;
import cz.spiffyk.flpmanager.util.Messenger;
import cz.spiffyk.flpmanager.util.StreamEater;
import cz.spiffyk.flpmanager.util.Messenger.MessageType;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.NonNull;

/**
 * Instances of this class represent individual project files, or versions, of a {@link Song}.
 * @author spiffyk
 */
public class Project extends Observable implements WorkspaceNode {
	
	/**
	 * App configuration
	 */
	private static final AppConfiguration appConfiguration = AppConfiguration.get();
	
	/**
	 * Messenger
	 */
	private static final Messenger messenger = Messenger.get();
	
	/**
	 * The name of the XML tag representing a project
	 */
	public static final String PROJECT_TAGNAME = "project";
	
	/**
	 * The name of the XML tag representing a list of projects
	 */
	public static final String PROJECTS_TAGNAME = "projects";
	
	/**
	 * The file extension of a project
	 */
	public static final String PROJECT_FILE_EXTENSION = ".flp";

	/**
	 * The suffix added to copies of project files
	 */
	private static final String COPY_FILENAME_SUFFIX = "-copy";
	
	/**
	 * The project template file
	 */
	private static final File FLP_TEMPLATE = new File(appConfiguration.getFlpTemplatePath());



	/**
	 * Unique identifier
	 */
	@Getter private final UUID identifier;
	
	/**
	 * The parent {@link Song}
	 */
	@Getter private final Song parent;
	
	/**
	 * The project's name
	 */
	@Getter private String name;

	/**
	 * The project's file name
	 */
	@Getter private String filename;
	
	/**
	 * The stored project file
	 */
	@Getter private File projectFile;
	
	/**
	 * The path to the project file when it is copied and being worked on
	 */
	private File openedProjectFile;
	
	/**
	 * {@code true} when the project is open and being worked on
	 */
	@Getter private boolean open = false;
	
	
	
	/**
	 * Creates a project with a random UUID, the UUID's string representation as the filename and the specified
	 * {@link Song} as the parent.
	 * @param parent The parent {@link Song}
	 */
	public Project(@NonNull Song parent) {
		this(UUID.randomUUID(), parent);
	}

	/**
	 * Creates a project with a random UUID, the specified filename and the specified {@link Song} as the parent.
	 * @param filename The filename of the project
	 * @param parent The parent {@link Song}
	 */
	public Project(@NonNull String filename, @NonNull Song parent) {
		this(UUID.randomUUID(), filename, parent);
	}

	/**
	 * Creates a new project with the specified UUID, UUID's string representation as the filename and the specified.
	 * {@link Song} as the parent.
	 * @param identifier The UUID to set
	 * @param parent The parent {@link Song}
	 */
	public Project(@NonNull UUID identifier, @NonNull Song parent) {
		this(identifier, identifier.toString() + PROJECT_FILE_EXTENSION, parent);
	}
	
	/**
	 * Creates a new project with the specified UUID, the specified filename and the specified {@link Song}
	 * as the parent.
	 * @param identifier The UUID to set
	 * @param filename The filename of the project
	 * @param parent The parent {@link Song}
	 */
	public Project(@NonNull UUID identifier, @NonNull String filename, @NonNull Song parent) {
		this.parent = parent;
		this.identifier = identifier;
		boolean validFilename = this.setFilename(filename, false);

		if (!validFilename) {
			throw new IllegalArgumentException("Invalid filename");
		}
	}


	
	/**
	 * Creates a project from the specified DOM {@link Element} with the specified {@link Song} as the parent. The tag
	 * must be {@code <project>}.
	 * @param root The element representing the project
	 * @param parent The parent {@link Song}
	 * @return A project represented by the element
	 */
	public static Project fromElement(@NonNull Element root, @NonNull Song parent) {
		if (!root.getTagName().toLowerCase().equals(PROJECT_TAGNAME)) {
			throw new ManagerFileException("Not tagged as a project; " + root.toString());
		}
		
		final Project project = new Project(UUID.fromString(root.getAttribute(ManagerFileHandler.UUID_ATTRNAME)), parent);
		String filename = root.getAttribute(ManagerFileHandler.FILENAME_ATTRNAME);
		if (filename.isEmpty()) {
			project.setFilename(project.getIdentifier().toString() + PROJECT_FILE_EXTENSION, false);
		} else {
			project.setFilename(filename, false);
		}
		project.updateFiles();
		project.setName(root.getAttribute(ManagerFileHandler.NAME_ATTRNAME));
		
		return project;
	}
	
	/**
	 * Creates a {@link List} of projects from the specified DOM {@link Element} with the specified {@link Song} as the
	 * parent of all the projects. The tag must be {@code <projects>}.
	 * @param root The element representing the list of projects
	 * @param parent The parent {@link Song}
	 * @return A {@link List} of project represented by the element
	 */
	public static List<Project> listFromElement(@NonNull Element root, @NonNull Song parent) {
		if (!root.getTagName().toLowerCase().equals(PROJECTS_TAGNAME)) {
			throw new ManagerFileException("Not tagged as a list of projects; " + root.toString());
		}
		
		final List<Project> projects = new ArrayList<>();
		
		final NodeList nodeList = root.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node node = nodeList.item(i);
			if (node instanceof Element) {
				final Element e = (Element) node;
				if (e.getTagName().toLowerCase().equals(PROJECT_TAGNAME)) {
						projects.add(Project.fromElement(e, parent));
				} else {
					throw new ManagerFileException("The tag <" + PROJECTS_TAGNAME + "> should only contain a list of <" + PROJECT_TAGNAME + ">.");
				}
			}
		}
		
		return projects;
	}
	
	/**
	 * Creates a DOM {@link Element} representing the specified {@link List} of projects.
	 * @param projects The {@link List} of projects to represent by the {@link Element}
	 * @param doc The parent DOM {@link Document} of the {@link Element}
	 * @return A DOM {@link Element} representing the {@link List} of projects
	 */
	public static Element listToElement(@NonNull List<Project> projects, @NonNull Document doc) {
		Element root = doc.createElement(PROJECTS_TAGNAME);
		for (Project project : projects) {
			root.appendChild(project.toElement(doc));
		}
		return root;
	}
	
	
	
	@Override
	public WorkspaceNodeType getType() {
		return WorkspaceNodeType.PROJECT;
	}
	
	/**
	 * Sets the name of the project
	 * @param name
	 */
	public void setName(@NonNull String name) {
		this.name = name;
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * Sets a new filename for the project and moves the original file.
	 * @param filename The new filename
	 * @return {@code true} if the filename was valid and no other file with that name existed, otherwise {@code false}.
	 */
	public boolean setFilename(@NonNull String filename) {
		return setFilename(filename, true);
	}

	/**
	 * Sets a new filename for the project.
	 * @param filename The new filename
	 * @param move Whether the current file should be renamed
	 * @return {@code true} if the filename was valid and no other file with that name existed, otherwise {@code false}.
	 */
	public boolean setFilename(@NonNull String filename, boolean move) {
		if (filename.matches(ManagerUtils.FILE_REGEX)) {
			this.filename = filename;
			File newFile = new File(this.parent.getProjectsDir(), filename);

			if (move && this.projectFile.isFile() && !this.projectFile.equals(newFile)) {
				if (newFile.exists()) {
					throw new IllegalStateException("File already exists");
				}

				this.projectFile.renameTo(newFile);
			}

			projectFile = new File(parent.getProjectsDir(), filename);
			openedProjectFile = new File(parent.getSongDir(), filename);

			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	/**
	 * Creates a new copy of this project with a new random UUID and {@code " (copy)"} added to its name.
	 * @return The copy of the project
	 */
	public Project copy() {
		return copy(false);
	}

	/**
	 * Creates a new copy of this project with a new random UUID, {@code "-copy"} added to the filename (taking the file
	 * extension into account) and {@code " (copy)"} added to its name. If {@code addToParent} is {@code true},
	 * the project is automatically added to its parent.
	 *
	 * @param addToParent Whether the project should be automatically added to its parent
	 * @return The copy of the project
	 */
	public Project copy(boolean addToParent) {
		String newName;
		if (filename.endsWith(PROJECT_FILE_EXTENSION)) {
			newName =
					filename.substring(0, filename.length() - PROJECT_FILE_EXTENSION.length()) +
							COPY_FILENAME_SUFFIX + PROJECT_FILE_EXTENSION;
		} else {
			newName = filename + COPY_FILENAME_SUFFIX;
		}

		return this.copy(newName, addToParent);
	}
	
	/**
	 * Creates a new copy of this project with a new random UUID, the specified filename and {@code " (copy)"} added
	 * to its name. If {@code addToParent} is {@code true}, the project is automatically added to its parent.
	 *
	 * @param newFilename The filename of the new project
	 * @param addToParent Whether the project should be automatically added to its parent
	 * @return The copy of the project
	 */
	public Project copy(String newFilename, boolean addToParent) {
		final Project copy = new Project(newFilename, this.parent);
		copy.setName(this.getName() + " (copy)");
		
		if (this.projectFile.exists()) {
			try {
				FileUtils.copyFile(this.projectFile, copy.projectFile);
			} catch (IOException e) {
				messenger.message(MessageType.ERROR, "File could not be copied.", e.getMessage());
			}
		}
		
		if (addToParent) {
			List<Project> projects = parent.getProjects();
			parent.getProjects().add(projects.indexOf(this), copy);
		}
		
		return copy;
	}
	
	/**
	 * Deletes the project file
	 */
	public void delete() {
		projectFile.delete();
	}
	
	/**
	 * Updates the project's files paths and if no stored project file exists, copies the template to its place.
	 */
	public synchronized void updateFiles() {
		projectFile = new File(parent.getProjectsDir(), filename);
		openedProjectFile = new File(parent.getSongDir(), filename);
		
		if (!projectFile.exists()) {
			try {
				FileUtils.copyFile(FLP_TEMPLATE, projectFile);
			} catch (IOException e) {
				messenger.message(MessageType.ERROR, "Could not create project file.", e.getMessage());
			}
		}
	}
	
	/**
	 * Opens the project in FL Studio
	 */
	public synchronized void openProject() {
		if (!open) {
			updateFiles();
			
			if (projectFile.exists()) {
				try {
					FileUtils.copyFile(projectFile, openedProjectFile);
				} catch (IOException e) {
					messenger.message(MessageType.ERROR, "File could not be copied.", e.getMessage());
					return;
				}
			} else {
				try {
					FileUtils.copyFile(FLP_TEMPLATE, openedProjectFile);
				} catch (IOException e) {
					messenger.message(MessageType.ERROR, "File could not be copied.", e.getMessage());
					return;
				}
			}
			
			messenger.message(MessageType.PROJECT_OPEN, this);
			open = true;
			
			Task<Void> task = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					try {
						ProcessBuilder builder = new ProcessBuilder(AppConfiguration.get().getFlExecutablePath(), openedProjectFile.getAbsolutePath());
						Process process = builder.start();
						
						// A workaround for Wine hanging when the output has nowhere to go...
						// This one is very strange...
						StreamEater errorGobbler = new StreamEater(process.getErrorStream());
						errorGobbler.start();
						StreamEater inputGobbler = new StreamEater(process.getInputStream());
						inputGobbler.start();
						
						process.waitFor();
					} catch (IOException e) {
						messenger.message(MessageType.ERROR, "Unable to start FL Studio");
					} catch (InterruptedException e) {
						e.printStackTrace();
						messenger.message(MessageType.ERROR, "Interrupted error.");
					}
					return null;
				}
			};
			
			task.setOnSucceeded((e) -> {
				closeProject();
			});
			
			task.setOnFailed((e) -> {
				messenger.message(MessageType.ERROR, "FL running task failed.");
				closeProject();
			});
			
			task.setOnCancelled((e) -> {
				messenger.message(MessageType.ERROR, "FL running task cancelled.");
				closeProject();
			});
			
			new Thread(task).start();
		}
	}
	
	/**
	 * Copies the working copy back to the storage and removes it.
	 */
	public synchronized void closeProject() {
		if (open) {
			updateFiles();
			
			try {
				FileUtils.copyFile(openedProjectFile, projectFile);
				openedProjectFile.delete();
			} catch (IOException e) {
				messenger.message(MessageType.ERROR, "Could not copy the file back.", e.getMessage());
			}
			
			messenger.message(MessageType.PROJECT_CLOSE);
			open = false;
		}
	}
	
	/**
	 * Calls FL Studio to render the project in the specified format
	 * @param format The {@link RenderFormat} to use
	 */
	public synchronized void renderProject(RenderFormat format) {
		new Thread(new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					try {
						ProcessBuilder builder = new ProcessBuilder(AppConfiguration.get().getFlExecutablePath(),
								"/R",
								"/E" + format.getFormatId(),
								projectFile.getAbsolutePath());
						Process process = builder.start();
						
						// A workaround for Wine hanging when the output has nowhere to go...
						// This one is very strange...
						StreamEater errorGobbler = new StreamEater(process.getErrorStream());
						errorGobbler.start();
						StreamEater inputGobbler = new StreamEater(process.getInputStream());
						inputGobbler.start();
						
						process.waitFor();
						
						String outName;
						if (name.matches(ManagerUtils.FILE_REGEX)) {
							if (parent.getName().matches(ManagerUtils.FILE_REGEX)) {
								outName = parent.getName() + " (" + parent.getName() + ")";
							} else {
								outName = name;
							}
						} else {
							outName = identifier.toString();
						}
						
						File outFile = new File(parent.getRenderDir(), outName + "." + format.getFormatId());
						File inFile = new File(parent.getProjectsDir(), identifier.toString() + "." + format.getFormatId());
						
						if (outFile.exists()) {
							outFile.delete(); // we will be replacing it
						}
						
						inFile.renameTo(outFile);
					} catch (IOException e) {
						messenger.message(MessageType.ERROR, "Unable to start FL Studio");
					} catch (InterruptedException e) {
						e.printStackTrace();
						messenger.message(MessageType.ERROR, "Interrupted error.");
					}
					return null;
				}
			}).start();
	}
	
	/**
	 * Creates a DOM {@link Element} representing the project.
	 * @param doc The parent DOM {@link Document}
	 * @return An {@link Element} representing the project
	 */
	public Element toElement(@NonNull Document doc) {
		Element root = doc.createElement(PROJECT_TAGNAME);
		root.setAttribute(ManagerFileHandler.NAME_ATTRNAME, this.getName());
		root.setAttribute(ManagerFileHandler.UUID_ATTRNAME, this.getIdentifier().toString());
		root.setAttribute(ManagerFileHandler.FILENAME_ATTRNAME, this.getFilename());
		return root;
	}
	
	@Override
	public void notifyObservers() {
		super.notifyObservers();
		if (parent != null) parent.nudge();
	}
	
	/**
	 * Compares project {@link WorkspaceNode}s by name
	 * @author spiffyk
	 */
	public static class NameComparator implements Comparator<TreeItem<WorkspaceNode>> {
		@Override
		public int compare(TreeItem<WorkspaceNode> o1, TreeItem<WorkspaceNode> o2) {
			if (o1.getValue().getType() == WorkspaceNodeType.PROJECT && o2.getValue().getType() == WorkspaceNodeType.PROJECT) {
				return ((Project) o1.getValue()).getName().compareTo(((Project) o2.getValue()).getName());
			}
			
			return 0;
		}
	}
	
	/**
	 * An enum representing formats in which FL Studio is able to render
	 * @author spiffyk
	 */
	public enum RenderFormat {
		/**
		 * MPEG Layer 3
		 */
		MP3("mp3"),
		
		/**
		 * Wave
		 */
		WAV("wav"),
		
		/**
		 * Free Lossless Audio Codec
		 */
		FLAC("flac"),
		
		/**
		 * OGG Vorbis
		 */
		VORBIS("ogg");
		
		/**
		 * The extension as well as the identifier FL Studio uses for the format
		 */
		@Getter private final String formatId;
		
		/**
		 * Enum constructor
		 * @param formatId The identifier of the format
		 */
		private RenderFormat(String formatId) {
			this.formatId = formatId;
		}
		
	}
}

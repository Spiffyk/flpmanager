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
import cz.spiffyk.flpmanager.util.Messenger;
import cz.spiffyk.flpmanager.util.StreamEater;
import cz.spiffyk.flpmanager.util.Messenger.MessageType;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.NonNull;

public class Project extends Observable implements WorkspaceNode {
	
	private static final AppConfiguration appConfiguration = AppConfiguration.get();
	private static final Messenger messenger = Messenger.get();
	
	public static final String PROJECT_TAGNAME = "project";
	public static final String PROJECTS_TAGNAME = "projects";
	
	private static final String PROJECT_FILE_EXTENSION = ".flp";
	private static final String FILE_REGEX = "[a-zA-Z0-9- ]+";
	
	private static final File EMPTY_FLP = new File(appConfiguration.getFlpTemplatePath());
	
	@Getter private final UUID identifier;
	@Getter private final Song parent;
	
	@Getter @NonNull private String name;
	@Getter private File projectFile;
	private File openedProjectFile;
	
	@Getter private boolean open = false;
	
	public Project(@NonNull Song parent) {
		this(UUID.randomUUID(), parent);
	}
	
	public Project(@NonNull UUID identifier, @NonNull Song parent) {
		this.parent = parent;
		this.identifier = identifier;
	}
	
	
	
	public static Project fromElement(@NonNull Element root, @NonNull Song parent) {
		if (!root.getTagName().toLowerCase().equals(PROJECT_TAGNAME)) {
			throw new ManagerFileException("Not tagged as a project; " + root.toString());
		}
		
		final Project project = new Project(UUID.fromString(root.getAttribute(ManagerFileHandler.UUID_ATTRNAME)), parent);
		project.setName(root.getAttribute(ManagerFileHandler.NAME_ATTRNAME));
		
		return project;
	}
	
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
	
	public void setName(@NonNull String name) {
		this.name = name;
		this.setChanged();
		this.notifyObservers();
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public Project copy() {
		return copy(false);
	}
	
	public Project copy(boolean addToParent) {
		final Project copy = new Project(this.parent);
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
	
	public void delete() {
		projectFile.delete();
	}
	
	public synchronized void updateFiles() {
		projectFile = new File(parent.getProjectsDir(), identifier.toString() + PROJECT_FILE_EXTENSION);
		openedProjectFile = new File(parent.getSongDir(), identifier.toString() + PROJECT_FILE_EXTENSION);
		
		if (!projectFile.exists()) {
			try {
				FileUtils.copyFile(EMPTY_FLP, projectFile);
			} catch (IOException e) {
				messenger.message(MessageType.ERROR, "Could not create project file.", e.getMessage());
			}
		}
	}
	
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
					FileUtils.copyFile(EMPTY_FLP, openedProjectFile);
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
						if (name.matches(FILE_REGEX)) {
							if (parent.getName().matches(FILE_REGEX)) {
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
	
	public Element toElement(@NonNull Document doc) {
		Element root = doc.createElement(PROJECT_TAGNAME);
		root.setAttribute(ManagerFileHandler.NAME_ATTRNAME, this.getName());
		root.setAttribute(ManagerFileHandler.UUID_ATTRNAME, this.getIdentifier().toString());
		return root;
	}
	
	@Override
	public void notifyObservers() {
		super.notifyObservers();
		if (parent != null) parent.nudge();
	}
	
	public static class NameComparator implements Comparator<TreeItem<WorkspaceNode>> {
		@Override
		public int compare(TreeItem<WorkspaceNode> o1, TreeItem<WorkspaceNode> o2) {
			if (o1.getValue().getType() == WorkspaceNodeType.PROJECT && o2.getValue().getType() == WorkspaceNodeType.PROJECT) {
				return ((Project) o1.getValue()).getName().compareTo(((Project) o2.getValue()).getName());
			}
			
			return 0;
		}
	}
	
	public static enum RenderFormat {
		MP3("mp3"), WAV("wav"), FLAC("flac"), VORBIS("ogg");
		
		@Getter private final String formatId;
		
		private RenderFormat(String formatId) {
			this.formatId = formatId;
		}
		
	}
}

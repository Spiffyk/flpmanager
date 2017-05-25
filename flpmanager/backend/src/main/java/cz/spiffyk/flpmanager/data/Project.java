package cz.spiffyk.flpmanager.data;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import cz.spiffyk.flpmanager.AppConfiguration;
import cz.spiffyk.flpmanager.util.Messenger;
import cz.spiffyk.flpmanager.util.Messenger.MessageType;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.NonNull;

public class Project extends Observable implements WorkspaceNode {
	
	private static final AppConfiguration appConfiguration = AppConfiguration.get();
	private static final Messenger messenger = Messenger.get();
	private static final String PROJECT_FILE_EXTENSION = ".flp";
	private static File EMPTY_FLP = new File(appConfiguration.getFlpTemplatePath());
	
	@Getter private final UUID identifier;
	
	@Getter @NonNull private Song parent;
	@Getter @NonNull private String name;
	@Getter private File projectFile;
	private File openedProjectFile;
	
	@Getter private boolean open = false;
	
	public Project() {
		this(UUID.randomUUID());
	}
	
	public Project(@NonNull UUID identifier) {
		this.identifier = identifier;
	}
	
	@Override
	public WorkspaceNodeType getType() {
		return WorkspaceNodeType.PROJECT;
	}
	
	public void setParent(@NonNull Song parent) {
		this.parent = parent;
		updateFiles();
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
		final Project copy = new Project();
		copy.setName(this.getName() + " (copy)");
		copy.setParent(this.getParent());
		
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
	
	public synchronized void updateFiles() {
		if (parent != null) {
			projectFile = new File(parent.getProjectsDir(), identifier.toString() + PROJECT_FILE_EXTENSION);
			openedProjectFile = new File(parent.getSongDir(), identifier.toString() + PROJECT_FILE_EXTENSION);
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
			
			messenger.message(MessageType.HIDE_STAGE);
			open = true;
			
			Task<Void> task = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					try {
						Process process = new ProcessBuilder(AppConfiguration.get().getFlExecutablePath(), openedProjectFile.getAbsolutePath()).start();
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
			
			messenger.message(MessageType.SHOW_STAGE);
			open = false;
		}
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
}

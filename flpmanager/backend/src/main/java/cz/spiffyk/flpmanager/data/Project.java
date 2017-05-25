package cz.spiffyk.flpmanager.data;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Observable;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import cz.spiffyk.flpmanager.AppConfiguration;
import cz.spiffyk.flpmanager.util.Messenger;
import cz.spiffyk.flpmanager.util.Messenger.MessageType;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class Project extends Observable implements WorkspaceNode {
	
	private static final Messenger messenger = Messenger.get();
	private static final String PROJECT_FILE_EXTENSION = ".flp";
	private static File EMPTY_FLP;
	
	{
		try {
			EMPTY_FLP = new File(Project.class.getClassLoader().getResource("emptyflp.flp").toURI());
		} catch (URISyntaxException e) {
			EMPTY_FLP = null;
		}
	}
	
	@Getter private final UUID identifier;
	
	@Getter @Setter private Song parent;
	@Getter @Setter @NonNull private String name;
	
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
	
	@Override
	public String toString() {
		return getName();
	}
	
	public synchronized void openProject() {
		if (!open) {
			File savedProjectFile = new File(parent.getProjectsDir(), identifier.toString() + PROJECT_FILE_EXTENSION);
			File openedProjectFile = new File(parent.getSongDir(), identifier.toString() + PROJECT_FILE_EXTENSION);
			
			if (savedProjectFile.exists()) {
				try {
					FileUtils.copyFile(savedProjectFile, openedProjectFile);
				} catch (IOException e) {
					throw new RuntimeException("File could not be copied.", e);
				}
			} else {
				try {
					FileUtils.copyFile(EMPTY_FLP, openedProjectFile);
				} catch (IOException e) {
					throw new RuntimeException("File could not be copied.", e);
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
			File savedProjectFile = new File(parent.getProjectsDir(), identifier.toString() + PROJECT_FILE_EXTENSION);
			File openedProjectFile = new File(parent.getSongDir(), identifier.toString() + PROJECT_FILE_EXTENSION);
			
			try {
				FileUtils.copyFile(openedProjectFile, savedProjectFile);
				openedProjectFile.delete();
			} catch (IOException e) {
				messenger.message(MessageType.ERROR, "Could not copy the file back.");
			}
			
			messenger.message(MessageType.SHOW_STAGE);
			open = false;
		}
	}
}

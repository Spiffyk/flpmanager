package cz.spiffyk.flpmanager.application.controls;

import java.util.Observable;
import java.util.Observer;

import cz.spiffyk.flpmanager.application.ProjectsListener;
import cz.spiffyk.flpmanager.application.WorkspaceNodeListener;
import cz.spiffyk.flpmanager.data.Project;
import cz.spiffyk.flpmanager.data.Song;
import cz.spiffyk.flpmanager.data.WorkspaceNode;
import javafx.scene.control.TreeItem;

public class SongTreeItem extends TreeItem<WorkspaceNode> {
	
	private WorkspaceNodeListener listener;
	
	public SongTreeItem(final Song song) {
		super(song);
		this.listener = new ProjectsListener(this);
		for (final Project p : song.getProjects()) {
			this.getChildren().add(new TreeItem<WorkspaceNode>(p));
		}
		song.getProjects().addListener(this.listener);
		song.addObserver(listener);
		listener.sort();
	}

}

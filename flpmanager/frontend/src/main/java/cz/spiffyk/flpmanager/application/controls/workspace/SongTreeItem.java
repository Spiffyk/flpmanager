package cz.spiffyk.flpmanager.application.controls.workspace;

import cz.spiffyk.flpmanager.application.ProjectsListener;
import cz.spiffyk.flpmanager.application.WorkspaceNodeListener;
import cz.spiffyk.flpmanager.data.Project;
import cz.spiffyk.flpmanager.data.Song;
import cz.spiffyk.flpmanager.data.WorkspaceNode;
import javafx.scene.control.TreeItem;

/**
 * A {@link TreeItem} wrapper for {@link Song}s
 * @author spiffyk
 */
public class SongTreeItem extends TreeItem<WorkspaceNode> {
	
	/**
	 * A listener for {@link Song}'s children
	 */
	private WorkspaceNodeListener listener;
	
	/**
	 * Creates a new wrapper tree item
	 * @param song The song to be wrapped by this item
	 */
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

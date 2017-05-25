package cz.spiffyk.flpmanager.application.controls;

import cz.spiffyk.flpmanager.application.WorkspaceNodeListener;
import cz.spiffyk.flpmanager.data.Project;
import cz.spiffyk.flpmanager.data.Song;
import cz.spiffyk.flpmanager.data.Workspace;
import cz.spiffyk.flpmanager.data.WorkspaceNode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public final class SongsView extends TreeView<WorkspaceNode> {
	private Workspace workspace;
	private final WorkspaceNodeListener listener;
	
	public SongsView() {
		this.setCellFactory((view) -> new WorkspaceNodeTreeCell());
		TreeItem<WorkspaceNode> root = new TreeItem<>();
		this.listener = new WorkspaceNodeListener(root);
		this.setRoot(root);
		this.setShowRoot(false);
	}
	
	public void setWorkspace(final Workspace workspace) {
		if (workspace == null)
			throw new IllegalArgumentException("Assigned workspace cannot be null");
		
		if (this.workspace == null) {
			this.workspace = workspace;
			for (Song song : workspace.getSongs()) {
				getRoot().getChildren().add(new SongTreeItem(song));
			}
			workspace.getSongs().addListener(listener);
		} else {
			throw new UnsupportedOperationException("The workspace can be assigned only once");
		}
	}
}

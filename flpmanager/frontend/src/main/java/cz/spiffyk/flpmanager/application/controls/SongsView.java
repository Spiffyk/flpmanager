package cz.spiffyk.flpmanager.application.controls;

import cz.spiffyk.flpmanager.application.SongsListener;
import cz.spiffyk.flpmanager.application.WorkspaceNodeListener;
import cz.spiffyk.flpmanager.data.Song;
import cz.spiffyk.flpmanager.data.Workspace;
import cz.spiffyk.flpmanager.data.WorkspaceNode;
import cz.spiffyk.flpmanager.data.WorkspaceNodeType;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;

public final class SongsView extends TreeView<WorkspaceNode> {
	private Workspace workspace;
	private final WorkspaceNodeListener listener;
	
	public SongsView() {
		this.setCellFactory((view) -> new WorkspaceNodeTreeCell());
		TreeItem<WorkspaceNode> root = new TreeItem<>();
		this.listener = new SongsListener(root);
		this.setRoot(root);
		this.setShowRoot(false);
		
		this.setOnKeyReleased((event) -> {
			TreeItem<WorkspaceNode> treeItem = getSelectionModel().getSelectedItem();
			
			if (treeItem != null && treeItem.getValue() != null) {
				WorkspaceNode node = treeItem.getValue();
				
				if (node.getType() == WorkspaceNodeType.SONG) {
					final Song song = (Song) node;
					
					if (event.getCode() == KeyCode.F) {
						song.toggleFavorite();
					}
				}
			}
		});
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
			listener.sort();
		} else {
			throw new UnsupportedOperationException("The workspace can be assigned only once");
		}
	}
}

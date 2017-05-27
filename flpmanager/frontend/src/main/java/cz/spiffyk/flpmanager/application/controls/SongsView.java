package cz.spiffyk.flpmanager.application.controls;

import cz.spiffyk.flpmanager.application.SongsListener;
import cz.spiffyk.flpmanager.application.WorkspaceNodeListener;
import cz.spiffyk.flpmanager.application.screens.songeditor.SongEditorDialog;
import cz.spiffyk.flpmanager.data.Song;
import cz.spiffyk.flpmanager.data.Workspace;
import cz.spiffyk.flpmanager.data.WorkspaceNode;
import cz.spiffyk.flpmanager.data.WorkspaceNodeType;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.NonNull;

public final class SongsView extends StackPane {
	
	private Workspace workspace;
	private final WorkspaceNodeListener listener;
	
	private final TreeView<WorkspaceNode> innerTreeView;
	private final VBox placeholder;
	
	public SongsView() {
		innerTreeView = new TreeView<WorkspaceNode>();
		innerTreeView.setCellFactory((view) -> new WorkspaceNodeTreeCell());
		TreeItem<WorkspaceNode> root = new TreeItem<>();
		this.listener = new SongsListener(root, this);
		innerTreeView.setRoot(root);
		innerTreeView.setShowRoot(false);
		
		innerTreeView.setOnKeyReleased((event) -> {
			TreeItem<WorkspaceNode> treeItem = innerTreeView.getSelectionModel().getSelectedItem();
			
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
		
		placeholder = new VBox();
		placeholder.setAlignment(Pos.CENTER);
		
		Button newSongButton = new Button("Create a new song");
		newSongButton.setOnAction((e) -> SongEditorDialog.createNewSong(this.workspace));
		
		Label placeholderText = new Label("There are no songs in this workspace.");
		
		placeholder.getChildren().addAll(placeholderText, newSongButton);
		
		this.getChildren().addAll(innerTreeView, placeholder);
	}
	
	public void setWorkspace(@NonNull final Workspace workspace) {
		
		if (this.workspace == null) {
			this.workspace = workspace;
			setShowPlaceholder(workspace.getSongs().isEmpty());
			for (Song song : workspace.getSongs()) {
				innerTreeView.getRoot().getChildren().add(new SongTreeItem(song));
			}
			workspace.getSongs().addListener(listener);
			workspace.addObserver(listener);
			listener.sort();
		} else {
			throw new UnsupportedOperationException("The workspace can be assigned only once");
		}
	}
	
	public void setShowPlaceholder(final boolean show) {
		placeholder.setVisible(show);
		innerTreeView.setVisible(!show);
	}
	
}

package cz.spiffyk.flpmanager.application.views.songs;

import java.io.IOException;

import cz.spiffyk.flpmanager.application.SongsListener;
import cz.spiffyk.flpmanager.application.WorkspaceNodeListener;
import cz.spiffyk.flpmanager.application.controls.workspace.SongTreeItem;
import cz.spiffyk.flpmanager.application.screens.SongEditorDialog;
import cz.spiffyk.flpmanager.application.screens.TagEditorDialog;
import cz.spiffyk.flpmanager.data.Song;
import cz.spiffyk.flpmanager.data.Workspace;
import cz.spiffyk.flpmanager.data.WorkspaceNode;
import cz.spiffyk.flpmanager.data.WorkspaceNodeType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import lombok.NonNull;

public final class SongsView extends VBox {
	
	private Workspace workspace;
	private WorkspaceNodeListener listener;
	
	@FXML private TreeView<WorkspaceNode> innerTreeView;
	@FXML private VBox placeholder;
	
	public SongsView() {
		super();
		
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("SongsView.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			Platform.exit();
		}
	}
	
	@FXML private void initialize() {
		TreeItem<WorkspaceNode> root = new TreeItem<>();
		this.listener = new SongsListener(root, this);
		
		innerTreeView.setRoot(root);
	}
	
	@FXML private void newSongAction() {
		Dialog<Boolean> dialog = SongEditorDialog.newSongDialog(this.workspace);
		dialog.initOwner(this.getScene().getWindow());
		dialog.showAndWait();
	}
	
	@FXML private void newTagAction() {
		Dialog<Boolean> dialog = TagEditorDialog.newTagDialog(this.workspace);
		dialog.initOwner(this.getScene().getWindow());
		dialog.showAndWait();
	}
	
	@FXML private void onTreeViewKey(KeyEvent event) {
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

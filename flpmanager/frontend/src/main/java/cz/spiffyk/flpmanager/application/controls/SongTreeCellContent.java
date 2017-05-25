package cz.spiffyk.flpmanager.application.controls;

import java.util.Observable;
import java.util.Observer;

import cz.spiffyk.flpmanager.application.screens.generator.SongEditorDialog;
import cz.spiffyk.flpmanager.data.Song;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class SongTreeCellContent extends WorkspaceNodeTreeCellContent<Song> implements Observer {
	
	private final Song song;
	private final CheckBox favoriteCheckBox;
	private final ContextMenu contextMenu;
	
	public SongTreeCellContent(Song node) {
		super(node);
		this.contextMenu = new SongContextMenu();
		this.setOnContextMenuRequested((event) -> {
			this.contextMenu.show(this, event.getScreenX(), event.getScreenY());
		});
		getStyleClass().add("song-cell");
		this.song = node;
		node.addObserver(this);
		this.favoriteCheckBox = new CheckBox();
		this.favoriteCheckBox.getStyleClass().add("favorite-check-box");
		this.favoriteCheckBox.setOnAction((event) -> {
			song.setFavorite(this.favoriteCheckBox.isSelected());
		});
		getLeftBox().getChildren().add(this.favoriteCheckBox);
		
		update();
	}

	@Override
	public void update(Observable o, Object arg) {
		update();
	}
	
	private void update() {
		this.favoriteCheckBox.setSelected(song.isFavorite());
		getLabel().setText(song.getAuthor() + " - " + song.getName());
	}
	
	private class SongContextMenu extends ContextMenu {
		{
			MenuItem editItem = new MenuItem("Edit song...");
			editItem.setOnAction((event) -> {
				new SongEditorDialog(song).showAndWait();
				update();
			});
			
			MenuItem openDirItem = new MenuItem("Open in system explorer...");
			openDirItem.setOnAction((event) -> {
				song.openInSystemBrowser();
			});
			
			this.getItems().addAll(editItem, openDirItem);
		}
	}
}

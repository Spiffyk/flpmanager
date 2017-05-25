package cz.spiffyk.flpmanager.application.controls;

import java.util.Observable;
import java.util.Observer;

import cz.spiffyk.flpmanager.application.screens.projecteditor.ProjectEditorDialog;
import cz.spiffyk.flpmanager.application.screens.songeditor.SongEditorDialog;
import cz.spiffyk.flpmanager.data.Project;
import cz.spiffyk.flpmanager.data.Song;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Alert.AlertType;

public class SongTreeCellContent extends WorkspaceNodeTreeCellContent<Song> implements Observer {
	
	private final Song song;
	private final CheckBox favoriteCheckBox;
	private final ContextMenu contextMenu;
	
	public SongTreeCellContent(Song node) {
		super(node);
		this.contextMenu = new SongContextMenu();
		this.setOnContextMenuRequested((event) -> {
			this.contextMenu.show(this.getScene().getWindow(), event.getScreenX(), event.getScreenY());
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
		public SongContextMenu() {
			MenuItem editItem = new MenuItem("_Edit song info...");
			editItem.setOnAction((event) -> {
				new SongEditorDialog(song).showAndWait();
				update();
			});
			
			MenuItem openDirItem = new MenuItem("_Open in system explorer...");
			openDirItem.setOnAction((event) -> {
				song.openInSystemBrowser();
			});
			
			MenuItem newProjectItem = new MenuItem("Create a new project...");
			newProjectItem.setOnAction((event) -> {
				final Project project = new Project();
				final ProjectEditorDialog dialog = new ProjectEditorDialog(project);
				dialog.showAndWait().ifPresent((b) -> {
					if (b.booleanValue()) {
						project.setParent(song);
						song.getProjects().add(project);
					}
				});
			});
			
			MenuItem deleteItem = new MenuItem("Delete");
			deleteItem.setOnAction((event) -> {
				final Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setHeaderText(null);
				alert.setContentText("Do you really wish to delete this song? (no undo)");
				ButtonType bt = alert.showAndWait().orElse(ButtonType.CANCEL);
				if (bt == ButtonType.OK) {
					song.getParent().getSongs().remove(song);
				}
			});
			
			this.getItems().addAll(
					editItem,
					openDirItem,
					new SeparatorMenuItem(),
					newProjectItem,
					new SeparatorMenuItem(),
					deleteItem);
		}
	}
}

package cz.spiffyk.flpmanager.application.controls.workspace;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.io.FileUtils;

import cz.spiffyk.flpmanager.Text;
import cz.spiffyk.flpmanager.application.controls.tags.TagsViewer;
import cz.spiffyk.flpmanager.application.screens.ProjectEditorDialog;
import cz.spiffyk.flpmanager.application.screens.SongEditorDialog;
import cz.spiffyk.flpmanager.data.Project;
import cz.spiffyk.flpmanager.data.Song;
import cz.spiffyk.flpmanager.util.Messenger;
import cz.spiffyk.flpmanager.util.Messenger.MessageType;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.control.Alert.AlertType;

/**
 * The content box of a tree cell representing a {@link Song}
 * @author spiffyk
 */
public class SongTreeCellContent extends WorkspaceNodeTreeCellContent<Song> implements Observer {
	
	private static final Text text = Text.get();
	
	/**
	 * The {@link Song} to represent
	 */
	private final Song song;
	
	/**
	 * A checkbox indicating whether the {@link Song} is marked as favorite
	 */
	private final CheckBox favoriteCheckBox;
	
	/**
	 * The context menu
	 */
	private final ContextMenu contextMenu;
	
	/**
	 * The {@link Song}'s {@link TagsViewer}
	 */
	private final TagsViewer tags;
	
	
	
	/**
	 * Creates a new tree cell content box representing the specified {@link Song}.
	 * @param node The {@link Song} to represent
	 */
	public SongTreeCellContent(Song node) {
		super(node);
		this.contextMenu = new SongContextMenu();
		this.setOnContextMenuRequested((event) -> {
			this.contextMenu.show(this.getScene().getWindow(), event.getScreenX(), event.getScreenY());
			event.consume();
		});
		getStyleClass().add("song-cell");
		this.song = node;
		node.addObserver(this);
		
		this.favoriteCheckBox = new CheckBox();
		this.favoriteCheckBox.getStyleClass().add("favorite-check-box");
		this.favoriteCheckBox.setOnAction((event) -> {
			song.setFavorite(this.favoriteCheckBox.isSelected());
		});
		getLeftBox().getChildren().addAll(this.favoriteCheckBox);
		
		this.tags = new TagsViewer();
		tags.setTags(this.song.getTags());
		getRightBox().getChildren().add(this.tags);
		
		update();
	}

	
	
	@Override
	public void update(Observable o, Object arg) {
		update();
	}
	
	/**
	 * Updates the content
	 */
	private void update() {
		this.favoriteCheckBox.setSelected(song.isFavorite());
		if (song.getAuthor().isEmpty()) {
			getLabel().setText(song.getName());
		} else {
			getLabel().setText(song.getAuthor() + " - " + song.getName());
		}
	}
	
	
	
	/**
	 * Context menu for the content
	 * @author spiffyk
	 */
	private class SongContextMenu extends ContextMenu {
		
		/**
		 * Creates a new context menu
		 */
		public SongContextMenu() {
			MenuItem editItem = new MenuItem(text.get("song.ctx.edit"));
			editItem.setOnAction((event) -> {
				Dialog<Boolean> dialog = new SongEditorDialog(song);
				dialog.initOwner(this.getOwnerWindow());
				dialog.showAndWait();
				update();
			});
			
			MenuItem openDirItem = new MenuItem(text.get("song.ctx.open_dir"));
			openDirItem.setOnAction((event) -> {
				song.openInSystemBrowser();
			});
			
			MenuItem newProjectItem = new MenuItem(text.get("song.ctx.new_project"));
			newProjectItem.setOnAction((event) -> {
				final Project project = new Project(song);
				
				final ProjectEditorDialog dialog = new ProjectEditorDialog(project);
				dialog.initOwner(this.getOwnerWindow());
				dialog.showAndWait().ifPresent((b) -> {
					if (b.booleanValue()) {
						song.getProjects().add(project);
						project.updateFiles();
					}
				});
			});
			
			MenuItem importProjectItem = new MenuItem(text.get("song.ctx.import_project"));
			importProjectItem.setOnAction((event) -> {
				
				final FileChooser chooser = new FileChooser();
				chooser.setTitle(text.get("song.import_project.title"));
				chooser.getExtensionFilters().add(new ExtensionFilter(text.get("file_type.flp"), "*.flp"));
				File file = chooser.showOpenDialog(this.getOwnerWindow());
				if (file != null) {
					Project project = new Project(song);
					project.setName(file.getName());
					
					ProjectEditorDialog dialog = new ProjectEditorDialog(project);
					dialog.initOwner(this.getOwnerWindow());
					dialog.showAndWait().ifPresent((b) -> {
						if (b.booleanValue()) {
							try {
								song.getProjects().add(project);
								project.updateFiles();
								FileUtils.copyFile(file, project.getProjectFile());
							} catch (IOException e) {
								e.printStackTrace();
								Messenger.get().message(MessageType.ERROR, text.get("song.import_project.cpy_err"), e.getMessage());
							}
						}
					});
				}
			});
			
			MenuItem deleteItem = new MenuItem(text.get("song.ctx.delete"));
			deleteItem.setOnAction((event) -> {
				final Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.initOwner(this.getOwnerWindow());
				alert.setHeaderText(null);
				alert.setContentText(text.get("song.delete_confirm"));
				ButtonType bt = alert.showAndWait().orElse(ButtonType.CANCEL);
				if (bt == ButtonType.OK) {
					song.getParent().getSongs().remove(song);
					song.delete();
				}
			});
			
			this.getItems().addAll(
					editItem,
					openDirItem,
					new SeparatorMenuItem(),
					newProjectItem,
					importProjectItem,
					new SeparatorMenuItem(),
					deleteItem);
		}
	}
}

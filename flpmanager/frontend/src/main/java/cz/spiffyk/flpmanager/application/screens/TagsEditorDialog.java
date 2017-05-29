package cz.spiffyk.flpmanager.application.screens;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import cz.spiffyk.flpmanager.data.Tag;
import cz.spiffyk.flpmanager.data.Workspace;
import cz.spiffyk.flpmanager.util.FXUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
import lombok.Getter;
import lombok.NonNull;

public class TagsEditorDialog extends Dialog<Boolean> {
	
	@Getter private final Workspace workspace;
	
	private static final Map<Tag, TagListCellContent> contents = new HashMap<>();
	
	@FXML private ListView<Tag> tags;
	
	
	
	public TagsEditorDialog(@NonNull Workspace workspace) {
		super();
		this.setTitle("Tags editor");
		this.workspace = workspace;
		
		final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("TagsEditor.fxml"));
		loader.setController(this);
		
		try {
			DialogPane pane = new DialogPane();
			
			pane.getButtonTypes().addAll(ButtonType.CLOSE);
			pane.setContent(loader.load());
			this.setDialogPane(pane);
		} catch (IOException e) {
			e.printStackTrace();
			Platform.exit();
		}
	}
	
	
	
	@FXML private void initialize() {
		tags.setCellFactory((v) -> new TagListCell());
		tags.setItems(workspace.getTags());
	}
	
	@FXML private void newTagAction() {
		final Dialog<Boolean> dialog = TagEditorDialog.newTagDialog(workspace);
		dialog.initOwner(this.getDialogPane().getScene().getWindow());
		dialog.showAndWait();
	}
	
	
	
	private class TagListCellContent extends Label implements Observer {
		
		private final Tag tag;
		
		private final ContextMenu menu;
		
		public TagListCellContent(Tag tag) {
			super();
			this.tag = tag;
			this.getStyleClass().add("tag");
			update();
			
			this.menu = new ContextMenu();
			
			MenuItem editItem = new MenuItem("Edit tag...");
			editItem.setOnAction((event) -> {
				new TagEditorDialog(this.tag).showAndWait();
				update();
			});
			
			MenuItem deleteItem = new MenuItem("Delete");
			deleteItem.setOnAction((event) -> {
				final Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setHeaderText(null);
				alert.setContentText("Do you really wish to delete this tag? (no undo)");
				ButtonType bt = alert.showAndWait().orElse(ButtonType.CANCEL);
				if (bt == ButtonType.OK) {
					workspace.getTags().remove(tag);
				}
			});
			
			this.menu.getItems().addAll(editItem, deleteItem);
		}
		
		private void update() {
			String textColor;
			if (tag.getColor().getBrightness() < 0.7) {
				textColor = "white";
			} else {
				textColor = "black";
			}
			
			this.setStyle(
					"-fx-background-color: " + FXUtils.toRGBCode(tag.getColor()) + ";"
							+ "-fx-text-fill: " + textColor + ";");
			
			this.setText(tag.getName());
		}

		@Override
		public void update(Observable o, Object arg) {
			update();
		}
	}
	
	
	
	private class TagListCell extends ListCell<Tag> {
		
		public TagListCell() {
			super();
		}
		
		@Override
		protected void updateItem(Tag item, boolean empty) {
			super.updateItem(item, empty);
			
			if (empty || item == null) {
				this.setText(null);
				this.setGraphic(null);
				this.setOnContextMenuRequested(null);
			} else {
				this.setText(null);
				TagListCellContent c = contents.get(item);
				if (c == null) {
					c = new TagListCellContent(item);
					contents.put(item, c);
				}
				
				final TagListCellContent content = c;
				
				this.setOnContextMenuRequested((event) -> {
					content.menu.show(this.getScene().getWindow(), event.getScreenX(), event.getScreenY());
				});
				
				this.setGraphic(content);
			}
		}
	}
}

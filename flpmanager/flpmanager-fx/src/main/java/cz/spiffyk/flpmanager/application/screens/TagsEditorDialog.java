package cz.spiffyk.flpmanager.application.screens;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import cz.spiffyk.flpmanager.Text;
import cz.spiffyk.flpmanager.data.Tag;
import cz.spiffyk.flpmanager.data.Workspace;
import cz.spiffyk.flpmanager.util.ManagerUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import lombok.Getter;
import lombok.NonNull;

public class TagsEditorDialog extends Dialog<Boolean> {
	
	private static final Text text = Text.get();

	private static final Map<Tag, TagListCellContent> contents = new HashMap<>();

	private static final ButtonType DELETE_BUTTON =
			new ButtonType(text.get("tag.delete_ok"), ButtonBar.ButtonData.OK_DONE);
	
	@Getter private final Workspace workspace;
	
	@FXML private ListView<Tag> tags;
	
	
	
	public TagsEditorDialog(@NonNull Workspace workspace) {
		super();
		this.setTitle(text.get("tags_edit.title"));
		this.workspace = workspace;
		
		final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/dialogs/TagsEditorDialog.fxml"));
		loader.setResources(text.getResourceBundle());
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
			
			MenuItem editItem = new MenuItem(text.get("tag.ctx.edit"));
			editItem.setOnAction((event) -> {
				Dialog<Boolean> dialog = new TagEditorDialog(this.tag);
				dialog.initOwner(getScene().getWindow());
				dialog.showAndWait();
				update();
			});
			
			MenuItem deleteItem = new MenuItem(text.get("tag.ctx.delete"));
			deleteItem.setOnAction((event) -> {
				final Alert alert = new Alert(
						AlertType.CONFIRMATION, text.get("tag.delete_confirm"), ButtonType.CANCEL, DELETE_BUTTON);
				alert.initOwner(getScene().getWindow());
				alert.setHeaderText(null);
				ButtonType bt = alert.showAndWait().orElse(ButtonType.CANCEL);
				if (bt.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
					workspace.getTags().remove(tag);
				}
			});
			
			this.menu.getItems().addAll(editItem, deleteItem);
		}
		
		private void update() {
			this.setStyle(ManagerUtils.getTagStyle(tag.getColor()));
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

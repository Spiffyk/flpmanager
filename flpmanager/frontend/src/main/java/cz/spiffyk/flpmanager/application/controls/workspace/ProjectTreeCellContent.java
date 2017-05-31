package cz.spiffyk.flpmanager.application.controls.workspace;

import java.util.Observable;
import java.util.Observer;

import cz.spiffyk.flpmanager.application.screens.ProjectEditorDialog;
import cz.spiffyk.flpmanager.data.Project;
import cz.spiffyk.flpmanager.data.Project.RenderFormat;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Alert.AlertType;

/**
 * The content box of a tree cell representing a {@link Project}
 * @author spiffyk
 */
public class ProjectTreeCellContent extends WorkspaceNodeTreeCellContent<Project> implements Observer {
	
	/**
	 * The context menu
	 */
	private final ContextMenu contextMenu;
	
	/**
	 * The {@link Project} represented by this node
	 */
	private final Project project;
	
	
	
	/**
	 * Creates a new tree cell content box representing the specified {@link Project}.
	 * @param node The {@link Project} to represent
	 */
	public ProjectTreeCellContent(Project node) {
		super(node);
		
		this.project = node;
		
		this.contextMenu = new ProjectContextMenu();
		this.setOnContextMenuRequested((event) -> {
			this.contextMenu.show(this.getScene().getWindow(), event.getScreenX(), event.getScreenY());
			event.consume();
		});
		
		getStyleClass().add("project-cell");
		getLabel().setText(node.getName());
		final Button openButton = new Button("Open");
		openButton.setOnAction((event) -> {
			node.openProject();
		});
		getButtonBox().getChildren().add(openButton);
	}

	
	
	@Override
	public void update(Observable o, Object arg) {
		update();
	}
	
	/**
	 * Updates the content
	 */
	private void update() {
		getLabel().setText(project.getName());
	}
	
	
	
	/**
	 * Context menu for the content
	 * @author spiffyk
	 */
	private class ProjectContextMenu extends ContextMenu {
		
		/**
		 * Creates a new context menu
		 */
		public ProjectContextMenu() {
			MenuItem editItem = new MenuItem("_Rename...");
			editItem.setOnAction((event) -> {
				Dialog<Boolean> dialog = new ProjectEditorDialog(project);
				dialog.initOwner(this.getOwnerWindow());
				dialog.showAndWait();
				update();
			});
			
			MenuItem cloneItem = new MenuItem("Clone");
			cloneItem.setOnAction((event) -> {
				project.copy(true);
			});
			
			Menu renderMenu = new Menu("Render");
			
			MenuItem renderWavItem = new MenuItem("WAV");
			renderWavItem.setOnAction((event) -> project.renderProject(RenderFormat.WAV));
			
			MenuItem renderMp3Item = new MenuItem("MP3");
			renderMp3Item.setOnAction((event) -> project.renderProject(RenderFormat.MP3));
			
			MenuItem renderVorbisItem = new MenuItem("OGG Vorbis");
			renderVorbisItem.setOnAction((event) -> project.renderProject(RenderFormat.VORBIS));
			
			MenuItem renderFlacItem = new MenuItem("FLAC");
			renderFlacItem.setOnAction((event) -> project.renderProject(RenderFormat.FLAC));
			
			renderMenu.getItems().addAll(renderWavItem, renderMp3Item, renderVorbisItem, renderFlacItem);
			
			MenuItem deleteItem = new MenuItem("Delete");
			deleteItem.setOnAction((event) -> {
				final Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.initOwner(this.getOwnerWindow());
				alert.setHeaderText(null);
				alert.setContentText("Do you really wish to delete this project? (no undo)");
				ButtonType bt = alert.showAndWait().orElse(ButtonType.CANCEL);
				if (bt == ButtonType.OK) {
					project.getParent().getProjects().remove(project);
					project.delete();
				}
			});
			
			this.getItems().addAll(
					editItem,
					cloneItem,
					new SeparatorMenuItem(),
					renderMenu,
					new SeparatorMenuItem(),
					deleteItem);
		}
	}
}

package cz.spiffyk.flpmanager.application.controls.workspace;

import java.util.Observable;
import java.util.Observer;

import cz.spiffyk.flpmanager.Text;
import cz.spiffyk.flpmanager.application.screens.ProjectEditorDialog;
import cz.spiffyk.flpmanager.data.Project;
import cz.spiffyk.flpmanager.data.Project.RenderFormat;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;

/**
 * The content box of a tree cell representing a {@link Project}
 * @author spiffyk
 */
public class ProjectTreeCellContent extends WorkspaceNodeTreeCellContent<Project> implements Observer {
	
	private static final Text text = Text.get();

	private static final ButtonType DELETE_BUTTON =
			new ButtonType(text.get("project.delete_ok"), ButtonBar.ButtonData.OK_DONE);
	
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

		getStyleClass().add("project-cell");
		getLabel().setText(node.getName());
		final Button openButton = new Button(text.get("project.open"));
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

	@Override
	public void onContextMenu(ContextMenuEvent event) {
		this.contextMenu.show(this.getScene().getWindow(), event.getScreenX(), event.getScreenY());
		event.consume();
	}

	@Override
	public void onMouseClick(MouseEvent event) {
		if (event.getClickCount() == 2) {
			project.openProject();
		}
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
			MenuItem editItem = new MenuItem(text.get("project.ctx.edit"));
			editItem.setOnAction((event) -> {
				Dialog<Boolean> dialog = new ProjectEditorDialog(project);
				dialog.initOwner(this.getOwnerWindow());
				dialog.showAndWait();
				update();
			});
			
			MenuItem cloneItem = new MenuItem(text.get("project.ctx.clone"));
			cloneItem.setOnAction((event) -> {
				project.copy(true);
			});
			
			MenuItem openDirItem = new MenuItem(text.get("project.ctx.open_dir"));
			openDirItem.setOnAction((event) -> {
				project.getParent().openInSystemBrowser();
			});
			
			Menu renderMenu = new Menu(text.get("project.ctx.render"));
			
			MenuItem renderWavItem = new MenuItem(text.get("audio_format.wav"));
			renderWavItem.setOnAction((event) -> project.renderProject(RenderFormat.WAV));
			
			MenuItem renderMp3Item = new MenuItem(text.get("audio_format.mp3"));
			renderMp3Item.setOnAction((event) -> project.renderProject(RenderFormat.MP3));
			
			MenuItem renderVorbisItem = new MenuItem(text.get("audio_format.vorbis"));
			renderVorbisItem.setOnAction((event) -> project.renderProject(RenderFormat.VORBIS));
			
			MenuItem renderFlacItem = new MenuItem(text.get("audio_format.flac"));
			renderFlacItem.setOnAction((event) -> project.renderProject(RenderFormat.FLAC));
			
			renderMenu.getItems().addAll(renderWavItem, renderMp3Item, renderVorbisItem, renderFlacItem);
			
			MenuItem deleteItem = new MenuItem(text.get("project.ctx.delete"));
			deleteItem.setOnAction((event) -> {
				final Alert alert = new Alert(
						AlertType.CONFIRMATION, text.get("project.delete_confirm"), ButtonType.CANCEL, DELETE_BUTTON);
				alert.initOwner(this.getOwnerWindow());
				alert.setHeaderText(null);
				ButtonType bt = alert.showAndWait().orElse(ButtonType.CANCEL);
				if (bt.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
					project.getParent().getProjects().remove(project);
					project.delete();
				}
			});
			
			this.getItems().addAll(
					editItem,
					cloneItem,
					openDirItem,
					new SeparatorMenuItem(),
					renderMenu,
					new SeparatorMenuItem(),
					deleteItem);
		}
	}
}

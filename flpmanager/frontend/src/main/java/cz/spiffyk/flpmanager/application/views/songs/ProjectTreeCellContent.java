package cz.spiffyk.flpmanager.application.views.songs;

import java.util.Observable;
import java.util.Observer;

import cz.spiffyk.flpmanager.application.screens.ProjectEditorDialog;
import cz.spiffyk.flpmanager.data.Project;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Alert.AlertType;

public class ProjectTreeCellContent extends WorkspaceNodeTreeCellContent<Project> implements Observer {

	private final ContextMenu contextMenu;
	private final Project project;
	
	public ProjectTreeCellContent(Project node) {
		super(node);
		
		this.project = node;
		
		this.contextMenu = new ProjectContextMenu();
		this.setOnContextMenuRequested((event) -> {
			this.contextMenu.show(this.getScene().getWindow(), event.getScreenX(), event.getScreenY());
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
	
	private void update() {
		getLabel().setText(project.getName());
	}
	
	private class ProjectContextMenu extends ContextMenu {
		public ProjectContextMenu() {
			MenuItem editItem = new MenuItem("_Rename...");
			editItem.setOnAction((event) -> {
				new ProjectEditorDialog(project).showAndWait();
				update();
			});
			
			MenuItem cloneItem = new MenuItem("Clone");
			cloneItem.setOnAction((event) -> {
				project.copy(true);
			});
			
			MenuItem deleteItem = new MenuItem("Delete");
			deleteItem.setOnAction((event) -> {
				final Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setHeaderText(null);
				alert.setContentText("Do you really wish to delete this project? (no undo)");
				ButtonType bt = alert.showAndWait().orElse(ButtonType.CANCEL);
				if (bt == ButtonType.OK) {
					project.getParent().getProjects().remove(project);
					project.delete();
				}
			});
			
			this.getItems().addAll(editItem, cloneItem, new SeparatorMenuItem(), deleteItem);
		}
	}


	
	
}

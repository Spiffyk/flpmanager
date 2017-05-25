package cz.spiffyk.flpmanager.application.controls;

import cz.spiffyk.flpmanager.data.Project;
import javafx.scene.control.Button;

public class ProjectTreeCellContent extends WorkspaceNodeTreeCellContent<Project> {

	public ProjectTreeCellContent(Project node) {
		super(node);
		getStyleClass().add("project-cell");
		getLabel().setText(node.getName());
		final Button openButton = new Button("Open");
		openButton.setOnAction((event) -> {
			node.openProject();
		});
		getButtonBox().getChildren().add(openButton);
	}

}

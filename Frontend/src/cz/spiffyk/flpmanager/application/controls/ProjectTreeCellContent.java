package cz.spiffyk.flpmanager.application.controls;

import cz.spiffyk.flpmanager.data.Project;

public class ProjectTreeCellContent extends WorkspaceNodeTreeCellContent<Project> {

	public ProjectTreeCellContent(Project node) {
		super(node);
		getLabel().setText(node.getName());
	}

}

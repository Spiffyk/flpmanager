package cz.spiffyk.flpmanager.application;

import java.util.Comparator;

import cz.spiffyk.flpmanager.data.Project;
import cz.spiffyk.flpmanager.data.WorkspaceNode;
import javafx.scene.control.TreeItem;

public class ProjectsListener extends WorkspaceNodeListener {

	private static final Comparator<TreeItem<WorkspaceNode>> NAME_COMPARATOR = new Project.NameComparator();
	
	public ProjectsListener(TreeItem<WorkspaceNode> parent) {
		super(parent);
	}

	@Override
	public void sort() {
		parent.getChildren().sort(NAME_COMPARATOR);
	}

	@Override
	protected TreeItem<WorkspaceNode> createTreeItem(WorkspaceNode node) {
		return new TreeItem<>(node);
	}
}

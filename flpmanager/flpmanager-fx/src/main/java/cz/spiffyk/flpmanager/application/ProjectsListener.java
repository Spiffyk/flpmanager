package cz.spiffyk.flpmanager.application;

import java.util.Comparator;

import cz.spiffyk.flpmanager.data.Project;
import cz.spiffyk.flpmanager.data.WorkspaceNode;
import javafx.scene.control.TreeItem;

/**
 * A listener for observing {@link Project}s and updating the {@link TreeItem} accordingly.
 * @author spiffyk
 */
public class ProjectsListener extends WorkspaceNodeListener {
	
	/**
	 * Comparator for sorting {@link Project}s by name
	 */
	private static final Comparator<TreeItem<WorkspaceNode>> NAME_COMPARATOR = new Project.NameComparator();
	
	
	
	/**
	 * Creates a new {@code ProjectsListener} updating the specified {@link TreeItem}
	 * @param parent The {@link TreeItem} to update
	 */
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

package cz.spiffyk.flpmanager.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import cz.spiffyk.flpmanager.data.WorkspaceNode;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TreeItem;

public abstract class WorkspaceNodeListener implements ListChangeListener<WorkspaceNode>, Observer {
	
	protected TreeItem<WorkspaceNode> parent;
	
	public WorkspaceNodeListener(final TreeItem<WorkspaceNode> parent) {
		if (parent == null) {
			throw new IllegalArgumentException("Parent cannot be null");
		}
		
		this.parent = parent;
		sort();
	}

	@Override
	public void onChanged(ListChangeListener.Change<? extends WorkspaceNode> c) {
		final List<TreeItem<WorkspaceNode>> children = parent.getChildren();
		final List<TreeItem<WorkspaceNode>> toRemove = new ArrayList<>();
		final List<TreeItem<WorkspaceNode>> toAdd = new ArrayList<>();
		while(c.next() == true) {
			final List<? extends WorkspaceNode> removed = c.getRemoved();
			final List<? extends WorkspaceNode> added = c.getAddedSubList();
			
			// remove children
			for (final TreeItem<WorkspaceNode> item : children) {
				if (removed.contains(item.getValue())) {
					toRemove.add(item);
				}
			}
			
			// add children
			for (final WorkspaceNode node : added) {
				final TreeItem<WorkspaceNode> item = createTreeItem(node);
				toAdd.add(item);
			}
		}
		children.removeAll(toRemove);
		children.addAll(toAdd);
		sort();
	}
	
	protected abstract TreeItem<WorkspaceNode> createTreeItem(WorkspaceNode node);
	
	public void sort() {}
	
	@Override
	public void update(Observable o, Object arg) {
		sort();
	}
}

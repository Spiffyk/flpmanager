package cz.spiffyk.flpmanager.application;

import java.util.*;
import java.util.function.Predicate;

import cz.spiffyk.flpmanager.data.WorkspaceNode;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TreeItem;
import lombok.Getter;

/**
 * An abstract listener for observing a {@link WorkspaceNode} and updating the {@link TreeItem} accordingly.
 * @author spiffyk
 */
public abstract class WorkspaceNodeListener implements ListChangeListener<WorkspaceNode>, Observer {
	
	/**
	 * The {@link TreeItem} to update.
	 */
	protected TreeItem<WorkspaceNode> parent;

	/**
	 * Items hidden by a predicate from {@link #hidingPredicateSet}.
	 */
	private List<TreeItem<WorkspaceNode>> hiddenItemList = new ArrayList<>();

	/**
	 * {@link Predicate}s which determine whether an item should be hidden from the list or not. If one of
	 * the predicates returns {@code true}, the checked item is hidden.
	 */
	@Getter private Set<Predicate<WorkspaceNode>> hidingPredicateSet = new HashSet<>();
	
	
	/**
	 * Creates a new listener updating the specified {@link TreeItem}
	 * @param parent The {@link TreeItem} to update
	 */
	public WorkspaceNodeListener(final TreeItem<WorkspaceNode> parent) {
		if (parent == null) {
			throw new IllegalArgumentException("Parent cannot be null");
		}
		
		this.parent = parent;
		sort();
	}

	@Override
	public void onChanged(ListChangeListener.Change<? extends WorkspaceNode> c) {
		unhideAll();
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
		hideByPredicates();
		parent.setExpanded(true);
		sort();
	}

	private void unhideAll() {
		parent.getChildren().addAll(hiddenItemList);
		hiddenItemList.clear();
	}

	private void hideByPredicates() {
		final Iterator<TreeItem<WorkspaceNode>> iterator = parent.getChildren().iterator();
		while (iterator.hasNext()) {
			final TreeItem<WorkspaceNode> item = iterator.next();
			for (final Predicate<WorkspaceNode> predicate : hidingPredicateSet) {
				if (predicate.test(item.getValue())) {
					iterator.remove();
					hiddenItemList.add(item);
					break;
				}
			}
		}
	}

	public void update() {
		unhideAll();
		hideByPredicates();
		sort();
	}
	
	/**
	 * Creates a {@link TreeItem} for the specified node.
	 * @param node The node to be assigned to the {@link TreeItem}
	 * @return A {@link TreeItem} containing the node.
	 */
	protected abstract TreeItem<WorkspaceNode> createTreeItem(WorkspaceNode node);
	
	/**
	 * Sorts the children of the underlying {@link TreeItem}
	 */
	public void sort() {}
	
	@Override
	public void update(Observable o, Object arg) {
		update();
	}
}

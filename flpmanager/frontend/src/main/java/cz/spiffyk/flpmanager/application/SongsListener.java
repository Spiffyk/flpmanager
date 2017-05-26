package cz.spiffyk.flpmanager.application;

import java.util.Comparator;

import cz.spiffyk.flpmanager.application.controls.SongTreeItem;
import cz.spiffyk.flpmanager.data.Song;
import cz.spiffyk.flpmanager.data.WorkspaceNode;
import javafx.scene.control.TreeItem;

/**
 * A listener for observing {@link Song}s and updating the {@link TreeItem} accordingly.
 * @author spiffyk
 */
public class SongsListener extends WorkspaceNodeListener {
	
	/**
	 * Comparator for sorting {@link Songs}s by name
	 */
	private static final Comparator<TreeItem<WorkspaceNode>> NAME_COMPARATOR = new Song.NameComparator();
	
	/**
	 * Comparator for sorting {@link Songs}s marked as favorite first
	 */
	private static final Comparator<TreeItem<WorkspaceNode>> FAVORITE_COMPARATOR = new Song.FavoriteComparator();
	
	
	
	/**
	 * Creates a new {@code SongsListener} updating the specified {@link TreeItem}
	 * @param parent The {@link TreeItem} to update
	 */
	public SongsListener(TreeItem<WorkspaceNode> parent) {
		super(parent);
	}
	
	
	
	@Override
	public void sort() {
		parent.getChildren().sort(NAME_COMPARATOR);
		parent.getChildren().sort(FAVORITE_COMPARATOR);
	}

	@Override
	protected TreeItem<WorkspaceNode> createTreeItem(WorkspaceNode node) {
		return new SongTreeItem((Song) node);
	}

}

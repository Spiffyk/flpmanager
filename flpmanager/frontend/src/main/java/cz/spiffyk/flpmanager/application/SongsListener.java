package cz.spiffyk.flpmanager.application;

import java.util.Comparator;

import cz.spiffyk.flpmanager.application.controls.workspace.SongTreeItem;
import cz.spiffyk.flpmanager.application.views.songs.SongsView;
import cz.spiffyk.flpmanager.data.Song;
import cz.spiffyk.flpmanager.data.WorkspaceNode;
import javafx.scene.control.TreeItem;
import lombok.NonNull;

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
	
	
	
	private final SongsView songsView;
	
	
	/**
	 * Creates a new {@code SongsListener} updating the specified {@link TreeItem}
	 * @param parent The {@link TreeItem} to update
	 */
	public SongsListener(@NonNull TreeItem<WorkspaceNode> parent, @NonNull SongsView songsView) {
		super(parent);
		this.songsView = songsView;
	}
	
	
	
	
	@Override
	public void onChanged(javafx.collections.ListChangeListener.Change<? extends WorkspaceNode> c) {
		super.onChanged(c);
		songsView.setShowPlaceholder(parent.getChildren().isEmpty());
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

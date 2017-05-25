package cz.spiffyk.flpmanager.application;

import java.util.Comparator;

import cz.spiffyk.flpmanager.application.controls.SongTreeItem;
import cz.spiffyk.flpmanager.data.Song;
import cz.spiffyk.flpmanager.data.WorkspaceNode;
import javafx.scene.control.TreeItem;

public class SongsListener extends WorkspaceNodeListener {
	
	private static final Comparator<TreeItem<WorkspaceNode>> NAME_COMPARATOR = new Song.NameComparator();
	private static final Comparator<TreeItem<WorkspaceNode>> FAVORITE_COMPARATOR = new Song.FavoriteComparator();
	

	public SongsListener(TreeItem<WorkspaceNode> parent) {
		super(parent);
	}
	
	@Override
	public void onChanged(javafx.collections.ListChangeListener.Change<? extends WorkspaceNode> c) {
		super.onChanged(c);
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

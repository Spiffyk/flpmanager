package cz.spiffyk.flpmanager.application.screens.songs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.spiffyk.flpmanager.SongManager;
import cz.spiffyk.flpmanager.application.controls.SongTreeItem;
import cz.spiffyk.flpmanager.data.Song;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public final class SongsView extends TreeView<ObservableList<Song>> {
	
	private SongManager songManager;
	
	@FXML private TreeView<ObservableList<Song>> songsView;
	@FXML private TreeItem<Object> songsRoot;
	
	public SongsView() {
		final FXMLLoader loader = new FXMLLoader(getClass().getResource("SongsView.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the song manager to work with
	 * @param songManager
	 */
	public void setSongManager(SongManager songManager) {
		this.songManager = songManager;
		updateSongs();
	}
	
	public void updateSongs() {
		final ObservableList<TreeItem<Object>> itemList = songsRoot.getChildren();
		itemList.clear();
		if (songManager != null) {
			final List<Song> songs = songManager.getSongs();
			final List<TreeItem<Object>> newItemList = new ArrayList<>();
			
			for (Song s : songs) {
				newItemList.add(new SongTreeItem(s));
			}
			
			itemList.addAll(newItemList);
		}
	}
}

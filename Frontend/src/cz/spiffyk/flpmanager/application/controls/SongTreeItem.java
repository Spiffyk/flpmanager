package cz.spiffyk.flpmanager.application.controls;

import cz.spiffyk.flpmanager.data.Song;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.FlowPane;

public class SongTreeItem extends TreeItem<Object> {
	
	/**
	 * The controls inside of the tree item
	 */
	final SongTreeItemContent content;
	
	/**
	 * The song represented by the tree item
	 */
	final Song song;
	
	/**
	 * Creates a new tree item representing the given song
	 * @param song The song to be represented by the item
	 */
	public SongTreeItem(Song song) {
		super();
		this.song = song;
		this.content = new SongTreeItemContent(song);
		this.content.favoriteCheckBox.setSelected(song.isFavorite());
		this.content.favoriteCheckBox.setOnAction(e -> {
			song.setFavorite(this.content.favoriteCheckBox.isSelected());
		});
		this.setValue(content);
	}
	
	/**
	 * Gets the song represented by the tree item
	 * @return The song
	 */
	public Song getSong() {
		return song;
	}
	
	/**
	 * The controls of the {@code SongTreeItem}
	 * @author spiffyk
	 */
	public static class SongTreeItemContent extends FlowPane {
		final FavoriteCheckBox favoriteCheckBox;
		final Label label;
		
		private SongTreeItemContent(Song s) {
			favoriteCheckBox = new FavoriteCheckBox();
			label = new Label(s.toString());
			this.getChildren().add(favoriteCheckBox);
			this.getChildren().add(label);
		}
		
		private static class FavoriteCheckBox extends CheckBox {
			public FavoriteCheckBox() {
				super();
				this.getStyleClass().add("favorite-check-box");
			}
		}
	}
	
	
}

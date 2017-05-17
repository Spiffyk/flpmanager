package cz.spiffyk.flpmanager.application.controls;

import java.util.Observable;
import java.util.Observer;

import cz.spiffyk.flpmanager.data.Song;
import javafx.scene.control.CheckBox;

public class SongTreeCellContent extends WorkspaceNodeTreeCellContent<Song> implements Observer {
	
	private final Song song;
	private final CheckBox favoriteCheckBox;
	
	public SongTreeCellContent(Song node) {
		super(node);
		this.song = node;
		node.addObserver(this);
		this.favoriteCheckBox = new CheckBox();
		this.favoriteCheckBox.getStyleClass().add("favorite-check-box");
		this.favoriteCheckBox.setOnAction((event) -> {
			song.setFavorite(this.favoriteCheckBox.isSelected());
		});
		getLeftBox().getChildren().add(this.favoriteCheckBox);
		
		update();
	}

	@Override
	public void update(Observable o, Object arg) {
		update();
	}
	
	private void update() {
		this.favoriteCheckBox.setSelected(song.isFavorite());
		getLabel().setText(song.getAuthor() + " - " + song.getName());
	}
	
}

package cz.spiffyk.flpmanager.data;

import java.util.Observable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;

/**
 * Contains song metadata.
 * @author spiffyk
 */
public class Song extends Observable implements WorkspaceNode {

	@Getter @Setter private final ObservableList<Project> projects = FXCollections.observableArrayList();
	@Getter @Setter private final ObservableList<Tag> tags = FXCollections.observableArrayList();
	
	@Getter @Setter private boolean favorite;
	@Getter @Setter private String name;
	@Getter @Setter private String author;
	
	/**
	 * Creates a song with empty name, author and not marked as favorite.
	 */
	public Song() {
		this.setName("");
		this.setAuthor("");
		this.setFavorite(false);
	}

	@Override
	public String toString() {
		return ((this.author.isEmpty()) ? "" : this.author + " - ") + this.name;
	}
	
	@Override
	public WorkspaceNodeType getType() {
		return WorkspaceNodeType.SONG;
	}
}

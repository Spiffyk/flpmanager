package cz.spiffyk.flpmanager.application.controls.tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import cz.spiffyk.flpmanager.data.Tag;
import cz.spiffyk.flpmanager.util.ManagerUtils;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.NonNull;

/**
 * A HBox showing tags in the given model
 * @author spiffyk
 */
public class TagsViewer extends HBox implements ListChangeListener<Tag> {
	
	/**
	 * The {@link Tag}s to render
	 */
	@Getter private ObservableList<Tag> tags;
	
	/**
	 * The list of {@link Label}s representing the {@link Tag}s
	 */
	private List<TagLabel> labels = new ArrayList<>();
	
	
	
	/**
	 * Creates a new tags viewer
	 */
	public TagsViewer() {
		super();
		this.getStyleClass().add("tags-viewer");
		this.setSpacing(3);
	}
	
	
	
	/**
	 * Sets the list of {@link Tag}s that this viewer should observe. If a list is already being observed, the viewer
	 * is automatically unregistered as a listener from that list.
	 * @param tags The list to observe
	 */
	public void setTags(ObservableList<Tag> tags) {
		if (this.tags != null) {
			this.tags.removeListener(this);
		}
		
		this.tags = tags;
		tags.addListener(this);
		updateLabels();
	}
	
	/**
	 * Updates labels
	 */
	private void updateLabels() {
		for (TagLabel label : labels) {
			label.cleanUp();
		}
		labels.clear();
		
		for (Tag tag : tags) {
			labels.add(new TagLabel(tag));
		}
		labels.sort((a, b) -> a.getTag().getName().compareTo(b.getTag().getName()));
		this.getChildren().clear();
		this.getChildren().addAll(labels);
	}
	
	@Override
	public void onChanged(javafx.collections.ListChangeListener.Change<? extends Tag> c) {
		updateLabels();
	}
	
	/**
	 * A label observing a {@link Tag}
	 * @author spiffyk
	 */
	private class TagLabel extends Label implements Observer {
		
		/**
		 * The {@link Tag} this label is observing
		 */
		@Getter private final Tag tag;
		
		
		
		/**
		 * Creates a new tag label
		 * @param tag
		 */
		TagLabel(@NonNull Tag tag) {
			this.tag = tag;
			this.getStyleClass().add("tag");
			tag.addObserver(this);
			update();
		}
		
		
		
		@Override
		public void update(Observable o, Object arg) {
			update();
		}
		
		/**
		 * Updates the label
		 */
		private void update() {
			this.setText(tag.getName());
			this.setStyle(ManagerUtils.getTagStyle(tag.getColor()));
		}
		
		/**
		 * Stops observing the {@link Tag} and prepares for cleanup
		 */
		public void cleanUp() {
			tag.deleteObserver(this);
			this.setVisible(false);
		}
	}
}

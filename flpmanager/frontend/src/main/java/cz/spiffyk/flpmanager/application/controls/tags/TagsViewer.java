package cz.spiffyk.flpmanager.application.controls.tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import cz.spiffyk.flpmanager.data.Tag;
import cz.spiffyk.flpmanager.util.FXUtils;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.NonNull;

public class TagsViewer extends HBox implements ListChangeListener<Tag> {
	
	@Getter private ObservableList<Tag> tags;
	
	private List<TagLabel> labels = new ArrayList<>();
	
	public TagsViewer() {
		super();
		this.getStyleClass().add("tags-viewer");
		this.setSpacing(3);
	}
	
	public void setTags(ObservableList<Tag> tags) {
		if (this.tags != null) {
			this.tags.removeListener(this);
		}
		
		this.tags = tags;
		tags.addListener(this);
		updateLabels();
	}
	
	private void updateLabels() {
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
	
	private class TagLabel extends Label implements Observer {
		@Getter private final Tag tag;
		
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
		
		private void update() {
			this.setText(tag.getName());
			this.setStyle(FXUtils.getTagStyle(tag.getColor()));
		}
	}
}

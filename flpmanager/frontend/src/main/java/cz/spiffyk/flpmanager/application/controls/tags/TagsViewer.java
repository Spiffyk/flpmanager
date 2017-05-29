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
			this.setText(tag.getName());
			
			String textColor;
			if (tag.getColor().getBrightness() < 0.7) {
				textColor = "white";
			} else {
				textColor = "black";
			}
			
			this.setStyle(
					"-fx-background-color: " + FXUtils.toRGBCode(tag.getColor()) + ";"
							+ "-fx-text-fill: " + textColor + ";");
		}

		@Override
		public void update(Observable o, Object arg) {
			this.setText(tag.getName());
		}
	}
}

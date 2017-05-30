package cz.spiffyk.flpmanager.application.controls.tags;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cz.spiffyk.flpmanager.data.Tag;
import cz.spiffyk.flpmanager.util.FXUtils;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

/**
 * A list view for picking {@link Tag}s to mark a {@link Song} with
 * @author spiffyk
 */
public class TagsSelector extends ListView<Tag> {
	
	/**
	 * {@link Tag}s to mark the {@link Song} with
	 */
	private List<Tag> selectedTags;
	
	
	
	/**
	 * Creates a new empty selector
	 */
	public TagsSelector() {
		super();
		this.setCellFactory((view) -> new TagListCell());
	}
	
	
	
	/**
	 * Marks {@link Tag}s in the provided {@link Collection} as selected
	 * @param tags The {@link Tag}s to be selected
	 */
	public void setSelected(Collection<Tag> tags) {
		if (this.selectedTags == null) {
			this.selectedTags = new ArrayList<>();
		} else {
			this.selectedTags.clear();
		}
		
		this.selectedTags.addAll(tags);
	}
	
	/**
	 * Gets the {@link List} of selected {@link Tag}s
	 * @return Selected {@link Tag}s
	 */
	public List<Tag> getSelected() {
		return selectedTags;
	}
	
	
	
	
	/**
	 * The cell to be rendered in the {@link TagsSelector}
	 * @author spiffyk
	 */
	private class TagListCell extends ListCell<Tag> {
		
		/**
		 * Checkbox for marking as selected
		 */
		private CheckBox checkbox;
		
		/**
		 * The label rendered as a tag
		 */
		private Label label;
		
		/**
		 * The box set as the graphic of the cell, containing the checkbox and the label
		 */
		private HBox box;
		
		/**
		 * Creates a new cell
		 */
		public TagListCell() {
			super();
			
			this.box = new HBox();
			this.checkbox = new CheckBox();
			this.label = new Label();
			this.label.getStyleClass().add("tag");
			
			this.box.getChildren().addAll(this.checkbox, this.label);
		}
		
		@Override
		protected void updateItem(Tag item, boolean empty) {
			super.updateItem(item, empty);
			
			if (empty || item == null) {
				this.setText(null);
				this.setGraphic(null);
			} else {
				checkbox.setSelected(selectedTags != null && selectedTags.contains(item));
				checkbox.setOnAction((e) -> {
					if (selectedTags != null) {
						if (checkbox.isSelected()) {
							if (!selectedTags.contains(item)) {
								selectedTags.add(item);
							}
						} else {
							selectedTags.remove(item);
						}
					}
				});
				
				this.label.setStyle(FXUtils.getTagStyle(item.getColor()));
				this.label.setText(item.getName());
				
				this.setText(null);
				this.setGraphic(box);
			}
		}
	}
}

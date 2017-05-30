package cz.spiffyk.flpmanager.application.controls.tags;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cz.spiffyk.flpmanager.data.Tag;
import cz.spiffyk.flpmanager.util.FXUtils;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

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
		 * Creates a new cell
		 */
		public TagListCell() {
			super();
			
			this.checkbox = new CheckBox();
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
				
				String textColor;
				if (item.getColor().getBrightness() < 0.7) {
					textColor = "white";
				} else {
					textColor = "black";
				}
				
				this.setStyle(
						"-fx-background-color: " + FXUtils.toRGBCode(item.getColor()) + ";"
								+ "-fx-text-fill: " + textColor + ";");
				
				this.setText(item.getName());
				this.setGraphic(checkbox);
			}
		}
	}
}

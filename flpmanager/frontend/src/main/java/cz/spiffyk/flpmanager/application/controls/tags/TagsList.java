package cz.spiffyk.flpmanager.application.controls.tags;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cz.spiffyk.flpmanager.data.Tag;
import cz.spiffyk.flpmanager.util.FXUtils;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class TagsList extends ListView<Tag> {
	
	private List<Tag> tags;
	
	public TagsList() {
		super();
		this.setCellFactory((view) -> new TagListCell());
	}
	
	public void setSelected(Collection<Tag> tags) {
		if (this.tags == null) {
			this.tags = new ArrayList<>();
		} else {
			this.tags.clear();
		}
		
		this.tags.addAll(tags);
	}
	
	public List<Tag> getSelected() {
		return tags;
	}
	
	public class TagListCell extends ListCell<Tag> {
		private CheckBox checkbox;
		
		public TagListCell() {
			super();
			
			this.checkbox = new CheckBox();
		}
		
		public void setChecked(boolean selected) {
			this.checkbox.setSelected(selected);
		}
		
		public boolean isChecked() {
			return this.checkbox.isSelected();
		}
		
		@Override
		protected void updateItem(Tag item, boolean empty) {
			super.updateItem(item, empty);
			
			if (empty || item == null) {
				this.setText(null);
				this.setGraphic(null);
			} else {
				checkbox.setSelected(tags != null && tags.contains(item));
				checkbox.setOnAction((e) -> {
					if (tags != null) {
						if (checkbox.isSelected()) {
							if (!tags.contains(item)) {
								tags.add(item);
							}
						} else {
							tags.remove(item);
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

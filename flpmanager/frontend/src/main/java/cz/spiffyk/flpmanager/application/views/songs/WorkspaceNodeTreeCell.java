package cz.spiffyk.flpmanager.application.views.songs;

import java.util.HashMap;
import java.util.Map;

import cz.spiffyk.flpmanager.data.Project;
import cz.spiffyk.flpmanager.data.Song;
import cz.spiffyk.flpmanager.data.WorkspaceNode;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;

public class WorkspaceNodeTreeCell extends TreeCell<WorkspaceNode> {

	private static final Map<WorkspaceNode, Node> contents = new HashMap<>();

	public WorkspaceNodeTreeCell() {
		super();
		this.setText(null);
	}

	@Override
	protected void updateItem(WorkspaceNode item, boolean empty) {
		super.updateItem(item, empty);
		if (empty || item == null) {
			this.setGraphic(null);
		} else {
			Node content = contents.get(item);
			
			if (content == null) {
				switch (item.getType()) {
				case PROJECT:
					content = new ProjectTreeCellContent((Project) item);
					break;
					
				case SONG:
					content = new SongTreeCellContent((Song) item);
					break;
					
				default:
					throw new UnsupportedOperationException("Unknown item");
				}
				
				contents.put(item, content);
			}
			
			this.setGraphic(content);
		}
	}
}

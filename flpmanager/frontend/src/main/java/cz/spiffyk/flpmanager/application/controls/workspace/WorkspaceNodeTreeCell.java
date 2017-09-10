package cz.spiffyk.flpmanager.application.controls.workspace;

import java.util.HashMap;
import java.util.Map;

import cz.spiffyk.flpmanager.data.Project;
import cz.spiffyk.flpmanager.data.Song;
import cz.spiffyk.flpmanager.data.WorkspaceNode;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

/**
 * A {@link TreeCell} for {@link WorkspaceNode}s
 * @author spiffyk
 */
public class WorkspaceNodeTreeCell extends TreeCell<WorkspaceNode> {

	/**
	 * A map of contents for {@link WorkspaceNode}s. (An optimization so that contents are not recreated after each
	 * redraw)
	 */
	private static final Map<WorkspaceNode, Node> contents = new HashMap<>();

	
	
	/**
	 * Creates a new tree cell
	 */
	public WorkspaceNodeTreeCell() {
		super();
		this.setText(null);

		this.setOnContextMenuRequested((event) -> {
			if (this.getGraphic() instanceof WorkspaceNodeTreeCellContent) {
				((WorkspaceNodeTreeCellContent) this.getGraphic()).onContextMenu(event);
			}
		});

		this.setOnMouseClicked((event) -> {
			if (this.getGraphic() instanceof WorkspaceNodeTreeCellContent) {
				((WorkspaceNodeTreeCellContent) this.getGraphic()).onMouseClick(event);
			}
		});
	}
	
	
	
	/**
	 * Gets the factory callback for this tree cell (used in FXML).
	 * @return The factory
	 */
	public static Callback<TreeView<WorkspaceNode>, TreeCell<WorkspaceNode>> factory() {
		return (view) -> new WorkspaceNodeTreeCell();
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

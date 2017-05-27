package cz.spiffyk.flpmanager.application.views.songs;

import cz.spiffyk.flpmanager.data.WorkspaceNode;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public abstract class WorkspaceNodeTreeCellContent<N extends WorkspaceNode> extends HBox {
	
	private final Label label;
	private final HBox buttonBox;
	private final HBox leftBox;
	
	private final N node;
	
	public WorkspaceNodeTreeCellContent(final N node) {
		super();
		getStyleClass().add("workspace-node-cell");
		this.node = node;
		
		this.leftBox = new HBox();
		this.label = new Label();
		this.buttonBox = new HBox();
		
		final Region separatorRegion = new Region();
		HBox.setHgrow(separatorRegion, Priority.ALWAYS);
		
		this.getChildren().addAll(this.leftBox, this.label, separatorRegion, this.buttonBox);
	}
	
	public final N getNode() {
		return node;
	}
	
	protected final Label getLabel() {
		return label;
	}
	
	protected final Pane getButtonBox() {
		return buttonBox;
	}
	
	protected final Pane getLeftBox() {
		return leftBox;
	}
}

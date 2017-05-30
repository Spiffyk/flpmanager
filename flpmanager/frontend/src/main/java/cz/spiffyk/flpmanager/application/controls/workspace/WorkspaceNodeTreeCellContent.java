package cz.spiffyk.flpmanager.application.controls.workspace;

import cz.spiffyk.flpmanager.data.WorkspaceNode;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import lombok.Getter;

public abstract class WorkspaceNodeTreeCellContent<N extends WorkspaceNode> extends HBox {
	
	@Getter private final Label label;
	@Getter private final HBox buttonBox;
	@Getter private final HBox leftBox;
	@Getter private final HBox rightBox;
	
	private final N node;
	
	public WorkspaceNodeTreeCellContent(final N node) {
		super();
		getStyleClass().add("workspace-node-cell");
		this.node = node;
		
		this.leftBox = new HBox();
		this.label = new Label();
		this.rightBox = new HBox();
		this.buttonBox = new HBox();
		
		final Region separatorRegion = new Region();
		HBox.setHgrow(separatorRegion, Priority.ALWAYS);
		
		this.getChildren().addAll(this.leftBox, this.label, this.rightBox, separatorRegion, this.buttonBox);
	}
	
	public final N getNode() {
		return node;
	}
}

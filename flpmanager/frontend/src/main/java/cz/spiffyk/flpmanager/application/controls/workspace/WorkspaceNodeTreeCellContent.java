package cz.spiffyk.flpmanager.application.controls.workspace;

import cz.spiffyk.flpmanager.data.WorkspaceNode;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import lombok.Getter;

/**
 * An abstract class for contents representing {@link WorkspaceNode}s
 * @author spiffyk
 *
 * @param <N> A {@link WorkspaceNode} subtype to represent
 */
public abstract class WorkspaceNodeTreeCellContent<N extends WorkspaceNode> extends HBox {
	
	/**
	 * The label containing the name
	 */
	@Getter private final Label label;
	
	/**
	 * A container for buttons (on the far right of the cell)
	 */
	@Getter private final HBox buttonBox;
	
	/**
	 * A container for controls before the label (e.g. for a checkbox)
	 */
	@Getter private final HBox leftBox;
	
	/**
	 * A container after the label (e.g. for tags)
	 */
	@Getter private final HBox rightBox;
	
	/**
	 * The {@link WorkspaceNode} to represent by the content
	 */
	@Getter private final N node;
	
	
	
	/**
	 * Creates a new content representing the specified {@link WorkspaceNode}
	 * @param node The {@link WorkspaceNode} to represent
	 */
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
}

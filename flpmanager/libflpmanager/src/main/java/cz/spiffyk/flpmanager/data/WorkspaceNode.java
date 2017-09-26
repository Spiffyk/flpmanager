package cz.spiffyk.flpmanager.data;

/**
 * A common interface for {@code Song}s and {@code Project}s for use with JavaFX
 * @author spiffyk
 */
public interface WorkspaceNode {
	
	/**
	 * Gets the type of the workspace node
	 * @return {@link WorkspaceNodeType} value
	 */
	public WorkspaceNodeType getType();
}

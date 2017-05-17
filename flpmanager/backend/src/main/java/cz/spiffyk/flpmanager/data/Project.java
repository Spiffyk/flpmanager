package cz.spiffyk.flpmanager.data;

import java.util.Observable;
import java.util.UUID;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class Project extends Observable implements WorkspaceNode {
	@Getter @Setter @NonNull private String name;
	@Getter @Setter @NonNull private UUID identifier;
	
	@Override
	public WorkspaceNodeType getType() {
		return WorkspaceNodeType.PROJECT;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}

package cz.spiffyk.flpmanager.data;

import java.util.Observable;
import java.util.UUID;

public class Project extends Observable implements WorkspaceNode {
	private String name;
	private UUID identifier;
	
	public void setName(final String name) {
		if (name == null) {
			this.name = "";
		} else {
			this.name = name;
		}
		
		this.setChanged();
		this.notifyObservers();
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setIdentifier(final UUID identifier) {
		this.identifier = identifier;
		this.setChanged();
		this.notifyObservers();
	}
	
	public void setIdentifier(final String identifier) {
		this.setIdentifier(UUID.fromString(identifier));
	}
	
	public UUID getIdentifier() {
		return this.identifier;
	}
	
	@Override
	public WorkspaceNodeType getType() {
		return WorkspaceNodeType.PROJECT;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}

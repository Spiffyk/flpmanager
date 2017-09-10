package cz.spiffyk.flpmanager;

public class WorkspaceInUseException extends RuntimeException {

	public WorkspaceInUseException() {
		super();
	}

	public WorkspaceInUseException(String message) {
		super(message);
	}

	public WorkspaceInUseException(Throwable cause) {
		super(cause);
	}

	public WorkspaceInUseException(String message, Throwable cause) {
		super(message, cause);
	}
}

package cz.spiffyk.flpmanager;

/**
 * A {@link RuntimeException} thrown when the workspace file is malformed.
 * @author spiffyk
 */
public class ManagerFileException extends RuntimeException {

	public ManagerFileException() {
		super();
	}
	
	public ManagerFileException(String message) {
		super(message);
	}
	
	public ManagerFileException(Throwable cause) {
		super(cause);
	}
	
	public ManagerFileException(String message, Throwable cause) {
		super(message, cause);
	}
}

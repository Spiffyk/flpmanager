package cz.spiffyk.flpmanager.data;

public enum MetadataType {
	SONG_NAME("songname", "Name", Integer.MIN_VALUE),
	AUTHOR("author", "Author", Integer.MIN_VALUE + 1);
	
	public final String id;
	public final String readableName;
	public final int index;
	
	private MetadataType(String id, String readableName, int index) {
		this.id = id;
		this.readableName = readableName;
		this.index = index;
	}
	
}

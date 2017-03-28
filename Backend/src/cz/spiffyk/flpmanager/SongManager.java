package cz.spiffyk.flpmanager;

import java.util.ArrayList;
import java.util.List;

import cz.spiffyk.flpmanager.data.Song;

public class SongManager {
	private List<Song> songs = new ArrayList<>();
	
	public List<Song> getSongs() {
		return songs;
	}
}

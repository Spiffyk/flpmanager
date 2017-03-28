package cz.spiffyk.flpmanager.application;

import java.util.List;

import cz.spiffyk.flpmanager.SongManager;
import cz.spiffyk.flpmanager.application.screens.main.MainScreen;
import cz.spiffyk.flpmanager.data.Song;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class Main extends Application {
	
	private SongManager getSampleSongManager() {
		final SongManager songManager = new SongManager();
		final List<Song> songs = songManager.getSongs();
		final Song s1 = new Song();
		s1.setName("Asdf");
		s1.setAuthor("Foo");
		songs.add(s1);
		final Song s2 = new Song();
		s2.setName("Ghjk");
		s2.setAuthor("Foo");
		s2.setFavorite(true);
		songs.add(s2);
		final Song s3 = new Song();
		s3.setName("BB (Foo remix)");
		s3.setAuthor("Bar");
		s3.setFavorite(true);
		songs.add(s3);
		return songManager;
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			MainScreen mainScreen = new MainScreen();
			Scene scene = new Scene(mainScreen);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			scene.getStylesheets().add(getClass().getResource("controls/controls.css").toExternalForm());
			
			final SongManager songManager = getSampleSongManager();
			mainScreen.setSongManager(songManager);
			
			
			primaryStage.setScene(scene);
			primaryStage.setTitle("FLP Manager");
			primaryStage.setOnCloseRequest((e) -> {
				Platform.exit();
			});
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

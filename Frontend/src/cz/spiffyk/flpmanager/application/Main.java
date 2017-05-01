package cz.spiffyk.flpmanager.application;

import java.io.IOException;
import java.util.List;

import cz.spiffyk.flpmanager.ManagerFileHandler;
import cz.spiffyk.flpmanager.application.screens.main.MainScreen;
import cz.spiffyk.flpmanager.data.Song;
import cz.spiffyk.flpmanager.data.Workspace;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class Main extends Application {
	
	private Workspace getSampleWorkspace() {
		final Workspace workspace = new Workspace("test_workspace");
		final List<Song> songs = workspace.getSongs();
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
		return workspace;
	}
	
	@Override
	public void start(Stage primaryStage) {
		MainScreen mainScreen = new MainScreen();
		Scene scene = new Scene(mainScreen, 640, 600);
		
		List<String> stylesheets = scene.getStylesheets();
		stylesheets.add(getClass().getResource("application.css").toExternalForm());
		stylesheets.add(getClass().getResource("controls/controls.css").toExternalForm());
		
//		final Workspace workspace = getSampleWorkspace();
		try {
			final Workspace workspace = ManagerFileHandler.loadWorkspace("test_workspace");
			mainScreen.setWorkspace(workspace);
		
			primaryStage.setScene(scene);
			primaryStage.setTitle("FLP Manager");
			primaryStage.setOnCloseRequest((e) -> {
				ManagerFileHandler.saveWorkspace(workspace);
				Platform.exit();
			});
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
			Platform.exit();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

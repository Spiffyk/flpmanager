package cz.spiffyk.flpmanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cz.spiffyk.flpmanager.data.Project;
import cz.spiffyk.flpmanager.data.Song;
import cz.spiffyk.flpmanager.data.Tag;
import cz.spiffyk.flpmanager.data.Workspace;
import javafx.scene.paint.Color;

import org.w3c.dom.Node;

public class ManagerFileHandler {
	
	private static final String WORKSPACE_FILENAME = "workspace.xml";
	
	private static final String WORKSPACE_TAGNAME = "workspace";
	private static final String TAGS_TAGNAME = "tags";
	private static final String TAG_TAGNAME = "tag";
	private static final String SONGS_TAGNAME = "songs";
	private static final String SONG_TAGNAME = "song";
	private static final String PROJECTS_TAGNAME = "projects";
	private static final String PROJECT_TAGNAME = "project";
	
	private static final String NAME_ATTRNAME = "name";
	private static final String AUTHOR_ATTRNAME = "author";
	private static final String FAVORITE_ATTRNAME = "favorite";
	private static final String UUID_ATTRNAME = "uuid";
	private static final String COLOR_ATTRNAME = "color";
	
	/**
	 * The most recent file version
	 */
	private static final String VERSION = "1";
	
	/**
	 * Loads a workspace from directory
	 * @param directory The directory the workspace is saved in
	 * @return Loaded workspace or a new empty workspace if none is loaded
	 * @throws IOException 
	 */
	public static Workspace loadWorkspace(File directory) throws IOException {
		if (directory.exists() && !directory.isDirectory()) {
			throw new IllegalArgumentException("Path exists and is not a directory");
		}
		
		final Workspace workspace = new Workspace(directory);
		final File workspaceFile = new File(directory, WORKSPACE_FILENAME);
		
		if (workspaceFile.exists()) {
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(workspaceFile);
				
				doc.getDocumentElement().normalize();
				String rootName = doc.getDocumentElement().getNodeName().toLowerCase();
				if (!rootName.equals("workspace")) {
					throw new ManagerFileException("Not a valid workspace file");
				}
				
				Element root = doc.getDocumentElement();
				loadWorkspace(workspace, root);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				throw new ManagerFileException("Not a valid XML file");
			}
		}
		
		return workspace;
	}
	
	/**
	 * Loads a workspace from directory
	 * @param path The path to the directory the workspace is saved in
	 * @return Loaded workspace or a new workspace if none is loaded
	 * @throws IOException 
	 */
	public static Workspace loadWorkspace(String path) throws IOException {
		return loadWorkspace(new File(path));
	}
	
	/**
	 * Loads workspace data from a {@code <workspace>} element and puts it into the given {@link Workspace}
	 * @param workspace The {@link Workspace} to load the data into
	 * @param doc The {@link Document} to load the data from
	 */
	private static void loadWorkspace(Workspace workspace, Element root) {
		if (!root.getTagName().toLowerCase().equals(WORKSPACE_TAGNAME)) {
			throw new ManagerFileException("Not tagged as a workspace; " + root.toString());
		}
		
		String version = root.getAttribute("version").trim();
		if (!version.equals(VERSION)) {
			throw new ManagerFileException("The workspace is not of a supported version");
		}
		
		boolean hadTags = false;
		
		final NodeList nodeList = root.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node node = nodeList.item(i);
			if (node instanceof Element) {
				final Element e = (Element) node;
				switch(e.getTagName().toLowerCase()) {
					case TAGS_TAGNAME:
						workspace.addTags(loadTags(e));
						hadTags = true;
						break;
					case SONGS_TAGNAME:
						if (!hadTags) {
							throw new ManagerFileException("Tags must precede Songs");
						}
						workspace.getSongs().addAll(loadSongs(e, workspace));
						break;
				}
			}
		}
	}
	
	/**
	 * Loads {@code Tag}s from {@code <tags>} element for adding into a workspace
	 * @param root The {@code <tags>} element
	 * @return List of loaded {@link Tag}s (may be empty)
	 */
	private static List<Tag> loadTags(Element root) {
		if (!root.getTagName().toLowerCase().equals(TAGS_TAGNAME)) {
			throw new ManagerFileException("Not tagged as a list of tags; "  + root.toString());
		}
		
		final List<Tag> tags = new ArrayList<>();
		
		final NodeList nodeList = root.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node node = nodeList.item(i);
			if (node instanceof Element) {
				final Element e = (Element) node;
				if (e.getTagName().toLowerCase().equals(TAG_TAGNAME)) {
					tags.add(loadTag(e));
				} else {
					throw new ManagerFileException("The tag <" + TAGS_TAGNAME + "> should only contain a list of <" + TAG_TAGNAME + ">.");
				}
			}
		}
		
		return tags;
	}
	
	/**
	 * Loads {@link Tag} from {@code <tag>} element for adding into a workspace
	 * @param root The {@code <tag>} element
	 * @return The loaded {@link Tag}
	 */
	private static Tag loadTag(Element root) {
		if (!root.getTagName().toLowerCase().equals(TAG_TAGNAME)) {
			throw new ManagerFileException("Not tagged as a tag; "  + root.toString());
		}
		
		return new Tag(root.getAttribute(NAME_ATTRNAME), Color.web(root.getAttribute(COLOR_ATTRNAME)));
	}
	
	/**
	 * Loads {@link Song}s from {@code <songs>} element
	 * @param root The {@code <songs>} element
	 * @param workspace The {@link Workspace} to search for tags in
	 * @return List of loaded {@link Song}s (may be empty)
	 */
	private static List<Song> loadSongs(Element root, Workspace workspace) {
		if (!root.getTagName().toLowerCase().equals(SONGS_TAGNAME)) {
			throw new ManagerFileException("Not tagged as a list of songs; "  + root.toString());
		}
		
		final List<Song> songs = new ArrayList<>();
		
		final NodeList nodeList = root.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node node = nodeList.item(i);
			if (node instanceof Element) {
				final Element e = (Element) node;
				if (e.getTagName().toLowerCase().equals(SONG_TAGNAME)) {
					songs.add(loadSong(e, workspace));
				} else {
					throw new ManagerFileException("The tag <" + SONGS_TAGNAME + "> should only contain a list of <" + SONG_TAGNAME + ">.");
				}
			}
		}
		
		return songs;
	}
	
	/**
	 * Loads {@link Song} from {@code <song>} element for adding into a workspace
	 * @param root The {@code <song>} element
	 * @param workspace The {@link Workspace} to search for tags in
	 * @return The loaded {@link Song}
	 */
	private static Song loadSong(Element root, Workspace workspace) {
		if (!root.getTagName().toLowerCase().equals(SONG_TAGNAME)) {
			throw new ManagerFileException("Not tagged as a song; "  + root.toString());
		}
		
		final Song song = new Song();
		
		song.setName(root.getAttribute(NAME_ATTRNAME));
		song.setAuthor(root.getAttribute(AUTHOR_ATTRNAME));
		String favoriteAttribute = root.getAttribute(FAVORITE_ATTRNAME);
		if (!favoriteAttribute.isEmpty()) {
			song.setFavorite(Boolean.parseBoolean(favoriteAttribute));
		}
		
		final NodeList nodeList = root.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node node = nodeList.item(i);
			if (node instanceof Element) {
				final Element e = (Element) node;
				switch(e.getTagName().toLowerCase()) {
					case PROJECTS_TAGNAME:
						song.getProjects().addAll(loadProjects(e));
						break;
					case TAGS_TAGNAME:
						song.getTags().addAll(linkTags(e, workspace));
						break;
				}
			}
		}
		
		return song;
	}
	
	private static List<Project> loadProjects(Element root) {
		if (!root.getTagName().toLowerCase().equals(PROJECTS_TAGNAME)) {
			throw new ManagerFileException("Not tagged as a list of projects; " + root.toString());
		}
		
		final List<Project> projects = new ArrayList<>();
		
		final NodeList nodeList = root.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node node = nodeList.item(i);
			if (node instanceof Element) {
				final Element e = (Element) node;
				if (e.getTagName().toLowerCase().equals(PROJECT_TAGNAME)) {
						projects.add(loadProject(e));
				} else {
					throw new ManagerFileException("The tag <" + PROJECTS_TAGNAME + "> should only contain a list of <" + PROJECT_TAGNAME + ">.");
				}
			}
		}
		
		return projects;
	}
	
	private static Project loadProject(Element root) {
		if (!root.getTagName().toLowerCase().equals(PROJECT_TAGNAME)) {
			throw new ManagerFileException("Not tagged as a project; " + root.toString());
		}
		
		final Project project = new Project();
		project.setName(root.getAttribute(NAME_ATTRNAME));
		project.setIdentifier(root.getAttribute(UUID_ATTRNAME));
		
		return project;
	}
	
	private static List<Tag> linkTags(Element root, Workspace workspace) {
		if (!root.getTagName().toLowerCase().equals(TAGS_TAGNAME)) {
			throw new ManagerFileException("Not tagged as a list of tags; "  + root.toString());
		}
		
		final List<Tag> tags = new ArrayList<>();
		
		final NodeList nodeList = root.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node node = nodeList.item(i);
			if (node instanceof Element) {
				final Element e = (Element) node;
				if (e.getTagName().toLowerCase().equals(TAG_TAGNAME)) {
					tags.add(linkTag(e, workspace));
				} else {
					throw new ManagerFileException("The tag <" + TAGS_TAGNAME + "> should only contain a list of <" + TAG_TAGNAME + ">.");
				}
			}
		}
		
		return tags;
	}
	
	private static Tag linkTag(Element root, Workspace workspace) {
		if (!root.getTagName().toLowerCase().equals(TAG_TAGNAME)) {
			throw new ManagerFileException("Not tagged as a tag; "  + root.toString());
		}
		
		return workspace.getTags().get(root.getTextContent());
	}
	
	/**
	 * Saves the workspace into its directory
	 * @param workspace The workspace to save
	 */
	public static void saveWorkspace(Workspace workspace) {
//		final File directory = workspace.getDirectory();
//		if (directory.exists() && !directory.isDirectory()) {
//			throw new IllegalArgumentException("Path exists but is not a directory");
//		}
//		
//		directory.mkdirs();
//		final File workspaceFile = new File(directory, WORKSPACE_FILENAME);
//		
//		if (directory.listFiles().length > 0 && !workspaceFile.exists()) {
//			throw new IllegalArgumentException("Directory is not empty and is not a workspace");
//		}
//		
//		try {
//			final ObjectOutputStream w = new ObjectOutputStream(new FileOutputStream(workspaceFile));
//			w.write(MAGIC_NUMBER);
//			w.writeByte(VERSION);
//			
//			w.writeByte(SONG_EVENT);
//			w.writeInt(workspace.getSongs().size());
//			for (Song s : workspace.getSongs()) {
//				w.writeObject(s);
//			}
//			
//			w.writeByte(TAG_EVENT);
//			w.writeInt(workspace.getTags().size());
//			for (Tag t : workspace.getTags()) {
//				w.writeObject(t);
//			}
//			
//			w.writeByte(END_EVENT);
//			
//			w.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
}

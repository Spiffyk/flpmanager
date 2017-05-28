package cz.spiffyk.flpmanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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

/**
 * A library class for loading workspaces from directories
 * @author spiffyk
 */
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
	 * This is not the constructor you are looking for, move along
	 */
	private ManagerFileHandler() {};
	
	
	
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
	 * Loads a workspace from directory. If a workspace does not exist, initializes it.
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
				if (!rootName.equals(WORKSPACE_TAGNAME)) {
					throw new ManagerFileException("Not a valid workspace file");
				}
				
				Element root = doc.getDocumentElement();
				loadWorkspace(workspace, root);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				throw new ManagerFileException("Not a valid XML file");
			}
		} else {
			// Initialize the workspace if none exists
			saveWorkspace(workspace);
		}
		
		return workspace;
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
		
		final Song song = new Song(UUID.fromString(root.getAttribute(UUID_ATTRNAME)), workspace);
		song.setName(root.getAttribute(NAME_ATTRNAME));
		song.setAuthor(root.getAttribute(AUTHOR_ATTRNAME));
		song.updateFiles();
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
						song.getProjects().addAll(loadProjects(e, song));
						break;
					case TAGS_TAGNAME:
						song.getTags().addAll(linkTags(e, workspace));
						break;
				}
			}
		}
		
		return song;
	}
	
	/**
	 * Loads {@link Project}s from {@code <projects>} element
	 * @param root The {@code <projects>} element
	 * @param parent The {@link Song} to be set as a parent
	 * @return List of loaded {@link Project}s (may be empty)
	 */
	private static List<Project> loadProjects(Element root, Song parent) {
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
						projects.add(loadProject(e, parent));
				} else {
					throw new ManagerFileException("The tag <" + PROJECTS_TAGNAME + "> should only contain a list of <" + PROJECT_TAGNAME + ">.");
				}
			}
		}
		
		return projects;
	}
	
	/**
	 * Loads {@link Project} from {@code <project>} element for adding into a song
	 * @param root The {@code <project>} element
	 * @param parent The {@link Song} to be set as a parent
	 * @return The loaded {@link Project}
	 */
	private static Project loadProject(Element root, Song parent) {
		if (!root.getTagName().toLowerCase().equals(PROJECT_TAGNAME)) {
			throw new ManagerFileException("Not tagged as a project; " + root.toString());
		}
		
		final Project project = new Project(UUID.fromString(root.getAttribute(UUID_ATTRNAME)), parent);
		project.setName(root.getAttribute(NAME_ATTRNAME));
		
		return project;
	}
	
	/**
	 * Links {@link Tag}s found in the workspace
	 * @param root The {@code <tags>} element
	 * @param workspace The {@link Workspace} to look for the tags in
	 * @return A list of {@link Tag}s (may be empty)
	 */
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
					Tag tag = linkTag(e, workspace);
					if (tag != null) {
						tags.add(tag);
					}
				} else {
					throw new ManagerFileException("The tag <" + TAGS_TAGNAME + "> should only contain a list of <" + TAG_TAGNAME + ">.");
				}
			}
		}
		
		return tags;
	}
	
	/**
	 * Finds a tag in the {@link Workspace}
	 * @param root The {@code <tag>} element
	 * @param workspace The {@link Workspace} to look for the tag in
	 * @return The {@link Tag} found in the workspace or {@code null} if none found
	 */
	private static Tag linkTag(Element root, Workspace workspace) {
		if (!root.getTagName().toLowerCase().equals(TAG_TAGNAME)) {
			throw new ManagerFileException("Not tagged as a tag; "  + root.toString());
		}
		
		return workspace.getTags().get(root.getTextContent().toLowerCase());
	}
	
	
	
	/**
	 * Saves the workspace into its directory
	 * @param workspace The workspace to save
	 */
	public static void saveWorkspace(Workspace workspace) {
		
		if (!workspace.getDirectory().exists()) {
			workspace.getDirectory().mkdirs();
		}
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			
			Element root = doc.createElement(WORKSPACE_TAGNAME);
			root.setAttribute("version", VERSION);
			
			root.appendChild(saveTags(workspace.getTags().values(), doc));
			root.appendChild(saveSongs(workspace.getSongs(), doc));
			
			doc.appendChild(root);
			
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); // y u no have constant fo dat???
			
			Result result = new StreamResult(new File(workspace.getDirectory(), WORKSPACE_FILENAME));
			Source source = new DOMSource(doc);
			transformer.transform(source, result);
		} catch (ParserConfigurationException e) {
			// TODO do something with these
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Saves {@link Tag}s into a {@code <tags>} element
	 * @param tags The tags to save
	 * @param doc The DOM document
	 * @return The {@code <tags>} element
	 */
	private static Element saveTags(Iterable<Tag> tags, Document doc) {
		Element root = doc.createElement(TAGS_TAGNAME);
		
		for (Tag tag : tags) {
			root.appendChild(saveTag(tag, doc));
		}
		
		return root;
	}
	
	/**
	 * Saves a {@link Tag} into a {@code <tag>} element
	 * @param tag The tag to save
	 * @param doc The DOM document
	 * @return The {@code <tag>} element
	 */
	private static Element saveTag(Tag tag, Document doc) {
		Element root = doc.createElement(TAG_TAGNAME);
		root.setAttribute(NAME_ATTRNAME, tag.getName());
		root.setAttribute(COLOR_ATTRNAME, "#" + Integer.toHexString(tag.getColor().hashCode()));
		return root;
	}
	
	
	
	/**
	 * Saves {@link Song}s into a {@code <songs>} element
	 * @param songs The songs to save
	 * @param doc The DOM document
	 * @return The {@code <songs>} element
	 */
	private static Element saveSongs(Iterable<Song> songs, Document doc) {
		Element root = doc.createElement(SONGS_TAGNAME);
		for (Song song : songs) {
			root.appendChild(saveSong(song, doc));
		}
		return root;
	}
	
	/**
	 * Saves {@link Song} into a {@code <song>} element
	 * @param song The song to save
	 * @param doc The DOM document
	 * @return The {@code <song>} element
	 */
	private static Element saveSong(Song song, Document doc) {
		Element root = doc.createElement(SONG_TAGNAME);
		root.setAttribute(NAME_ATTRNAME, song.getName());
		root.setAttribute(AUTHOR_ATTRNAME, song.getAuthor());
		root.setAttribute(UUID_ATTRNAME, song.getIdentifier().toString());
		
		if (song.isFavorite()) {
			root.setAttribute(FAVORITE_ATTRNAME, "true");
		}
		
		root.appendChild(saveProjects(song.getProjects(), doc));
		root.appendChild(saveLinkedTags(song.getTags(), doc));
		
		return root;
	}
	
	
	
	/**
	 * Saves {@link Project}s into a {@code <projects>} element
	 * @param projects The projects to save
	 * @param doc The DOM document
	 * @return The {@code <projects>} element
	 */
	private static Element saveProjects(Iterable<Project> projects, Document doc) {
		Element root = doc.createElement(PROJECTS_TAGNAME);
		for (Project project : projects) {
			root.appendChild(saveProject(project, doc));
		}
		return root;
	}
	
	/**
	 * Saves {@link Project} into a {@code <project>} element
	 * @param project The project to save
	 * @param doc The DOM document
	 * @return The {@code <project>} element
	 */
	private static Element saveProject(Project project, Document doc) {
		Element root = doc.createElement(PROJECT_TAGNAME);
		root.setAttribute(NAME_ATTRNAME, project.getName());
		root.setAttribute(UUID_ATTRNAME, project.getIdentifier().toString());
		return root;
	}
	
	
	
	/**
	 * Saves linked {@link Tag}s into a {@code <tags>} element
	 * @param tags The tags to save
	 * @param doc The DOM document
	 * @return The {@code <tags>} element
	 */
	private static Element saveLinkedTags(Iterable<Tag> tags, Document doc) {
		Element root = doc.createElement(TAGS_TAGNAME);
		for (Tag tag : tags) {
			root.appendChild(saveLinkedTag(tag, doc));
		}
		return root;
	}
	
	/**
	 * Saves linked {@link Tag} into a {@code <tag>} element
	 * @param tag The tag to save
	 * @param doc The DOM document
	 * @return The {@code <tag>} element
	 */
	private static Element saveLinkedTag(Tag tag, Document doc) {
		Element root = doc.createElement(TAG_TAGNAME);
		root.setTextContent(tag.getName());
		return root;
	}
}

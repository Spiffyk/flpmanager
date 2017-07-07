package cz.spiffyk.flpmanager;

import java.io.File;
import java.io.IOException;

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
import org.xml.sax.SAXException;

import cz.spiffyk.flpmanager.data.Workspace;

/**
 * A library class for loading manager files
 * @author spiffyk
 */
public class ManagerFileHandler {
	
	/**
	 * The filename used for the Workspace XML file
	 */
	public static final String WORKSPACE_FILENAME = "workspace.xml";
	
	/**
	 * Attribute name for node name
	 */
	public static final String NAME_ATTRNAME = "name";
	
	/**
	 * Attribute name for node author
	 */
	public static final String AUTHOR_ATTRNAME = "author";
	
	/**
	 * Attribute name for filename
	 */
	public static final String FILENAME_ATTRNAME = "filename";
	
	/**
	 * Attribute name for node favorite mark
	 */
	public static final String FAVORITE_ATTRNAME = "favorite";
	
	/**
	 * Attribute name for node universally-unique-identifier
	 */
	public static final String UUID_ATTRNAME = "uuid";
	
	/**
	 * Attribute name for node color
	 */
	public static final String COLOR_ATTRNAME = "color";
	
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
		
		final Workspace workspace;
		final File workspaceFile = new File(directory, WORKSPACE_FILENAME);
		
		if (workspaceFile.exists()) {
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(workspaceFile);
				
				doc.getDocumentElement().normalize();
				String rootName = doc.getDocumentElement().getNodeName().toLowerCase();
				if (!rootName.equals(Workspace.WORKSPACE_TAGNAME)) {
					throw new ManagerFileException("Not a valid workspace file");
				}
				
				Element root = doc.getDocumentElement();
				String version = root.getAttribute("version").trim();
				if (!version.equals(VERSION)) {
					throw new ManagerFileException("The workspace is not of a supported version");
				}
				
				workspace = Workspace.fromElement(root, directory);
			} catch (ParserConfigurationException e) {
				throw new ManagerFileException("Wrong parser configuration", e);
			} catch (SAXException e) {
				throw new ManagerFileException("Not a valid XML file", e);
			}
		} else {
			workspace = new Workspace(directory);
			saveWorkspace(workspace);
		}
		
		return workspace;
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
			
			Element element = workspace.toElement(doc);
			element.setAttribute("version", VERSION);
			doc.appendChild(element);
			
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); // y u no have constant fo dat???
			
			Result result = new StreamResult(new File(workspace.getDirectory(), WORKSPACE_FILENAME));
			Source source = new DOMSource(doc);
			transformer.transform(source, result);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
	}
}

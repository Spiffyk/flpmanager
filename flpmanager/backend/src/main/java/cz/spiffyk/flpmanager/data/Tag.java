package cz.spiffyk.flpmanager.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.UUID;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cz.spiffyk.flpmanager.ManagerFileException;
import cz.spiffyk.flpmanager.ManagerFileHandler;
import cz.spiffyk.flpmanager.util.ManagerUtils;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.NonNull;

/**
 * Contains a tag that {@link Song}s can be marked with
 * @author spiffyk
 */
public class Tag extends Observable {

	/**
	 * The name of the XML tag representing a tag
	 */
	public static final String TAG_TAGNAME = "tag";
	
	/**
	 * The name of the XML tag representing a list of tags
	 */
	public static final String TAGS_TAGNAME = "tags";
	
	/**
	 * The unique identifier of the tag
	 */
	@Getter private final UUID identifier;
	
	/**
	 * The parent workspace
	 */
	@Getter private final Workspace parent;
	
	/**
	 * The lower-case tag name
	 */
	@Getter private String name;
	
	/**
	 * The color of the tag
	 */
	@Getter private Color color;
	
	/**
	 * Creates a new tag with the specified name. The name gets trimmed and converted to lower-case
	 * @param name The name to assign to the tag
	 */
	public Tag(@NonNull Workspace parent) {
		this(UUID.randomUUID(), parent);
	}
	
	/**
	 * Creates a new tag with the specified name. The name gets trimmed and converted to lower-case
	 * @param name The name to assign to the tag
	 */
	public Tag(@NonNull UUID identifier, @NonNull Workspace parent) {
		this.identifier = identifier;
		this.parent = parent;
	}
	
	
	
	/**
	 * Creates a tag represented by the specified DOM {@link Element} with the specified {@link Workspace} as the
	 * parent of the tag. The element name must be {@code <tag>}.
	 * @param root The DOM {@link Element} representing the tag
	 * @param parent The parent {@link Workspace}
	 * @return The tag represented by the {@link Element}
	 */
	public static Tag fromElement(@NonNull Element root, @NonNull Workspace parent) {
		if (!root.getTagName().toLowerCase().equals(TAG_TAGNAME)) {
			throw new ManagerFileException("Not tagged as a tag; "  + root.toString());
		}
		
		Tag tag = new Tag(UUID.fromString(root.getAttribute(ManagerFileHandler.UUID_ATTRNAME)), parent);
		tag.setName(root.getAttribute(ManagerFileHandler.NAME_ATTRNAME));
		tag.setColor(Color.web(root.getAttribute(ManagerFileHandler.COLOR_ATTRNAME)));
		
		return tag;
	}
	
	/**
	 * Creates a {@link List} of tags represented by the specified DOM {@link Element} with the specified
	 * {@link Workspace} as the parent of all the tags. The element name must be {@code <songs>}.
	 * @param root The DOM {@link Element} representing the tags
	 * @param parent The parent {@link Workspace}
	 * @return The list of tags represented by the {@link Element}
	 */
	public static List<Tag> listFromElement(@NonNull Element root, @NonNull Workspace parent) {
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
					tags.add(Tag.fromElement(e, parent));
				} else {
					throw new ManagerFileException("The tag <" + TAGS_TAGNAME + "> should only contain a list of <" + TAG_TAGNAME + ">.");
				}
			}
		}
		
		return tags;
	}
	
	/**
	 * Creates a DOM {@link Element} representing the specified {@link List} of tags.
	 * @param songs The {@link List} of tags to represent by the {@link Element}
	 * @param doc The parent DOM {@link Document}
	 * @return The {@link Element} representing the tags
	 */
	public static Element listToElement(@NonNull List<Tag> tags, @NonNull Document doc) {
		Element root = doc.createElement(TAGS_TAGNAME);
		
		for (Tag tag : tags) {
			root.appendChild(tag.toElement(doc));
		}
		
		return root;
	}
	
	
	
	/**
	 * Sets the tag's color.
	 * @param color The color
	 */
	public void setColor(@NonNull Color color) {
		this.color = color;
		this.setChanged();
		this.notifyObservers();
	}
	
	/**
	 * Sets the tag's name
	 * @param name The name
	 */
	public void setName(@NonNull String name) {
		this.name = name.trim().toLowerCase();
		this.setChanged();
		this.notifyObservers();
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
	
	/**
	 * Creates a DOM {@link Element} representing the tag.
	 * @param doc The parent DOM {@link Document}
	 * @return An {@link Element} representing the tag
	 */
	public Element toElement(Document doc) {
		Element root = doc.createElement(TAG_TAGNAME);
		root.setAttribute(ManagerFileHandler.NAME_ATTRNAME, this.getName());
		root.setAttribute(ManagerFileHandler.COLOR_ATTRNAME, ManagerUtils.toRGBCode(this.getColor()));
		root.setAttribute(ManagerFileHandler.UUID_ATTRNAME, this.getIdentifier().toString());
		return root;
	}
	
	/**
	 * Compares {@link Tag}s by their names
	 * @author spiffyk
	 */
	public static class NameComparator implements Comparator<Tag> {
		@Override
		public int compare(Tag o1, Tag o2) {
			return o1.getName().compareTo(o2.getName());
		}
	}
}

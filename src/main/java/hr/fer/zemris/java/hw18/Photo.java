package hr.fer.zemris.java.hw18;

import java.util.List;
import java.util.Objects;

/**
 * This class is an abstract representation of a photo. It serves as a photo
 * descriptor, containing most important information about a photo:
 * <ul>
 * <li>photo name,
 * <li>photo description and
 * <li>photo tags.
 * </ul>
 *
 * @author Mario Bobic
 */
public class Photo {

	/** Name of the photo. */
	private String name;
	
	/** Photo description. */
	private String desc;
	
	/** List of photo tags. */
	private List<String> tags;
	
	/**
	 * Constructs an instance of {@code Photo} with the specified arguments.
	 *
	 * @param name name of the photo
	 * @param desc photo description
	 * @param tags photo tags
	 */
	public Photo(String name, String desc, List<String> tags) {
		this.name = Objects.requireNonNull(name);
		this.desc = Objects.requireNonNull(desc);
		this.tags = Objects.requireNonNull(tags);
	}
	
	/**
	 * Adds a tag to this photo.
	 * 
	 * @param tag tag to be added
	 */
	public void addTag(String tag) {
		tags.add(tag);
	}
	
	/**
	 * Removes the specified <tt>tag</tt> from this photo. Returns true if this
	 * photo contained the specified <tt>tag</tt>.
	 * 
	 * @param tag tag to be removed from this photo
	 * @return true if this photo contained the specified <tt>tag</tt>
	 */
	public boolean removeTag(String tag) {
		return tags.remove(tag);
	}
	
	/**
	 * Returns the name of this photo.
	 *
	 * @return the name of this photo
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the photo description.
	 *
	 * @return the photo description
	 */
	public String getDesc() {
		return desc;
	}
	
	/**
	 * Returns a list of photo tags.
	 *
	 * @return a list of photo tags
	 */
	public List<String> getTags() {
		return tags;
	}
	
}

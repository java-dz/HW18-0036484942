package hr.fer.zemris.java.hw18;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

/**
 * The photo database class offers methods for loading and manipulating photo
 * descriptors, as well as the photos these descriptors describe. Since photos
 * are located inside the <tt>WEB-INF</tt> directory, client can't access them
 * directly so this class provides bridge-methods for generating instances of
 * the {@code BufferedImage} class.
 *
 * @author Mario Bobic
 */
public class PhotosDB {
	
	/** Path to the photo descriptor file. */
	private static final Path DESCRIPTOR_PATH =
			Paths.get("src", "main", "webapp", "WEB-INF", "opisnik.txt");
	
	/** Path to directory containing photos. */
	private static final Path PHOTOS_PATH =
			Paths.get("src", "main", "webapp", "WEB-INF", "slike");
	
	/** Path to directory containing thumbnails. */
	private static final Path THUMBS_PATH =
			Paths.get("src", "main", "webapp", "WEB-INF", "thumbnails");
	
	/** Standard thumbnail image size. */
	private static final int THUMB_SIZE = 150;
	
	/** List of photo descriptors. */
	private static final List<Photo> photos = loadPhotos();

	/**
	 * Loads all photo descriptors into a list and returns it.
	 * 
	 * @return photo descriptors loaded from the descriptor file
	 */
	private static List<Photo> loadPhotos() {
		List<Photo> photos = new ArrayList<>();
		
		List<String> lines;
		try {
			lines = Files.readAllLines(DESCRIPTOR_PATH);
		} catch (IOException e) {
			throw new InternalError("Can not load photo descriptor.");
		}
		
		String name = null;
		String desc = null;
		List<String> tags = null;
		int counter = 0;
		for (int i = 0, n = lines.size(); i < n; i++) {
			String line = lines.get(i).trim();
			if (line.isEmpty()) continue;
			
			switch (counter % 3) {
			case 0: name = line; break;
			case 1: desc = line; break;
			case 2: tags = parseTags(line);
			}
			
			counter++;
			if (counter % 3 == 0 && counter != 0) {
				photos.add(new Photo(name, desc, tags));
			}
		}
		
		return photos;
	}
	
	/**
	 * Parses the specified <tt>line</tt> parameter as tags and returns a list
	 * of these tags.
	 * <p>
	 * Tags are expected to be separated by a comma symbol (<tt>,</tt>).
	 *
	 * @param line line to be parsed
	 * @return a list of parsed tags
	 */
	private static List<String> parseTags(String line) {
		List<String> list = new ArrayList<>();
		
		String[] tags = line.split(",");
		for (String tag : tags) {
			tag = tag.trim().toLowerCase();
			if (tag.isEmpty()) continue;
			list.add(tag);
		}
		
		return list;
	}
	
	/**
	 * Returns the number of photos in database.
	 * 
	 * @return the number of photos in database
	 */
	public static int getNumberOfPhotos() {
		return photos.size();
	}
	
	/**
	 * Returns a {@code Photo} at the specified <tt>index</tt>.
	 * 
	 * @param index index of the photo to be returned
	 * @return a photo descriptor at the specified index.
	 */
	public static Photo getPhoto(int index) {
		return photos.get(index);
	}
	
	/**
	 * Returns a {@code Photo} with the specified <tt>name</tt>.
	 * 
	 * @param name name of the photo to be returned
	 * @return a photo descriptor with the specified name
	 */
	public static Photo getPhoto(String name) {
		for (Photo photo : photos) {
			if (photo.getName().equals(name)) {
				return photo;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns all tags contained in all photo descriptors in database.
	 * Duplicates are removed, leaving only unique tags.
	 * 
	 * @return all tags from all photo descriptors in database
	 */
	public static List<String> getTags() {
		Set<String> tags = new HashSet<>();
		
		for (Photo photo : photos) {
			tags.addAll(photo.getTags());
		}
		
		return new ArrayList<>(tags);
	}
	
	/**
	 * Returns a list of {@code Photo}s containing the specified <tt>tag</tt>.
	 * 
	 * @param tag tag of a photo
	 * @return a list of photo descriptors containing the specified tag
	 */
	public static List<Photo> getPhotosByTag(String tag) {
		List<Photo> list = new ArrayList<>();
		
		tag = tag.toLowerCase();
		for (Photo photo : photos) {
			if (photo.getTags().contains(tag)) {
				list.add(photo);
			}
		}
		
		return list;
	}
	
	/**
	 * Returns a {@code BufferedImage} by loading an image from disk, specified
	 * by the <tt>photo</tt>.
	 * 
	 * @param photo photo descriptor containing information about image on disk
	 * @return a BufferedImage created from the specified <tt>photo</tt>
	 * @throws IOException if an I/O exception occurs
	 */
	public static BufferedImage getImage(Photo photo) throws IOException  {
		File file = getPhotoFile(photo);
		return ImageIO.read(file);
	}
	
	/**
	 * Returns a {@code BufferedImage} by loading an image from disk, specified
	 * by the <tt>photo</tt>.
	 * <p>
	 * The image is returned as a thumbnail. The process of finding a thumbnail
	 * is as follows:
	 * <ol>
	 * <li>If the thumbnail already exists on disk, that image is read and
	 * returned.
	 * <li>Else the thumbnail is generated as a scaled image from a full-sized
	 * image, and saved in the thumbnails directory.
	 * <li>If the thumbnails directory does not exist, it is created.
	 * </ol>
	 * 
	 * @param photo photo descriptor containing information about image on disk
	 * @return a BufferedImage created from the specified <tt>photo</tt>
	 * @throws IOException if an I/O exception occurs
	 */
	public static BufferedImage getThumbnail(Photo photo) throws IOException {
		File thumb = getThumbFile(photo);
		if (thumb.exists()) {
			return ImageIO.read(thumb);
		} else if (!Files.exists(THUMBS_PATH)) {
			Files.createDirectory(THUMBS_PATH);
		}
		
		BufferedImage img = getImage(photo);
		Image scaled = img.getScaledInstance(THUMB_SIZE, THUMB_SIZE, Image.SCALE_SMOOTH);
		img = getBufferedImage(scaled);
		
		ImageIO.write(img, "jpg", thumb);
		return img;
	}
	
	/**
	 * Returns a {@code File} as an abstract representation of an image
	 * described by the specified <tt>photo</tt>, loaded from the photos path.
	 * 
	 * @param photo photo descriptor specifying the image
	 * @return a File of an image described by the <tt>photo</tt>
	 */
	private static File getPhotoFile(Photo photo) {
		return new File(PHOTOS_PATH.toString(), photo.getName());
	}
	
	/**
	 * Returns a {@code File} as an abstract representation of an image
	 * described by the specified <tt>photo</tt>, loaded from the thumbnails
	 * path.
	 * 
	 * @param photo photo descriptor specifying the image
	 * @return a File of an image described by the <tt>photo</tt>
	 */
	private static File getThumbFile(Photo photo) {
		return new File(THUMBS_PATH.toString(), photo.getName());
	}
	
	/**
	 * Returns a {@code BufferedImage} generated from the specified
	 * {@code Image} <tt>img</tt>. The {@code BufferedImage} is of same width
	 * and height as the specified <tt>img</tt>, with its type set to
	 * {@link BufferedImage#TYPE_3BYTE_BGR}.
	 * 
	 * @param img {@code Image} from which a {@code BufferedImage} is generated
	 * @return a {@code BufferedImage} generated from the specified {@code Image}
	 */
	private static BufferedImage getBufferedImage(Image img) {
		int width = img.getWidth(null);
		int height = img.getHeight(null);

		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics g = bi.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		
		return bi;
	}
	
}

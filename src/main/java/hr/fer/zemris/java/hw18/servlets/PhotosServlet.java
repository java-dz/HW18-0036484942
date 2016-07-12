package hr.fer.zemris.java.hw18.servlets;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import hr.fer.zemris.java.hw18.Photo;
import hr.fer.zemris.java.hw18.PhotosDB;

/**
 * This servlet is used for fetching full-sized photos from the database.
 * <p>
 * The <tt>name</tt> parameter is required for this servlet. If a
 * <tt>showImg</tt> parameter is not present in the list of parameters,
 * <tt>JSON</tt> text is returned as a representation of a {@code Photo} object.
 * Else, if it is present and <tt>true</tt>, the image is fetched using
 * {@link PhotosDB#getImage(Photo)} and written to the response output stream.
 *
 * @author Mario Bobic
 */
@WebServlet(urlPatterns={"/servlets/photos"})
public class PhotosServlet extends HttpServlet {
	/** Serialization UID. */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String name = req.getParameter("name");
		Photo photo = PhotosDB.getPhoto(name);

		boolean showImg = Boolean.parseBoolean(req.getParameter("showImg"));
		if (!showImg) {
			resp.setContentType("application/json;charset=UTF-8");
			
			Gson gson = new Gson();
			String jsonText = gson.toJson(photo);
	
			resp.getWriter().write(jsonText);
			resp.getWriter().flush();
		} else {
			resp.setContentType("image/jpg");
			BufferedImage image = PhotosDB.getImage(photo);
			ImageIO.write(image, "jpg", resp.getOutputStream());
		}
	}
	
}

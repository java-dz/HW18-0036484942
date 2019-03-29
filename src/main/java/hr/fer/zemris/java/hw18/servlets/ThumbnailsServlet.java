package hr.fer.zemris.java.hw18.servlets;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

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
 * This servlet is used for fetching thumbnail photos from the database.
 * <p>
 * There are two parameter keywords used by this servlet; <tt>tag</tt> and
 * <tt>name</tt>.
 * <ol>
 * <li>The <tt>tag</tt> parameter is used for fetching a list of photo
 * descriptors that contain the specified tag, and an array is returned wrapped
 * as <tt>JSON</tt>.
 * <li>The <tt>name</tt> parameter is used for fetching a specific and unique
 * photo descriptor, where the thumbnail image is fetched using
 * {@link PhotosDB#getThumbnail(Photo)} and is written to the response output
 * stream.
 * </ol>
 *
 * @author Mario Bobic
 */
@WebServlet(urlPatterns={"/servlets/thumbs"})
public class ThumbnailsServlet extends HttpServlet {
    /** Serialization UID. */
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String tag = req.getParameter("tag");
        String name = req.getParameter("name");

        if (tag != null) {
            resp.setContentType("application/json;charset=UTF-8");

            List<Photo> list = PhotosDB.getPhotosByTag(tag);
            Photo[] array = new Photo[list.size()];
            list.toArray(array);

            Gson gson = new Gson();
            String jsonText = gson.toJson(array);

            resp.getWriter().write(jsonText);
            resp.getWriter().flush();
        } else if (name != null) {
            resp.setContentType("image/jpg");
            Photo photo = PhotosDB.getPhoto(name);
            BufferedImage thumb = PhotosDB.getThumbnail(photo);
            ImageIO.write(thumb, "jpg", resp.getOutputStream());
        }
    }

}

package hr.fer.zemris.java.hw18.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import hr.fer.zemris.java.hw18.PhotosDB;

/**
 * Returns tags fetched by the {@link PhotosDB#getTags()} method by wrapping it
 * into a <tt>JSON</tt> and writing that object to the response.
 *
 * @author Mario Bobic
 */
@WebServlet(urlPatterns={"/servlets/tags"})
public class ReturnTagsServlet extends HttpServlet {
    /** Serialization UID. */
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<String> list = PhotosDB.getTags();

        String[] array = new String[list.size()];
        list.toArray(array);

        resp.setContentType("application/json;charset=UTF-8");

        Gson gson = new Gson();
        String jsonText = gson.toJson(array);

        resp.getWriter().write(jsonText);
        resp.getWriter().flush();
    }

}

package org.example.handler;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.http.Part;
import org.example.Controller;
import org.example.data.GsonData;
import org.example.data.ResponseGson;
import org.example.exception.InvalidFieldException;
import org.example.utils.MySQLConnection;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

import static spark.Spark.halt;

public class CreateCourseHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp.txt"));
        res.type("application/json");

        try {
            Part filePart = req.raw().getPart("thumbnail");
            Controller.validateAccessToken(req);

            if (req.attribute("role").equals("creator")) {
                throw new InvalidFieldException(403, "Forbidden");
            }

            Controller.validateParams(req, "title", "description");

            if (req.queryParams("title").isEmpty()) {
                throw new InvalidFieldException(400, "Title is required");
            }

            if (req.queryParams("title").length() < 2 || req.queryParams("title").length() > 30) {
                throw new InvalidFieldException(400, "Title must be between 2 and 30 characters long");
            }

            if (req.queryParams("description").isEmpty()) {
                throw new InvalidFieldException(400, "Description is required");
            }

            if (req.queryParams("description").length() < 2 || req.queryParams("description").length() > 255) {
                throw new InvalidFieldException(400, "Description must be between 2 and 255 characters long");
            }

            if (filePart == null) {
                throw new InvalidFieldException(400, "Thumbnail is required");
            }

            String fileName = filePart.getSubmittedFileName();
            String extension = fileName.substring(fileName.lastIndexOf("."));
            String path = "/images/course-thumbnail/" + UUID.randomUUID() + extension;
            Path dir = Path.of(System.getenv("SERVER_RESOURCE_PATH"), path);

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO tblcourse (author, title, description, thumbnail) VALUES (?, ?, ?, ?)")) {
                stmt.setInt(1, Integer.parseInt(req.attribute("userId")));
                stmt.setString(2, req.queryParams("title"));
                stmt.setString(3, req.queryParams("description"));
                stmt.setString(4, req.host() + path);
                stmt.executeUpdate();

                Files.createDirectories(dir.getParent());
                try (InputStream input = filePart.getInputStream()) {
                    Files.copy(input, dir, StandardCopyOption.REPLACE_EXISTING);
                }

                res.status(201);
                return GsonData.objectToJson(new ResponseGson<>(true, "Course created successfully"));
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(new ResponseGson<>(false, e.getMessage())));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(new ResponseGson<>(false, "Something went wrong in the server")));
        }
        return null;
    }
}

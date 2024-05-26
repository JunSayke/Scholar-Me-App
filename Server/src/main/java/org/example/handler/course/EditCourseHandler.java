package org.example.handler.course;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.http.Part;
import org.example.Controller;
import org.example.data.GsonData;
import org.example.data.ResponseGson;
import org.example.exception.InvalidFieldException;
import org.example.pattern.behavioural.SQLParamsChainOfResponsibility;
import org.example.pattern.behavioural.SQLParamsString;
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
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static spark.Spark.halt;

public class EditCourseHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp.txt"));
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            if (req.attribute("role").equals("creator")) {
                throw new InvalidFieldException(403, "Forbidden");
            }

            Controller.validateParams(req, "courseId");

            if (req.queryParams("courseId").isEmpty()) {
                throw new InvalidFieldException(400, "Course ID is required");
            }

            if (req.queryParams("courseId").matches("[^0-9]+")) {
                throw new InvalidFieldException(400, "Course ID must be a number");
            }

            List<String> updates = new ArrayList<>();
            List<Object> params = new ArrayList<>();

            if (req.queryParams("title") != null) {
                if (req.queryParams("title").length() < 2 || req.queryParams("title").length() > 30) {
                    throw new InvalidFieldException(400, "Title must be between 2 and 30 characters long");
                }

                updates.add("title = ?");
                params.add(req.queryParams("title"));
            }

            if (req.queryParams("description") != null) {
                if (req.queryParams("description").length() < 2 || req.queryParams("description").length() > 255) {
                    throw new InvalidFieldException(400, "Description must be between 2 and 255 characters long");
                }

                updates.add("description = ?");
                params.add(req.queryParams("description"));
            }

            Part filePart = req.raw().getPart("thumbnail");
            String currThumbnailPath;
            Path dir = null;

            if (updates.isEmpty() && filePart == null) {
                throw new InvalidFieldException(200, "No changes made");
            }

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT thumbnail FROM tblcourse WHERE courseid = ? AND author = ? LIMIT 1")) {
                SQLParamsChainOfResponsibility paramsChain = Controller.getSQLParamsChain();
                paramsChain.handle(stmt, 1, Integer.parseInt(req.queryParams("courseId")));
                paramsChain.handle(stmt, 2, Integer.parseInt(req.attribute("userId")));
                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    throw new InvalidFieldException(404, "Course not found");
                }

                currThumbnailPath = rs.getString("thumbnail");
                if (filePart != null) {
                    String fileName = filePart.getSubmittedFileName();
                    String extension = fileName.substring(fileName.lastIndexOf("."));
                    String path = "/images/course-thumbnail/" + UUID.randomUUID() + extension;
                    updates.add("thumbnail = ?");
                    params.add(req.scheme() + req.host() + path);
                    dir = Path.of(System.getenv("SERVER_RESOURCE_PATH"), path);
                }
            }

            String updateClause = String.join(", ", updates);
            String query = "UPDATE tblcourse SET " + updateClause + " WHERE courseid = ? AND author = ?";

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                SQLParamsChainOfResponsibility paramsChain = Controller.getSQLParamsChain();
                for (int i = 0; i < params.size(); i++) {
                    paramsChain.handle(stmt, i + 1, params.get(i));
                }
                stmt.setInt(params.size() + 1, Integer.parseInt(req.queryParams("courseId")));
                stmt.setInt(params.size() + 2, Integer.parseInt(req.attribute("userId")));

                if (stmt.executeUpdate() == 0) {
                    throw new InvalidFieldException(404, "Course not found");
                }

                if (filePart != null) {
                    if (currThumbnailPath != null) {
                        Path oldFile = Path.of(System.getenv("SERVER_RESOURCE_PATH"), currThumbnailPath.substring(req.scheme().length() + req.host().length()));
                        Files.deleteIfExists(oldFile);
                    }
                    Files.createDirectories(dir.getParent());
                    try (InputStream input = filePart.getInputStream()) {
                        Files.copy(input, dir, StandardCopyOption.REPLACE_EXISTING);
                    }
                }

                return GsonData.objectToJson(new ResponseGson<>(true, "Course updated successfully"));
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(new ResponseGson<>(false, e.getMessage())));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(new ResponseGson<>(false, "Something went wrong in the server")));
        }
        return null;
    }
}

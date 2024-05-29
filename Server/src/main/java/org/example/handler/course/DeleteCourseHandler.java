package org.example.handler.course;

import org.example.Controller;
import org.example.data.GsonData;
import org.example.data.ResponseGson;
import org.example.exception.InvalidFieldException;
import org.example.utils.MySQLConnection;
import spark.Request;
import spark.Response;
import spark.Route;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static spark.Spark.halt;

public class DeleteCourseHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            if (!req.attribute("role").equals("creator")) {
                throw new InvalidFieldException(403, "Forbidden");
            }

            Controller.validateParams(req, "courseId");

            if (req.queryParams("courseId").isEmpty()) {
                throw new InvalidFieldException(400, "Course ID is required");
            }

            if (req.queryParams("courseId").matches("[^0-9]+")) {
                throw new InvalidFieldException(400, "Course ID must be a number");
            }

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT thumbnail FROM tblcourse WHERE courseid = ? AND author = ?")) {
                stmt.setInt(1, Integer.parseInt(req.queryParams("courseId")));
                stmt.setInt(2, req.attribute("userId"));

                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    throw new InvalidFieldException(400, "Course not found");
                }

                String thumbnail = rs.getString("thumbnail");

                try (PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM tblcourse WHERE courseid = ? AND author = ?")) {
                    stmt2.setInt(1, Integer.parseInt(req.queryParams("courseId")));
                    stmt2.setInt(2, req.attribute("userId"));

                    if (stmt2.executeUpdate() == 0) {
                        throw new InvalidFieldException(400, "Failed to delete course");
                    }

                    if (thumbnail != null) {
                        Path path = Path.of(System.getenv("SERVER_RESOURCE_PATH"), thumbnail);
                        Files.deleteIfExists(path);
                    }

                    res.status(200);
                    return GsonData.objectToJson(new ResponseGson<>(true, "Course deleted successfully"));
                }
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(new ResponseGson<>(false, e.getMessage())));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(new ResponseGson<>(false, "Something went wrong in the server")));
        }
        return null;
    }
}

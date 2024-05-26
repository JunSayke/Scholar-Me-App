package org.example.handler.course.lesson;

import org.example.Controller;
import org.example.data.GsonData;
import org.example.data.ResponseGson;
import org.example.exception.InvalidFieldException;
import org.example.utils.MySQLConnection;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static spark.Spark.halt;

public class AddCourseLessonHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            if (!req.attribute("role").equals("creator")) {
                throw new InvalidFieldException(403, "Forbidden");
            }

            Controller.validateParams(req, "courseId", "title", "lessonNumber", "description", "content", "duration");

            if (req.queryParams("courseId").isEmpty()) {
                throw new InvalidFieldException(400, "Course ID is required");
            }

            if (req.queryParams("courseId").matches("[^0-9]+")) {
                throw new InvalidFieldException(400, "Course ID must be a number");
            }

            if (req.queryParams("title").length() < 2 || req.queryParams("title").length() > 255) {
                throw new InvalidFieldException(400, "Title must be between 2 and 255 characters long");
            }

            if (req.queryParams("lessonNumber").isEmpty()) {
                throw new InvalidFieldException(400, "Lesson number is required");
            }

            if (req.queryParams("lessonNumber").matches("[^0-9]+")) {
                throw new InvalidFieldException(400, "Lesson number must be a number");
            }

            if (req.queryParams("description").length() < 2 || req.queryParams("description").length() > 255) {
                throw new InvalidFieldException(400, "Description must be between 2 and 255 characters long");
            }

            if (req.queryParams("content").length() < 2 || req.queryParams("content").length() > 600000) {
                throw new InvalidFieldException(400, "Content must be between 2 and 600,000 characters long");
            }

            if (req.queryParams("duration").isEmpty()) {
                throw new InvalidFieldException(400, "Duration is required");
            }

            if (req.queryParams("duration").matches("[^0-9]+")) {
                throw new InvalidFieldException(400, "Duration must be a number");
            }

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO tblcourselesson (courseid, title, lessonno, description, content, duration) VALUES (?, ?, ?, ?, ?, ?)")) {
                stmt.setInt(1, Integer.parseInt(req.queryParams("courseId")));
                stmt.setString(2, req.queryParams("title"));
                stmt.setInt(3, Integer.parseInt(req.queryParams("lessonNumber")));
                stmt.setString(4, req.queryParams("description"));
                stmt.setString(5, req.queryParams("content"));
                stmt.setInt(6, Integer.parseInt(req.queryParams("duration")));
                stmt.executeUpdate();

                res.status(200);
                return GsonData.objectToJson(new ResponseGson<>(true, "Lesson added successfully"));
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(new ResponseGson<>(false, e.getMessage())));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(new ResponseGson<>(false, "Something went wrong in the server")));
        }
        return null;
    }
}

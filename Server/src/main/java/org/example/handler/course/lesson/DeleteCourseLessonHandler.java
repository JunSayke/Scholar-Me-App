package org.example.handler.course.lesson;

import org.example.Controller;
import org.example.data.GsonData;
import org.example.data.ResponseGson;
import org.example.exception.InvalidFieldException;
import org.example.utils.MySQLConnection;
import spark.*;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static spark.Spark.halt;

public class DeleteCourseLessonHandler implements Route {

    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            if (!req.attribute("role").equals("creator")) {
                throw new InvalidFieldException(403, "Forbidden");
            }

            Controller.validateParams(req, "courseLessonId");

            if (req.queryParams("courseLessonId").isEmpty()) {
                throw new InvalidFieldException(400, "Lesson ID is required");
            }

            if (req.queryParams("courseLessonId").matches("[^0-9]+")) {
                throw new InvalidFieldException(400, "Lesson ID must be a number");
            }

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM tblcourselesson WHERE courselessonid = ?")) {
                stmt.setInt(1, Integer.parseInt(req.queryParams("courseLessonId")));
                if (stmt.executeUpdate() == 0) {
                    throw new InvalidFieldException(400, "Lesson not found");
                }

                res.status(200);
                return GsonData.objectToJson(new ResponseGson<>(true, "Lesson deleted successfully"));
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(ResponseGson.builder().status(false).message(e.getMessage()).build()));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(ResponseGson.builder().status(false).message("Something went wrong in the server").build()));
        }
        return null;
    }
}

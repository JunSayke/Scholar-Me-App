package org.example.handler.course.lesson;

import org.example.Controller;
import org.example.data.GsonData;
import org.example.data.ResponseGson;
import org.example.exception.InvalidFieldException;
import org.example.pattern.behavioural.SQLParamsChainOfResponsibility;
import org.example.utils.MySQLConnection;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.halt;

public class EditCourseLesson implements Route {
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
                throw new InvalidFieldException(400, "Course lesson ID is required");
            }

            if (req.queryParams("courseLessonId").matches("[^0-9]+")) {
                throw new InvalidFieldException(400, "Course lesson ID must be a number");
            }

            List<String> updates = new ArrayList<>();
            List<Object> params = new ArrayList<>();

            if (req.queryParams("title") != null) {
                if (req.queryParams("title").length() < 2 || req.queryParams("title").length() > 255) {
                    throw new InvalidFieldException(400, "Title must be between 2 and 255 characters long");
                }

                updates.add("title = ?");
                params.add(req.queryParams("title"));
            }

            if (req.queryParams("lessonNumber") != null) {
                if (req.queryParams("lessonNumber").isEmpty()) {
                    throw new InvalidFieldException(400, "Lesson number is required");
                }

                if (req.queryParams("lessonNumber").matches("[^0-9]+")) {
                    throw new InvalidFieldException(400, "Lesson number must be a number");
                }

                updates.add("lessonno = ?");
                params.add(Integer.parseInt(req.queryParams("lessonNumber")));
            }

            if (req.queryParams("description") != null) {
                if (req.queryParams("description").length() < 2 || req.queryParams("description").length() > 255) {
                    throw new InvalidFieldException(400, "Description must be between 2 and 255 characters long");
                }

                updates.add("description = ?");
                params.add(req.queryParams("description"));
            }

            if (req.queryParams("content") != null) {
                if (req.queryParams("content").length() < 2 || req.queryParams("content").length() > 600000) {
                    throw new InvalidFieldException(400, "Content must be between 2 and 600,000 characters long");
                }

                updates.add("content = ?");
                params.add(req.queryParams("content"));
            }

            if (req.queryParams("duration") != null) {
                if (req.queryParams("duration").isEmpty()) {
                    throw new InvalidFieldException(400, "Duration is required");
                }

                if (req.queryParams("duration").matches("[^0-9]+")) {
                    throw new InvalidFieldException(400, "Duration must be a number");
                }

                updates.add("duration = ?");
                params.add(Integer.parseInt(req.queryParams("duration")));
            }

            if (updates.isEmpty()) {
                throw new InvalidFieldException(200, "No changes made");
            }

            String updateClause = String.join(", ", updates);
            String query = "UPDATE tblcourselesson SET " + updateClause + " WHERE courselessonid = ?";

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                SQLParamsChainOfResponsibility paramsChain = Controller.getSQLParamsChain();
                for (int i = 0; i < params.size(); i++) {
                    paramsChain.handle(stmt, i + 1, params.get(i));
                }

                stmt.setInt(params.size() + 1, Integer.parseInt(req.queryParams("courseLessonId")));
                stmt.executeUpdate();
                res.status(200);
                return GsonData.objectToJson(new ResponseGson<>(true, "Course lesson updated"));
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(new ResponseGson<>(false, e.getMessage())));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(new ResponseGson<>(false, "Something went wrong in the server")));
        }
        return null;
    }
}

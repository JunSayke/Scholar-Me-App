package org.example.handler.course.lesson;

import org.example.Controller;
import org.example.data.CourseLessonGson;
import org.example.data.GsonData;
import org.example.data.ResponseGson;
import org.example.exception.InvalidFieldException;
import org.example.utils.MySQLConnection;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.halt;

public class GetCourseLessonsHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            Controller.validateParams(req, "courseId");

            if (req.queryParams("courseId").isEmpty()) {
                throw new InvalidFieldException(400, "Course ID is required");
            }

            if (req.queryParams("courseId").matches("[^0-9]+")) {
                throw new InvalidFieldException(400, "Course ID must be a number");
            }

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tblcourselesson WHERE courseid = ?")) {
                stmt.setInt(1, Integer.parseInt(req.queryParams("courseId")));
                ResultSet rs = stmt.executeQuery();

                List<CourseLessonGson> courseLessons = new ArrayList<>();

                while (rs.next()) {
                    CourseLessonGson courseLesson = CourseLessonGson.builder()
                            .courseLessonId(rs.getInt("courselessonid"))
                            .courseId(rs.getInt("courseid"))
                            .title(rs.getString("title"))
                            .lessonNumber(rs.getInt("lessonno"))
                            .description(rs.getString("description"))
                            .content(rs.getString("content"))
                            .duration(rs.getInt("duration"))
                            .isLocked(rs.getBoolean("islocked"))
                            .dateAdded(rs.getTimestamp("dateadded").toLocalDateTime())
                            .dateUpdated(rs.getTimestamp("dateupdated").toLocalDateTime())
                            .build();
                    courseLessons.add(courseLesson);
                }

                res.status(200);
                return GsonData.objectToJson(new ResponseGson<>(true, "Course lessons retrieved", courseLessons));
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(ResponseGson.builder().status(false).message(e.getMessage()).build()));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(ResponseGson.builder().status(false).message("Something went wrong in the server").build()));
        }
        return null;
    }
}

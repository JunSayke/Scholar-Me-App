package org.example.handler.course;

import org.example.Controller;
import org.example.data.CourseGson;
import org.example.data.CourseLearnerGson;
import org.example.data.GsonData;
import org.example.data.ResponseGson;
import org.example.exception.InvalidFieldException;
import org.example.utils.MySQLConnection;
import spark.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.halt;

public class GetUserCoursesHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT JSON_OBJECT('courseId', c.courseid, 'userId', cl.userid, 'dateEnrolled', cl.dateenrolled, 'author', c.author, 'title', c.title, 'description', c.description, 'thumbnail', c.thumbnail, 'views', c.views, 'dateAdded', c.dateadded, 'dateUpdated', c.dateupdated) as courses FROM tblcourselearner cl JOIN tblcourse c ON cl.userid = c.author WHERE cl.userid = ?")) {
                stmt.setInt(1, req.attribute("userId"));
                ResultSet rs = stmt.executeQuery();

                List<CourseLearnerGson> courses = new ArrayList<>();

                while (rs.next()) {
                    courses.add(GsonData.jsonToObject(rs.getString("courses"), CourseLearnerGson.class));
                }
                res.status(200);
                return GsonData.objectToJson(new ResponseGson<>(true, "Courses retrieved", courses));
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(new ResponseGson<>(false, e.getMessage())));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(new ResponseGson<>(false, "Something went wrong in the server")));
        }
        return null;
    }
}

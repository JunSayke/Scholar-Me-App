package org.example.handler.course;

import org.example.Controller;
import org.example.data.CourseGson;
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

public class GetCreatorCoursesHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            if (!req.attribute("role").equals("creator")) {
                throw new InvalidFieldException(403, "Forbidden");
            }

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT JSON_OBJECT('courseId', courseid, 'author', author, 'title', title, 'description', description, 'thumbnail', thumbnail, 'views', views, 'dateAdded', dateadded, 'dateUpdated', dateupdated) as courses FROM tblcourse WHERE author = ?")) {
                stmt.setInt(1, Integer.parseInt(req.attribute("userId")));
                ResultSet rs = stmt.executeQuery();

                List<CourseGson> courses = new ArrayList<>();

                while (rs.next()) {
                    courses.add(GsonData.jsonToObject(rs.getString("courses"), CourseGson.class));
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

package org.example.handler.course;

import org.example.Controller;
import org.example.data.CourseGson;
import org.example.data.GsonData;
import org.example.data.ResponseGson;
import org.example.data.UserGson;
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
                 PreparedStatement stmt = conn.prepareStatement("SELECT c.courseid, up.acctid, up.firstname, up.lastname, up.profilepic, c.title, c.description, c.thumbnail, c.views, c.dateadded, c.dateupdated, SUM(l.duration) as totalDuration FROM tblcourse c JOIN tbluserprofile up ON c.author = up.acctid LEFT JOIN tblcourselesson l ON c.courseid = l.courseid WHERE c.author = ? GROUP BY c.courseid")) {
                stmt.setInt(1, req.attribute("userId"));
                ResultSet rs = stmt.executeQuery();

                List<CourseGson> courses = new ArrayList<>();

                while (rs.next()) {
                    UserGson author = UserGson.builder()
                            .userId(rs.getInt("acctid"))
                            .firstName(rs.getString("firstname"))
                            .lastName(rs.getString("lastname"))
                            .profilePic(rs.getString("profilepic"))
                            .build();

                    CourseGson course = CourseGson.builder()
                            .courseId(rs.getInt("courseid"))
                            .author(author)
                            .title(rs.getString("title"))
                            .description(rs.getString("description"))
                            .thumbnail(rs.getString("thumbnail"))
                            .views(rs.getInt("views"))
                            .dateAdded(rs.getTimestamp("dateadded").toLocalDateTime())
                            .dateUpdated(rs.getTimestamp("dateupdated").toLocalDateTime())
                            .totalDuration(rs.getInt("totalDuration"))
                            .build();

                    courses.add(course);
                }

                res.status(200);
                return GsonData.objectToJson(new ResponseGson<>(true, "Courses retrieved", courses));
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(new ResponseGson<>(false, e.getMessage())));
        } catch (Exception e) {
//            e.printStackTrace();
            halt(500, GsonData.objectToJson(new ResponseGson<>(false, "Something went wrong in the server")));
        }
        return null;
    }
}

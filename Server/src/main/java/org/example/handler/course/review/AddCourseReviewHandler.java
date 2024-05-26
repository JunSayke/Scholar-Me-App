package org.example.handler.course.review;

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
import java.sql.ResultSet;

import static spark.Spark.halt;

public class AddCourseReviewHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            Controller.validateParams(req, "commentId", "courseId", "comment");

            if (req.queryParams("commentId").isEmpty()) {
                throw new InvalidFieldException(400, "Comment ID is required");
            }

            if (req.queryParams("commentId").matches("[^0-9]+")) {
                throw new InvalidFieldException(400, "Comment ID must be a number");
            }

            if (req.queryParams("courseId").isEmpty()) {
                throw new InvalidFieldException(400, "Course ID is required");
            }

            if (req.queryParams("courseId").matches("[^0-9]+")) {
                throw new InvalidFieldException(400, "Course ID must be a number");
            }

            if (req.queryParams("comment").length() < 2 || req.queryParams("comment").length() > 2000) {
                throw new InvalidFieldException(400, "Comment must be between 2 and 2000 characters long");
            }

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM tblcourselearner WHERE courseid = ? AND userid = ?")) {
                stmt.setInt(1, Integer.parseInt(req.queryParams("courseId")));
                stmt.setInt(2, req.attribute("userId"));

                if (!stmt.executeQuery().next()) {
                    throw new InvalidFieldException(400, "You must enroll in the course to review it");
                }

                conn.setAutoCommit(false);
                try (PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO tblcomment (userid, comment) VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                    stmt2.setInt(1, req.attribute("userId"));
                    stmt2.setString(2, req.queryParams("comment"));
                    stmt2.executeUpdate();

                    ResultSet rs = stmt2.getGeneratedKeys();

                    if (!rs.next()) {
                        throw new InvalidFieldException(400, "Failed to add comment");
                    }

                    int commentId = rs.getInt(1);
                    try (PreparedStatement stmt3 = conn.prepareStatement("INSERT INTO tblcoursecomment (commentid, courseid) VALUES (?, ?)")) {
                        stmt3.setInt(1, commentId);
                        stmt3.setInt(2, Integer.parseInt(req.queryParams("courseId")));
                        stmt3.executeUpdate();
                    }
                    conn.commit();
                } catch (Exception e) {
                    conn.rollback();
                    throw e;
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

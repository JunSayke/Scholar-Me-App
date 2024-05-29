package org.example.handler.review;

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

public class AddDiscussionCommentHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            Controller.validateParams(req, "comment");

            if (req.queryParams("comment").length() < 2 || req.queryParams("comment").length() > 255) {
                throw new InvalidFieldException(400, "Comment must be between 2 and 255 characters long");
            }

            try (Connection conn = MySQLConnection.getConnection()) {
                conn.setAutoCommit(false);

                try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO tblcomment (userid, comment) VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, req.attribute("userId"));
                    stmt.setString(2, req.queryParams("comment"));
                    stmt.executeUpdate();

                    try (PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO tbldiscussioncomment (commentid) VALUES (?)")) {
                        stmt2.setInt(1, stmt.getGeneratedKeys().getInt(1));
                        stmt2.executeUpdate();
                    }
                } catch (Exception e) {
                    conn.rollback();
                    throw e;
                }

                conn.commit();
                res.status(200);
                return GsonData.objectToJson(new ResponseGson<>(true, "Comment added successfully"));
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(ResponseGson.builder().status(false).message(e.getMessage()).build()));
        } catch (Exception e) {
            e.printStackTrace();
            halt(500, GsonData.objectToJson(ResponseGson.builder().status(false).message("Something went wrong in the server").build()));
        }
        return null;
    }
}

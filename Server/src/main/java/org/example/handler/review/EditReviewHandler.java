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

public class EditReviewHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            Controller.validateParams(req, "commentId");

            if (req.queryParams("commentId").isEmpty()) {
                throw new InvalidFieldException(400, "Comment ID is required");
            }

            if (req.queryParams("commentId").matches("[^0-9]+")) {
                throw new InvalidFieldException(400, "Comment ID must be a number");
            }

            if (req.queryParams("comment") == null) {
                throw new InvalidFieldException(200, "No changes made");
            }

            if (req.queryParams("comment").length() < 2 || req.queryParams("comment").length() > 2000) {
                throw new InvalidFieldException(400, "Comment must be between 2 and 2,000 characters long");
            }

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("UPDATE tblcomment SET comment = ? WHERE commentid = ? AND userid = ?")) {
                stmt.setString(1, req.queryParams("comment"));
                stmt.setInt(2, Integer.parseInt(req.queryParams("commentId")));
                stmt.setInt(3, req.attribute("userId"));

                if (stmt.executeUpdate() == 0) {
                    throw new InvalidFieldException(400, "Comment not found");
                }

                res.status(200);
                return GsonData.objectToJson(new ResponseGson<>(true, "Comment updated successfully"));
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(new ResponseGson<>(false, e.getMessage())));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(new ResponseGson<>(false, "Something went wrong in the server")));
        }
        return null;
    }
}

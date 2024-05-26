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

public class DeleteReviewHandler implements Route {
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

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM tblcomment WHERE commentid = ? AND userid = ?")) {
                stmt.setInt(1, Integer.parseInt(req.queryParams("commentId")));
                stmt.setInt(2, req.attribute("userId"));
                if (stmt.executeUpdate() == 0) {
                    throw new InvalidFieldException(400, "Comment not found");
                }

                res.status(200);
                return GsonData.objectToJson(new ResponseGson<>(true, "Comment deleted successfully"));
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(new ResponseGson<>(false, e.getMessage())));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(new ResponseGson<>(false, "Something went wrong in the server")));
        }
        return null;
    }
}

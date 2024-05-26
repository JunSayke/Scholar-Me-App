package org.example.handler.reply;

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

public class AddReplyHandler implements Route {

    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            Controller.validateParams(req, "commentId", "userId", "reply");

            if (req.queryParams("commentId").isEmpty()) {
                throw new InvalidFieldException(400, "Comment ID is required");
            }

            if (req.queryParams("commentId").matches("[^0-9]+")) {
                throw new InvalidFieldException(400, "Comment ID must be a number");
            }

            if (req.queryParams("userId").isEmpty()) {
                throw new InvalidFieldException(400, "User ID is required");
            }

            if (req.queryParams("userId").matches("[^0-9]+")) {
                throw new InvalidFieldException(400, "User ID must be a number");
            }

            if (req.queryParams("reply").length() < 2 || req.queryParams("reply").length() > 2000) {
                throw new InvalidFieldException(400, "Reply must be between 2 and 2,000 characters long");
            }

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO tblreply (commentid, userid, reply) VALUES (?, ?, ?)")) {
                stmt.setInt(1, Integer.parseInt(req.queryParams("commentId")));
                stmt.setInt(2, Integer.parseInt(req.queryParams("userId")));
                stmt.setString(3, req.queryParams("reply"));
                stmt.executeUpdate();

                res.status(200);
                return GsonData.objectToJson(new ResponseGson<>(true, "Reply added successfully"));
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(new ResponseGson<>(false, e.getMessage())));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(new ResponseGson<>(false, "Something went wrong in the server")));
        }
        return null;
    }
}

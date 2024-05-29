package org.example.handler;

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

public class DeleteUserNotification implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            Controller.validateParams(req, "notificationId");

            if (req.queryParams("notificationId").isEmpty()) {
                throw new InvalidFieldException(400, "Notification ID is required");
            }

            if (req.queryParams("notificationId").matches("[^0-9]+")) {
                throw new InvalidFieldException(400, "Notification ID must be a number");
            }

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM tblusernotification WHERE notificationid = ?")) {
                stmt.setInt(1, Integer.parseInt(req.queryParams("notificationId")));

                if (stmt.executeUpdate() == 0) {
                    throw new InvalidFieldException(400, "Notification not found");
                }

                res.status(200);
                return GsonData.objectToJson(new ResponseGson<>(true, "Notification deleted successfully"));
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(ResponseGson.builder().status(false).message(e.getMessage()).build()));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(ResponseGson.builder().status(false).message("Something went wrong in the server").build()));
        }
        return null;
    }
}

package org.example.handler;

import org.example.Controller;
import org.example.data.GsonData;
import org.example.data.NotificationGson;
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

public class GetUserNotificationsHandler implements Route {

    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT JSON_OBJECT('notificationId', notificationid, 'userId', userid, 'title', title, 'message', message, 'dateAdded', dateadded, 'dateUpdated', dateupdated) as notification FROM tblnotification WHERE userid = ?")) {
                stmt.setInt(1, req.attribute("userId"));
                ResultSet rs = stmt.executeQuery();

                List<NotificationGson> notifications = new ArrayList<>();

                while (rs.next()) {
                    notifications.add(GsonData.jsonToObject(rs.getString("notification"), NotificationGson.class));
                }

                res.status(200);
                return GsonData.objectToJson(new ResponseGson<>(true, "Notifications retrieved", notifications));
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(new ResponseGson<>(false, e.getMessage())));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(new ResponseGson<>(false, "Something went wrong in the server")));
        }
        return null;
    }
}

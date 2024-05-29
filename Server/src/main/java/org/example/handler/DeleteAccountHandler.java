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
import java.sql.ResultSet;

import static spark.Spark.halt;

public class DeleteAccountHandler implements Route {

    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT status FROM tbluseraccount WHERE userid = ? LIMIT 1")) {
                stmt.setInt(1, req.attribute("userId"));
                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    throw new InvalidFieldException(404, "User not found");
                }

                if (rs.getString("status").equals("inactive")) {
                    throw new InvalidFieldException(400, "Account already deleted");
                }

                try (PreparedStatement stmt2 = conn.prepareStatement("UPDATE tbluseraccount SET status = 'inactive' WHERE userid = ?")) {
                    stmt2.setInt(1, req.attribute("userId"));
                    stmt2.executeUpdate();
                    res.status(200);
                    return GsonData.objectToJson(new ResponseGson<>(true, "Account deleted"));
                }
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(ResponseGson.builder().status(false).message(e.getMessage()).build()));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(ResponseGson.builder().status(false).message("Something went wrong in the server").build()));
        }
        return null;
    }
}

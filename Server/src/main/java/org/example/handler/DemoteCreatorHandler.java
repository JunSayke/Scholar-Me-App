package org.example.handler;

import org.example.Controller;
import org.example.data.ErrorGson;
import org.example.data.GsonData;
import org.example.data.SuccessGson;
import org.example.exception.InvalidFieldException;
import org.example.utils.MySQLConnection;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static spark.Spark.halt;

public class DemoteCreatorHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            if (!"admin".equals(req.attribute("role"))) {
                throw new InvalidFieldException(403, "Forbidden");
            }

            Controller.validateParams(req, "userid");

            if (req.queryParams("userid").isEmpty()) {
                throw new InvalidFieldException(400, "userid cannot be empty");
            }

            if (req.queryParams("userid").matches("[^0-9]+")) {
                throw new InvalidFieldException(400, "userid must be a number");
            }

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT role FROM tbluseraccount WHERE userid = ?")) {
                stmt.setInt(1, Integer.parseInt(req.queryParams("userid")));
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    if (!rs.getString("role").equals("creator")) {
                        throw new InvalidFieldException(400, "User is not a creator");
                    }
                    try (PreparedStatement stmt2 = conn.prepareStatement("UPDATE tbluseraccount SET role = 'user' WHERE userid = ?")) {
                        stmt2.setInt(1, Integer.parseInt(req.queryParams("userid")));
                        stmt2.executeUpdate();
                    }
                    return GsonData.objectToJson(new SuccessGson<>(true, "Creator demoted successfully", null));
                }
                throw new InvalidFieldException(400, "User not found");
            }
        } catch (InvalidFieldException e) {
//            e.printStackTrace();
            halt(e.getStatusCode(), GsonData.objectToJson(new ErrorGson(false, e.getMessage())));
        } catch (Exception e) {
//            e.printStackTrace();
            halt(500, GsonData.objectToJson(new ErrorGson(false, e.getMessage())));
        }
        return null;
    }
}

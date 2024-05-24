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

public class DemoteCreatorHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            if (!"admin".equals(req.attribute("role"))) {
                throw new InvalidFieldException(403, "Forbidden");
            }

            Controller.validateParams(req, "userId");

            if (req.queryParams("userId").isEmpty()) {
                throw new InvalidFieldException(400, "userid cannot be empty");
            }

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT role FROM tbluseraccount WHERE userid = ? LIMIT 1")) {
                stmt.setInt(1, Integer.parseInt(req.queryParams("userId")));
                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    throw new InvalidFieldException(404, "User not found");
                }

                if (!rs.getString("role").equals("creator")) {
                    throw new InvalidFieldException(400, "User is not a creator");
                }
                try (PreparedStatement stmt2 = conn.prepareStatement("UPDATE tbluseraccount SET role = 'user' WHERE userid = ?")) {
                    stmt2.setInt(1, Integer.parseInt(req.queryParams("userId")));
                    stmt2.executeUpdate();
                }
                return GsonData.objectToJson(new ResponseGson<>(true, "Creator demoted successfully"));
            }
        } catch (InvalidFieldException e) {
//            e.printStackTrace();
            halt(e.getStatusCode(), GsonData.objectToJson(new ResponseGson<>(false, e.getMessage())));
        } catch (Exception e) {
//            e.printStackTrace();
            halt(500, GsonData.objectToJson(new ResponseGson<>(false, "Something went wrong in the server")));
        }
        return null;
    }
}

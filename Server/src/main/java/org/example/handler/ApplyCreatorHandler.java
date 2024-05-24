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

public class ApplyCreatorHandler implements Route {

    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            if (!"user".equals(req.attribute("role"))) {
                throw new InvalidFieldException(403, "Forbidden");
            }

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM tbluseraccount WHERE userid = ? LIMIT 1")) {
                stmt.setInt(1, req.attribute("userId"));
                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    throw new InvalidFieldException(404, "User not found");
                }

                // Check if the user already has a pending application
                try (PreparedStatement stmt2 = conn.prepareStatement("SELECT 1 FROM tblcreatorapplicant WHERE userid = ? AND status = 'pending' LIMIT 1")) {
                    stmt2.setInt(1, req.attribute("userId"));
                    ResultSet rs2 = stmt2.executeQuery();
                    if (rs2.next()) {
                        throw new InvalidFieldException(409, "You already have a pending application");
                    }
                }

                // If no pending application, proceed with the application submission
                try (PreparedStatement stmt3 = conn.prepareStatement("INSERT INTO tblcreatorapplicant (userid) VALUES (?)")) {
                    stmt3.setInt(1, req.attribute("userId"));
                    stmt3.executeUpdate();
                    res.status(200);
                    return GsonData.objectToJson(new ResponseGson<>(true, "Application submitted"));
                }
            }
        } catch (InvalidFieldException e) {
//            e.printStackTrace();
            halt(e.getStatusCode(), GsonData.objectToJson(new ResponseGson<>(false, e.getMessage())));
        } catch (Exception e) {
            e.printStackTrace();
            halt(500, GsonData.objectToJson(new ResponseGson<>(false, "Something went wrong in the server")));
        }
        return null;
    }
}

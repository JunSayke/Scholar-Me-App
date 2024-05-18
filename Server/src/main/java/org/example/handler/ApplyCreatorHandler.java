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
                 PreparedStatement stmt = conn.prepareStatement("SELECT EXISTS(SELECT 1 FROM tbluseraccount WHERE userid = ?) as 'exists'")) {
                stmt.setInt(1, req.attribute("userid"));
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    if (rs.getInt("exists") == 0) {
                        throw new InvalidFieldException(400, "User not found");
                    }

                    // Check if the user already has a pending application
                    try (PreparedStatement stmt2 = conn.prepareStatement("SELECT EXISTS(SELECT 1 FROM tblcreatorapplicant WHERE userid = ? AND status = 'pending') as 'exists'")) {
                        stmt2.setInt(1, req.attribute("userid"));
                        ResultSet rs2 = stmt2.executeQuery();
                        if (rs2.next() && rs2.getInt("exists") == 1) {
                            throw new InvalidFieldException(409, "You already have a pending application");
                        }
                    }

                    // If no pending application, proceed with the application submission
                    try (PreparedStatement stmt3 = conn.prepareStatement("INSERT INTO tblcreatorapplicant (userid) VALUES (?)")) {
                        stmt3.setInt(1, req.attribute("userid"));
                        stmt3.executeUpdate();
                        res.status(200);
                        return GsonData.objectToJson(new SuccessGson<>(true, "Application submitted", null));
                    }
                }
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

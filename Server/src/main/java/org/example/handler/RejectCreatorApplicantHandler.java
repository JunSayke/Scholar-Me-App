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

public class RejectCreatorApplicantHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            if (!"admin".equals(req.attribute("role"))) {
                throw new InvalidFieldException(403, "Forbidden");
            }

            Controller.validateParams(req, "creatorapplicantid");

            if (req.queryParams("creatorapplicantid").isEmpty()) {
                throw new InvalidFieldException(400, "creatorapplicantid cannot be empty");
            }

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT status FROM tblcreatorapplicant WHERE creatorapplicantid = ? LIMIT 1")) {
                stmt.setInt(1, Integer.parseInt(req.queryParams("creatorapplicantid")));
                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    throw new InvalidFieldException(404, "Application not found");
                }

                if (rs.getString("status").equals("rejected")) {
                    throw new InvalidFieldException(409, "Application already rejected");
                }
                if (!rs.getString("status").equals("pending")) {
                    throw new InvalidFieldException(409, "Application already processed");
                }
                try (PreparedStatement stmt2 = conn.prepareStatement("UPDATE tblcreatorapplicant SET status = 'rejected' WHERE creatorapplicantid = ?")) {
                    stmt2.setInt(1, Integer.parseInt(req.queryParams("creatorapplicantid")));
                    stmt2.executeUpdate();
                    res.status(200);
                    return GsonData.objectToJson(new ResponseGson<>(true, "Application rejected"));
                }
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(new ResponseGson<>(false, e.getMessage())));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(new ResponseGson<>(false, "Something went wrong in the server")));
        }
        return null;
    }
}

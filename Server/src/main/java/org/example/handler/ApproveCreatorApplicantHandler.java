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

public class ApproveCreatorApplicantHandler implements Route {

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

            if (req.queryParams("creatorapplicantid").matches("[^0-9]+")) {
                throw new InvalidFieldException(400, "creatorapplicantid must be a number");
            }

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT status FROM tblcreatorapplicant WHERE creatorapplicantid = ?")) {
                stmt.setInt(1, Integer.parseInt(req.queryParams("creatorapplicantid")));
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    if (rs.getString("status").equals("approved")) {
                        throw new InvalidFieldException(409, "Application already approved");
                    }
                    if (!rs.getString("status").equals("pending")) {
                        throw new InvalidFieldException(409, "Application already processed");
                    }
                    conn.setAutoCommit(false);
                    try (PreparedStatement stmt2 = conn.prepareStatement("UPDATE tblcreatorapplicant ta JOIN tbluseraccount ua ON ta.userid = ua.userid SET ta.status = 'approved', ua.role = 'creator' WHERE ta.creatorapplicantid = ?")) {
                        stmt2.setInt(1, Integer.parseInt(req.queryParams("creatorapplicantid")));
                        stmt2.executeUpdate();

                        conn.commit();
                        res.status(200);
                        return GsonData.objectToJson(new SuccessGson<>(true, "Application approved", null));
                    } catch (Exception e) {
                        conn.rollback();
                        throw e;
                    }
                }
                throw new InvalidFieldException(400, "Application not found");
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(new ErrorGson(false, e.getMessage())));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(new ErrorGson(false, e.getMessage())));
        }
        return null;
    }
}

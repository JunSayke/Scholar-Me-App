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

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT status, userid FROM tblcreatorapplicant WHERE creatorapplicantid = ? LIMIT 1")) {
                stmt.setInt(1, Integer.parseInt(req.queryParams("creatorapplicantid")));
                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    throw new InvalidFieldException(404, "Application not found");
                }

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
                } catch (Exception e) {
                    conn.rollback();
                    throw e;
                }

                Controller.addNotification(rs.getInt("userid"), "Approved", "Your application to become a creator has been approved");
                res.status(200);
                return GsonData.objectToJson(new ResponseGson<>(true, "Application approved"));
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(ResponseGson.builder().status(false).message(e.getMessage()).build()));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(ResponseGson.builder().status(false).message("Something went wrong in the server").build()));
        }
        return null;
    }
}

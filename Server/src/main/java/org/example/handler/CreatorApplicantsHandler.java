package org.example.handler;

import org.example.Controller;
import org.example.data.CreatorApplicantGson;
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
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.halt;

public class CreatorApplicantsHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            if (!"admin".equals(req.attribute("role"))) {
                res.status(403);
            }

            String status = req.queryParams("status");

            // Check for optional status query parameter
            String sql = "SELECT * FROM tblcreatorapplicant";
            if (status != null) {
                // Check if status is valid
                if (!status.equals("pending") && !status.equals("approved") && !status.equals("rejected")) {
                    throw new InvalidFieldException(400, "Invalid status");
                }
                sql += " WHERE status = ?";
            }

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                if (status != null) {
                    stmt.setString(1, status);
                }

                ResultSet rs = stmt.executeQuery();

                List<CreatorApplicantGson> applicants = new ArrayList<>();
                while (rs.next()) {
                    CreatorApplicantGson applicant = CreatorApplicantGson.builder()
                            .creatorApplicantId(rs.getInt("creatorapplicantid"))
                            .userId(rs.getInt("userid"))
                            .status(rs.getString("status"))
                            .dateAdded(String.valueOf(rs.getTimestamp("dateadded")))
                            .dateUpdated(String.valueOf(rs.getTimestamp("dateupdated")))
                            .build();
                    applicants.add(applicant);
                }
                res.status(200);
                return GsonData.objectToJson(new SuccessGson<>(true, "Creator applicants retrieved", applicants));
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

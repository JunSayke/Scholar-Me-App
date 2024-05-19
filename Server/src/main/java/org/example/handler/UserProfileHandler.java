package org.example.handler;

import org.example.Controller;
import org.example.data.ErrorGson;
import org.example.data.GsonData;
import org.example.data.SuccessGson;
import org.example.data.UserGson;
import org.example.exception.InvalidFieldException;
import org.example.utils.MySQLConnection;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static spark.Spark.halt;

public class UserProfileHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateParams(req, "userid");

            if (req.queryParams("userid").isEmpty()) {
                throw new InvalidFieldException(400, "userid cannot be empty");
            }

            try (Connection conn = MySQLConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT ua.userid, ua.username, ua.email, ua.role, ua.status, ua.dateadded, ua.dateupdated, up.firstname, up.lastname, up.phonenumber, up.profilepic FROM tbluseraccount ua JOIN tbluserprofile up ON ua.userid = up.acctid WHERE ua.userid = ? LIMIT 1")) {
                stmt.setInt(1, Integer.parseInt(req.queryParams("userid")));
                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    throw new InvalidFieldException(404, "User not found");
                }

                UserGson user = UserGson.builder()
                        .userId(rs.getInt("userid"))
                        .username(rs.getString("username"))
                        .email(rs.getString("email"))
                        .role(rs.getString("role"))
                        .firstName(rs.getString("firstname"))
                        .lastName(rs.getString("lastname"))
                        .phoneNumber(rs.getString("phonenumber"))
                        .profilePic(rs.getString("profilepic"))
                        .status(rs.getString("status"))
                        .dateAdded(rs.getString("dateadded"))
                        .dateUpdated(rs.getString("dateupdated"))
                        .build();
                return GsonData.objectToJson(new SuccessGson<>(true, "User profile retrieved", user));
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(new ErrorGson(false, e.getMessage())));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(new ErrorGson(false, e.getMessage())));
        }
        return null;
    }
}

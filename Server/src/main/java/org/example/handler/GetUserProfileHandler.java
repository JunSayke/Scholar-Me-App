package org.example.handler;

import org.example.Controller;
import org.example.data.GsonData;
import org.example.data.ResponseGson;
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

public class GetUserProfileHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateParams(req, "userId");

            if (req.queryParams("userId").isEmpty()) {
                throw new InvalidFieldException(400, "userid cannot be empty");
            }

            try (Connection conn = MySQLConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT JSON_OBJECT('userId', ua.userid, 'username', ua.username, 'email', ua.email, 'firstName', up.firstname, 'lastName', up.lastname, 'phoneNumber', up.phonenumber, 'profilePic', up.profilepic, 'role', ua.role, 'dateAdded', ua.dateadded, 'dateUpdated', ua.dateupdated) as user FROM tbluseraccount ua JOIN tbluserprofile up ON ua.userid = up.acctid WHERE ua.userid = ? LIMIT 1")) {
                stmt.setInt(1, Integer.parseInt(req.queryParams("userId")));
                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    throw new InvalidFieldException(404, "User not found");
                }

                UserGson user = GsonData.jsonToObject(rs.getString("user"), UserGson.class);
                res.status(200);
                return GsonData.objectToJson(new ResponseGson<>(true, "User profile retrieved", user));
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(ResponseGson.builder().status(false).message(e.getMessage()).build()));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(ResponseGson.builder().status(false).message("Something went wrong in the server").build()));
        }
        return null;
    }
}

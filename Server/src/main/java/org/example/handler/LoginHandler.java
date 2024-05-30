package org.example.handler;

import org.example.Controller;
import org.example.data.GsonData;
import org.example.data.ResponseGson;
import org.example.data.UserGson;
import org.example.exception.InvalidFieldException;
import org.example.utils.JwtUtil;
import org.example.utils.MySQLConnection;
import org.mindrot.jbcrypt.BCrypt;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import static spark.Spark.halt;

public class LoginHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
        res.type("application/json");

        try {
            // Check if any required field is missing
            Controller.validateParams(req, "email", "password");

            // https://www.javatpoint.com/java-email-validation#:~:text=To%20validate%20the%20email%20permitted,%5D%2B%24%22%20regular%20expression.
            boolean isEmail = req.queryParams("email").matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}");

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT ua.password, JSON_OBJECT('userId', ua.userid, 'username', ua.username, 'email', ua.email, 'firstName', up.firstname, 'lastName', up.lastname, 'phoneNumber', up.phonenumber, 'profilePic', up.profilepic, 'role', ua.role, 'dateAdded', ua.dateadded, 'dateUpdated', ua.dateupdated) as user FROM tbluseraccount ua JOIN tbluserprofile up ON ua.userid = up.acctid WHERE " + (isEmail ? "ua.email" : "ua.username") + " = ? LIMIT 1")) {
                stmt.setString(1, req.queryParams("email"));
                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    throw new InvalidFieldException(400, "Incorrect email or username");
                }

                if (!BCrypt.checkpw(req.queryParams("password"), rs.getString("password"))) {
                    throw new InvalidFieldException(400, "Incorrect password");
                }

                UserGson user = GsonData.jsonToObject(rs.getString("user"), UserGson.class);

                if (user.getRole().equals("inactive")) {
                    try (PreparedStatement stmt2 = conn.prepareStatement("UPDATE tbluseraccount SET status = 'active' WHERE userid = ?")) {
                        stmt2.setInt(1, rs.getInt("userId"));
                    }
                }

                // Generate JWT token
                String token = JwtUtil.createToken(user.getUserId(), user.getRole());
                res.cookie("access_token", token);
                res.status(200);

                return GsonData.objectToJson(new ResponseGson<>(true, "Login successful", user));
            }
        } catch (InvalidFieldException e) {
//            e.printStackTrace();
            halt(e.getStatusCode(), GsonData.objectToJson(ResponseGson.builder().status(false).message(e.getMessage()).build()));
        } catch (Exception e) {
//            e.printStackTrace();
            halt(500, GsonData.objectToJson(ResponseGson.builder().status(false).message("Something went wrong in the server").build()));
        }

        return null;
    }
}

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
                 PreparedStatement stmt = conn.prepareStatement("SELECT ua.userid, ua.username, ua.email, ua.password, ua.role, ua.dateadded, ua.status, ua.dateadded, ua.dateupdated, up.firstname, up.lastname, up.phonenumber, up.profilepic FROM tbluseraccount ua JOIN tbluserprofile up ON ua.userid = up.acctid WHERE " + (isEmail ? "ua.email" : "ua.username") + " = ? LIMIT 1")) {
                stmt.setString(1, req.queryParams("email"));
                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    throw new InvalidFieldException(400, "Incorrect email or username");
                }

                if (!BCrypt.checkpw(req.queryParams("password"), rs.getString("password"))) {
                    throw new InvalidFieldException(400, "Incorrect password");
                }

                if (rs.getString("status").equals("inactive")) {
                    try (PreparedStatement stmt2 = conn.prepareStatement("UPDATE tbluseraccount SET status = 'active' WHERE userid = ?")) {
                        stmt2.setInt(1, rs.getInt("userId"));
                    }
                }

                UserGson user = UserGson.builder()
                        .userId(rs.getInt("userId"))
                        .username(rs.getString("username"))
                        .email(rs.getString("email"))
                        .firstName(rs.getString("firstname"))
                        .lastName(rs.getString("lastname"))
                        .phoneNumber(rs.getString("phonenumber"))
                        .profilePic(rs.getString("profilepic"))
                        .role(rs.getString("role"))
                        .dateAdded(rs.getTimestamp("dateadded").toLocalDateTime())
                        .dateUpdated(rs.getTimestamp("dateupdated").toLocalDateTime())
                        .build();

                // Generate JWT token
                String token = JwtUtil.createToken(user.getUserId(), user.getRole());
                System.err.println(token);
                res.cookie("access_token", token);
                res.status(200);

                return GsonData.objectToJson(new ResponseGson<>(true, "Login successful", user));
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

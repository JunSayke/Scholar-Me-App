package org.example.handler;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.http.Part;
import org.example.Controller;
import org.example.data.GsonData;
import org.example.data.ResponseGson;
import org.example.exception.InvalidFieldException;
import org.example.utils.MySQLConnection;
import org.mindrot.jbcrypt.BCrypt;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static spark.Spark.halt;

public class EditProfileHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp.txt"));
        res.type("application/json");

        try {
            // Check if any required field is missing
            Controller.validateAccessToken(req);

            List<String> updates = new ArrayList<>();
            List<String> params = new ArrayList<>();

            if (req.queryParams("firstName") != null) {
                if (req.queryParams("firstName").length() < 2 || req.queryParams("firstName").length() > 30) {
                    throw new InvalidFieldException(400, "First name must be between 2 and 30 characters long");
                }
                updates.add("up.firstname = ?");
                params.add(req.queryParams("firstName"));
            }

            if (req.queryParams("lastName") != null) {
                if (req.queryParams("lastName").length() < 2 || req.queryParams("lastName").length() > 30) {
                    throw new InvalidFieldException(400, "Last name must be between 2 and 30 characters long");
                }
                updates.add("up.lastname = ?");
                params.add(req.queryParams("lastName"));
            }

            if (req.queryParams("phoneNumber") != null) {
                if (req.queryParams("phoneNumber").length() != 11) {
                    throw new InvalidFieldException(400, "Invalid phone number: must be 11 digits long");
                }
                updates.add("up.phonenumber = ?");
                params.add(req.queryParams("phoneNumber"));
            }

            if (req.queryParams("email") != null) {
                if (!req.queryParams("email").matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}")) {
                    throw new InvalidFieldException(400, "Invalid email: must be in the format of user@domain");
                }
                updates.add("ua.email = ?");
                params.add(req.queryParams("email"));
            }

            if (req.queryParams("password") != null) {
                if (req.queryParams("password").length() < 8 || req.queryParams("password").length() > 30) {
                    throw new InvalidFieldException(400, "Password must be between 8 and 30 characters long");
                }
                updates.add("ua.password = ?");
                params.add(BCrypt.hashpw(req.queryParams("password"), BCrypt.gensalt()));
            }

            Part filePart = req.raw().getPart("profilePic");
            Path dir = null;

            // Check if at least one optional field is present
            if (updates.isEmpty() && filePart == null) {
                throw new InvalidFieldException(200, "No changes made");
            }

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT profilepic FROM tbluserprofile WHERE acctid = ? LIMIT 1")) {
                stmt.setInt(1, req.attribute("userId"));
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    throw new InvalidFieldException(404, "User not found");
                }

                if (filePart != null) {
                    if (rs.getString("profilepic") != null) {
                        Path oldFile = Path.of(System.getenv("SERVER_RESOURCE_PATH"), rs.getString("profilepic").substring(req.scheme().length() + req.host().length()));
                        Files.deleteIfExists(oldFile);
                    }
                    String fileName = filePart.getSubmittedFileName();
                    String extension = fileName.substring(fileName.lastIndexOf("."));
                    String path = "/images/profile/" + UUID.randomUUID() + extension;
                    updates.add("up.profilepic = ?");
                    params.add(path);
                    dir = Path.of(System.getenv("SERVER_RESOURCE_PATH"), path);
                }
            }

            String updateClause = String.join(", ", updates);
            String query = "UPDATE tbluserprofile up JOIN tbluseraccount ua ON up.acctid = ua.userid SET " + updateClause + " WHERE up.acctid = ?";

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                for (int i = 0; i < params.size(); i++) {
                    stmt.setString(i + 1, params.get(i));
                }
                stmt.setInt(params.size() + 1, req.attribute("userId"));
                stmt.executeUpdate();

                if (filePart != null) {
                    // Only save the file if the database operations are successful
                    Files.createDirectories(dir.getParent());
                    try (InputStream input = filePart.getInputStream()) {
                        Files.copy(input, dir, StandardCopyOption.REPLACE_EXISTING);
                    }
                }

                return GsonData.objectToJson(new ResponseGson<>(true, "Profile updated successfully"));
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(ResponseGson.builder().status(false).message(e.getMessage()).build()));
        } catch (Exception e) {
//            e.printStackTrace();
            halt(500, GsonData.objectToJson(ResponseGson.builder().status(false).message("Something went wrong in the server").build()));
        }
        return null;
    }
}

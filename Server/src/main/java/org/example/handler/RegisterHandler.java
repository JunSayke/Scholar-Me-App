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
import java.sql.Statement;
import java.util.UUID;

import static spark.Spark.halt;

public class RegisterHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
        req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp.txt"));
        res.type("application/json");

        try {
            validateFields(req);
            checkEmailExists(req);
            return registerUser(req, res);
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(ResponseGson.builder().status(false).message(e.getMessage()).build()));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(ResponseGson.builder().status(false).message("Something went wrong in the server").build()));
        }
        return null;
    }

    private void validateFields(Request req) throws InvalidFieldException {

        Controller.validateParams(req, "email", "username", "password", "firstName", "lastName", "phoneNumber");

        // Validate fields
        if (req.queryParams("firstName").length() < 2 || req.queryParams("firstName").length() > 30) {
            throw new InvalidFieldException(400, "First name must be between 2 and 30 characters long");
        }

        if (req.queryParams("lastName").length() < 2 || req.queryParams("lastName").length() > 30) {
            throw new InvalidFieldException(400, "Last name must be between 2 and 30 characters long");
        }

        if (req.queryParams("phoneNumber").length() != 11) {
            throw new InvalidFieldException(400, "Invalid phone number: must be 11 digits long");
        }

        // https://www.javatpoint.com/java-email-validation#:~:text=To%20validate%20the%20email%20permitted,%5D%2B%24%22%20regular%20expression.
        if (!req.queryParams("email").matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}")) {
            throw new InvalidFieldException(400, "Invalid email: must be in the format of user@domain");
        }

        if (req.queryParams("password").length() < 8 || req.queryParams("password").length() > 30) {
            throw new InvalidFieldException(400, "Password must be between 8 and 30 characters long");
        }

        if (req.queryParams("username").length() < 4 || req.queryParams("username").length() > 30) {
            throw new InvalidFieldException(400, "Username must be between 4 and 30 characters long");
        }
    }

    private void checkEmailExists(Request req) throws Exception {
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tbluseraccount WHERE email = ? OR username = ?")) {
            stmt.setString(1, req.queryParams("email"));
            stmt.setString(2, req.queryParams("username"));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                throw new InvalidFieldException(409, "Email or username already taken");
            }
        }
    }

    private Object registerUser(Request req, Response res) throws Exception {
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO tbluseraccount (email, username, password) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            conn.setAutoCommit(false);

            String hashedPassword = BCrypt.hashpw(req.queryParams("password"), BCrypt.gensalt());

            stmt.setString(1, req.queryParams("email"));
            stmt.setString(2, req.queryParams("username"));
            stmt.setString(3, hashedPassword);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            if (!rs.next()) {
                throw new InvalidFieldException(400, "Failed to register user");
            }

            int userId = rs.getInt(1);
            insertUserProfile(req, conn, userId);
            uploadProfilePic(req, conn, userId);

            conn.commit();
            res.status(req.raw().getPart("profilePic") == null ? 200 : 201);
            return GsonData.objectToJson(ResponseGson.builder()
                    .status(true)
                    .message("User registered")
                    .build());
        }
    }

    private void insertUserProfile(Request req, Connection conn, int userId) throws Exception {
        try (PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO tbluserprofile (acctid, firstname, lastname, phonenumber) VALUES (?, ?, ?, ?)")) {
            stmt2.setInt(1, userId);
            stmt2.setString(2, req.queryParams("firstName"));
            stmt2.setString(3, req.queryParams("lastName"));
            stmt2.setString(4, req.queryParams("phoneNumber"));
            stmt2.executeUpdate();
        }
    }

    private void uploadProfilePic(Request req, Connection conn, int userId) throws Exception {
        Part filePart = req.raw().getPart("profilePic");
        if (filePart != null) {
            String fileName = filePart.getSubmittedFileName();
            String extension = fileName.substring(fileName.lastIndexOf("."));
            String path = "/images/profile/" + UUID.randomUUID() + extension;
            Path dir = Path.of(System.getenv("SERVER_RESOURCE_PATH"), path);

            try (InputStream input = filePart.getInputStream();
                 PreparedStatement stmt3 = conn.prepareStatement("UPDATE tbluserprofile SET profilepic = ? WHERE acctid = ?")) {
                Files.createDirectories(dir.getParent());
                Files.copy(input, dir, StandardCopyOption.REPLACE_EXISTING);

                stmt3.setString(1, path);
                stmt3.setInt(2, userId);

                if (stmt3.executeUpdate() == 0) {
                    throw new InvalidFieldException(404, "User not found");
                }
            }
        }
    }
}

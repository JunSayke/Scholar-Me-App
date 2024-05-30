package org.example.handler.flashcardset;

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

import static spark.Spark.halt;

public class CreateFlashcardSetHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            // Check if any required field is missing
            Controller.validateParams(req, "title", "description");

            // Validate fields
            if (req.queryParams("title").length() < 2 || req.queryParams("title").length() > 30) {
                throw new InvalidFieldException(400, "Title must be between 2 and 30 characters long");
            }

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO tblflashcardset (userid, title, description) VALUES (?, ?, ?)")) {
                stmt.setInt(1, req.attribute("userId"));
                stmt.setString(2, req.queryParams("title"));
                stmt.setString(3, req.queryParams("description"));
                stmt.executeUpdate();
                res.status(200);
                return GsonData.objectToJson(new ResponseGson<>(true, "Flashcard set created"));
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(ResponseGson.builder().status(false).message(e.getMessage()).build()));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(ResponseGson.builder().status(false).message("Something went wrong in the server").build()));
        }
        return null;
    }
}

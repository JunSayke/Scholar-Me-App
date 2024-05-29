package org.example.handler.flashcard.choice;

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

public class AddFlashcardChoiceHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            Controller.validateParams(req, "flashcardId", "choice", "isAnswer");

            if (req.queryParams("flashcardId").isEmpty()) {
                throw new InvalidFieldException(400, "Flashcard ID is required");
            }

            if (req.queryParams("flashcardId").matches("[^0-9]+")) {
                throw new InvalidFieldException(400, "Flashcard ID must be a number");
            }

            if (req.queryParams("choice").length() < 2 || req.queryParams("choice").length() > 255) {
                throw new InvalidFieldException(400, "Choice must be between 2 and 255 characters long");
            }

            if (req.queryParams("isAnswer").isEmpty()) {
                throw new InvalidFieldException(400, "isAnswer is required");
            }

            if (!req.queryParams("isAnswer").equals("true") && !req.queryParams("isAnswer").equals("false")) {
                throw new InvalidFieldException(400, "isAnswer must be either true or false");
            }

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO tblflashcardchoice (flashcardid, choice, isanswer) VALUES (?, ?, ?)")) {
                stmt.setInt(1, Integer.parseInt(req.queryParams("flashcardId")));
                stmt.setString(2, req.queryParams("choice"));
                stmt.setBoolean(3, Boolean.parseBoolean(req.queryParams("isAnswer")));
                stmt.executeUpdate();
            }
            res.status(200);
            return GsonData.objectToJson(new ResponseGson<>(true, "Choice added successfully"));
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(ResponseGson.builder().status(false).message(e.getMessage()).build()));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(ResponseGson.builder().status(false).message("Something went wrong in the server").build()));
        }
        return null;
    }
}

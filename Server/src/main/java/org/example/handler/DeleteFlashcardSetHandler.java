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

import static spark.Spark.halt;

public class DeleteFlashcardSetHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);
            Controller.validateParams(req, "flashcardsetid");

            if (req.queryParams("flashcardsetid").isEmpty()) {
                throw new InvalidFieldException(400, "Flashcard set ID is required");
            }

            if (req.queryParams("flashcardsetid").matches("[^0-9]+")) {
                throw new InvalidFieldException(400, "Flashcard set ID must be a number");
            }

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM tblflashcardset WHERE flashcardsetid = ? AND userid = ?")) {
                stmt.setInt(1, Integer.parseInt(req.queryParams("flashcardsetid")));
                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new InvalidFieldException(404, "Flashcard set not found");
                }

                return GsonData.objectToJson(new ResponseGson<>(true, "Flashcard set deleted"));
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(new ResponseGson<>(false, e.getMessage())));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(new ResponseGson<>(false, e.getMessage())));
        }
        return null;
    }
}

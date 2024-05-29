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

public class DeleteFlashcardChoiceHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            Controller.validateParams(req, "flashcardChoiceId");

            if (req.queryParams("flashcardChoiceId").isEmpty()) {
                throw new InvalidFieldException(400, "Flashcard choice ID is required");
            }

            if (req.queryParams("flashcardChoiceId").matches("[^0-9]+")) {
                throw new InvalidFieldException(400, "Flashcard choice ID must be a number");
            }

            // Delete flashcard choice
            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM tblflashcardchoice WHERE flashcardchoiceid = ?")) {
                stmt.setInt(1, Integer.parseInt(req.queryParams("flashcardChoiceId")));
                stmt.executeUpdate();

                if (stmt.getUpdateCount() == 0) {
                    throw new InvalidFieldException(400, "Flashcard choice not found");
                }

                res.status(200);
                return GsonData.objectToJson(new ResponseGson<>(true, "Flashcard choice deleted successfully"));
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(ResponseGson.builder().status(false).message(e.getMessage()).build()));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(ResponseGson.builder().status(false).message("Something went wrong in the server").build()));
        }
        return null;
    }
}

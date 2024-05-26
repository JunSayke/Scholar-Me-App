package org.example.handler.flashcard;

import org.example.Controller;
import org.example.data.FlashcardGson;
import org.example.data.GsonData;
import org.example.data.ResponseGson;
import org.example.exception.InvalidFieldException;
import org.example.utils.MySQLConnection;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.halt;

public class GetFlashcardSetFlashcardsHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            Controller.validateParams(req, "flashcardSetId");

            if (req.queryParams("flashcardSetId").isEmpty()) {
                throw new InvalidFieldException(400, "Flashcard set ID is required");
            }

            if (req.queryParams("flashcardSetId").matches("[^0-9]+")) {
                throw new InvalidFieldException(400, "Flashcard set ID must be a number");
            }

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT JSON_OBJECT('flashcardId', f.flashcardid, 'userId', f.userid, 'question', f.question, 'dateAdded', f.dateadded, 'dateUpdated', f.dateupdated) as flashcard FROM tblflashcardsetflashcard fsf JOIN tblflashcard f ON fsf.flashcardid = f.flashcardid WHERE fsf.flashcardsetid = ? AND f.userid = ?")) {
                stmt.setInt(1, Integer.parseInt(req.queryParams("flashcardSetId")));
                stmt.setInt(2, req.attribute("userId"));
                ResultSet rs = stmt.executeQuery();

                List<FlashcardGson> flashcards = new ArrayList<>();
                while (rs.next()) {
                    flashcards.add(GsonData.jsonToObject(rs.getString("flashcard"), FlashcardGson.class));
                }

                res.status(200);
                return GsonData.objectToJson(new ResponseGson<>(true, "Flashcards retrieved", flashcards));
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(new ResponseGson<>(false, e.getMessage())));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(new ResponseGson<>(false, "Something went wrong in the server")));
        }
        return null;
    }
}

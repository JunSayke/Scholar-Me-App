package org.example.handler.flashcard.choice;

import org.example.Controller;
import org.example.data.FlashcardChoiceGson;
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

public class GetFlashcardChoicesHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            Controller.validateParams(req, "flashcardId");

            if (req.queryParams("flashcardId").isEmpty()) {
                throw new InvalidFieldException(400, "Flashcard ID is required");
            }

            if (req.queryParams("flashcardId").matches("[^0-9]+")) {
                throw new InvalidFieldException(400, "Flashcard ID must be a number");
            }

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT JSON_OBJECT('flashcardChoiceId', flashcardchoiceid, 'flashcardId', flashcardid, 'choice', choice, 'isAnswer', IF(isanswer=1, true, false), 'dateAdded', dateadded, 'dateUpdated', dateupdated) as flashcardchoices FROM tblflashcardchoice WHERE flashcardid = ?")) {
                stmt.setInt(1, Integer.parseInt(req.queryParams("flashcardId")));
                ResultSet rs = stmt.executeQuery();

                List<FlashcardChoiceGson> flashcardChoices = new ArrayList<>();
                while (rs.next()) {
                    flashcardChoices.add(GsonData.jsonToObject(rs.getString("flashcardchoices"), FlashcardChoiceGson.class));
                }

                res.status(200);
                return GsonData.objectToJson(new ResponseGson<>(false, "Flashcard choices retrieved", flashcardChoices));
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(ResponseGson.builder().status(false).message(e.getMessage()).build()));
        } catch (Exception e) {
            e.printStackTrace();
            halt(500, GsonData.objectToJson(ResponseGson.builder().status(false).message("Something went wrong in the server").build()));
        }
        return null;
    }
}

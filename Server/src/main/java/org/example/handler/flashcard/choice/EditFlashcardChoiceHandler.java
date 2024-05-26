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
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.halt;

public class EditFlashcardChoiceHandler implements Route {
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

            List<String> updates = new ArrayList<>();
            List<String> params = new ArrayList<>();

            if (req.queryParams("choice") != null) {
                if (req.queryParams("choice").length() < 2 || req.queryParams("choice").length() > 255) {
                    throw new InvalidFieldException(400, "Choice must be between 2 and 255 characters long");
                }

                updates.add("choice = ?");
                params.add(req.queryParams("choice"));
            }

            if (req.queryParams("isAnswer") != null) {
                if (!req.queryParams("isAnswer").equals("true") && !req.queryParams("isAnswer").equals("false")) {
                    throw new InvalidFieldException(400, "isAnswer must be either true or false");
                }

                updates.add("isanswer = ?");
                params.add(req.queryParams("isAnswer"));
            }

            if (updates.isEmpty()) {
                throw new InvalidFieldException(200, "No changes made");
            }

            String updateClause = String.join(", ", updates);
            String query = "UPDATE tblflashcardchoice SET " + updateClause + " WHERE flashcardchoiceid = ?";

            try (Connection conn = MySQLConnection.getConnection();
                    PreparedStatement stmt2 = conn.prepareStatement(query)) {
                for (int i = 0; i < params.size(); i++) {
                    stmt2.setString(i + 1, params.get(i));
                }

                stmt2.setInt(params.size() + 1, Integer.parseInt(req.queryParams("flashcardChoiceId")));
                stmt2.executeUpdate();
                res.status(200);
                return GsonData.objectToJson(new ResponseGson<>(true, "Flashcard choice updated"));
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(new ResponseGson<>(false, e.getMessage())));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(new ResponseGson<>(false, "Something went wrong in the server")));
        }
        return null;
    }
}

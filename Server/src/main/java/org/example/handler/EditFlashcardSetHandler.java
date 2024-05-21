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
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.halt;

public class EditFlashcardSetHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            // Check if any required field is missing
            Controller.validateParams(req, "flashcardsetid", "title", "description");

            if (req.queryParams("flashcardsetid").isEmpty()) {
                throw new InvalidFieldException(400, "Flashcard set ID is required");
            }

            if (req.queryParams("flashcardsetid").matches("[^0-9]+")) {
                throw new InvalidFieldException(400, "Flashcard set ID must be a number");
            }

            List<String> updates = new ArrayList<>();
            List<String> params = new ArrayList<>();

            if (req.queryParams("title") != null) {
                if (req.queryParams("title").length() < 2 || req.queryParams("title").length() > 30) {
                    throw new InvalidFieldException(400, "Title must be between 2 and 30 characters long");
                }
                updates.add("title = ?");
                params.add(req.queryParams("title"));
            }

            if (req.queryParams("description") != null) {
                updates.add("description = ?");
                params.add(req.queryParams("description"));
            }

            // Check if at least one optional field is present
            if (updates.isEmpty()) {
                throw new InvalidFieldException(200, "No changes made");
            }

            String updateClause = String.join(", ", updates);
            String query = "UPDATE tblflashcardset SET " + updateClause + " WHERE flashcardsetid = ? AND userid = ?";

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                for (int i = 0; i < params.size(); i++) {
                    stmt.setString(i + 1, params.get(i));
                }

                stmt.setInt(params.size() + 1, Integer.parseInt(req.queryParams("flashcardsetid")));
                stmt.setInt(params.size() + 2, Integer.parseInt(req.attribute("userid")));
                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new InvalidFieldException(404, "Flashcard set not found");
                }

                res.status(200);
                return GsonData.objectToJson(new ResponseGson<>(true, "Flashcard set updated"));
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(new ResponseGson<>(false, e.getMessage())));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(new ResponseGson<>(false, e.getMessage())));
        }
        return null;
    }
}

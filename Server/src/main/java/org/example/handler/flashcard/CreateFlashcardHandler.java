package org.example.handler.flashcard;

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
import java.sql.ResultSet;
import java.sql.Statement;

import static spark.Spark.halt;

public class CreateFlashcardHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            Controller.validateParams(req, "flashcardSetId", "question");

            if (req.queryParams("flashcardSetId").isEmpty()) {
                throw new InvalidFieldException(400, "Flashcard set ID is required");
            }

            if (req.queryParams("flashcardSetId").matches("[^0-9]+")) {
                throw new InvalidFieldException(400, "Flashcard set ID must be a number");
            }

            if (req.queryParams("question").length() < 2 || req.queryParams("question").length() > 255) {
                throw new InvalidFieldException(400, "Question must be between 2 and 255 characters long");
            }

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO tblflashcard (userid, question) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                conn.setAutoCommit(false);
                stmt.setInt(1, req.attribute("userId"));
                stmt.setString(2, req.queryParams("question"));
                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();

                if (!rs.next()) {
                    throw new InvalidFieldException(400, "Failed to create flashcard");
                }

                int flashcardId = rs.getInt(1);
                try (PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO tblflashcardsetflashcard (flashcardsetid, flashcardid) VALUES (?, ?)")) {
                    stmt2.setInt(1, Integer.parseInt(req.queryParams("flashcardSetId")));
                    stmt2.setInt(2, flashcardId);
                    stmt2.executeUpdate();
                }

                conn.commit();
                res.status(200);
                return GsonData.objectToJson(new ResponseGson<>(true, "Flashcard created successfully"));
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

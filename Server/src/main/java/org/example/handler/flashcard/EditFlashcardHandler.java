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

import static spark.Spark.halt;

public class EditFlashcardHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);
            Controller.validateParams(req, "flashcardId");
            validateFlashcardId(req);

            try (Connection conn = MySQLConnection.getConnection()) {
                conn.setAutoCommit(false);

                try {
                    updateQuestion(req, conn);
                    updateFlashcardSet(req, conn);
                    conn.commit();
                } catch (Exception e) {
                    conn.rollback();
                    throw e;
                }
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(ResponseGson.builder().status(false).message(e.getMessage()).build()));
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(ResponseGson.builder().status(false).message("Something went wrong in the server").build()));
        }
        return null;
    }

    private void validateFlashcardId(Request req) throws InvalidFieldException {
        if (req.queryParams("flashcardId").isEmpty() || req.queryParams("flashcardId").matches("[^0-9]+")) {
            throw new InvalidFieldException(400, "Flashcard ID must be a number and is required");
        }
    }

    private void updateQuestion(Request req, Connection conn) throws Exception {
        if (req.queryParams("question") != null) {
            if (req.queryParams("question").length() < 2 || req.queryParams("question").length() > 255) {
                throw new InvalidFieldException(400, "Question must be between 2 and 255 characters long");
            }

            try (PreparedStatement stmt = conn.prepareStatement("UPDATE tblflashcard SET question = ? WHERE flashcardid = ?")) {
                stmt.setString(1, req.queryParams("question"));
                stmt.setInt(2, Integer.parseInt(req.queryParams("flashcardId")));
                if (stmt.executeUpdate() == 0) {
                    throw new InvalidFieldException(404, "Flashcard not found");
                }
            }
        }
    }

    private void updateFlashcardSet(Request req, Connection conn) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement("UPDATE tblflashcardsetflashcard SET flashcardsetid = ? WHERE flashcardid = ?")) {
            if (req.queryParams("flashcardSetId") != null) {
                if (req.queryParams("flashcardSetId").isEmpty() || req.queryParams("flashcardSetId").matches("[^0-9]+")) {
                    throw new InvalidFieldException(400, "Flashcard set ID must be a number and is required");
                }
                stmt.setInt(1, Integer.parseInt(req.queryParams("flashcardSetId")));
                stmt.setInt(2, Integer.parseInt(req.queryParams("flashcardId")));
                if (stmt.executeUpdate() == 0) {
                    throw new InvalidFieldException(404, "Flashcard not found");
                }
            }
        }
    }
}
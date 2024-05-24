package org.example.handler;

import org.example.Controller;
import org.example.data.FlashcardSetGson;
import org.example.data.GsonData;
import org.example.data.ResponseGson;
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

public class FlashcardSetsHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tblflashcardset WHERE userid = ?")) {
                stmt.setInt(1, Integer.parseInt(req.attribute("userId")));
                ResultSet rs = stmt.executeQuery();
                List<FlashcardSetGson> flashcardSets = new ArrayList<>();

                while (rs.next()) {
                    FlashcardSetGson flashcardSet = FlashcardSetGson.builder()
                            .flashcardSetId(rs.getInt("flashcardsetid"))
                            .userid(rs.getInt("userId"))
                            .title(rs.getString("title"))
                            .description(rs.getString("description"))
                            .dateAdded(rs.getTimestamp("dateadded").toLocalDateTime())
                            .dateUpdated(rs.getTimestamp("dateupdated").toLocalDateTime())
                            .build();
                    flashcardSets.add(flashcardSet);
                }

                return GsonData.objectToJson(new ResponseGson<>(true, "", flashcardSets));
            }
        } catch (Exception e) {
            halt(500, GsonData.objectToJson(new ResponseGson<>(false, "Something went wrong in the server")));
        }
        return null;
    }
}

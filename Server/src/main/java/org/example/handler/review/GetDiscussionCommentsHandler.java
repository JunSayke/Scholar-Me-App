package org.example.handler.review;

import org.example.Controller;
import org.example.data.CommentGson;
import org.example.data.GsonData;
import org.example.data.ResponseGson;
import org.example.data.UserGson;
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

public class GetDiscussionCommentsHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");

        try {
            Controller.validateAccessToken(req);

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT c.commentid, c.userid, c.comment, c.dateadded, c.dateupdated, up.firstname, up.lastname, up.profilepic FROM tbldiscussioncomment dc JOIN tblcomment c ON dc.commentid = c.commentid JOIN tbluserprofile up ON c.userid = up.userid")) {
                ResultSet rs = stmt.executeQuery();

                List<CommentGson> comments = new ArrayList<>();

                while (rs.next()) {
                    UserGson user = UserGson.builder()
                            .userId(rs.getInt("userid"))
                            .firstName(rs.getString("firstname"))
                            .lastName(rs.getString("lastname"))
                            .profilePic(rs.getString("profilepic"))
                            .build();

                    CommentGson comment = CommentGson.builder()
                            .commentId(rs.getInt("commentid"))
                            .user(user)
                            .comment(rs.getString("comment"))
                            .dateAdded(rs.getTimestamp("dateadded").toLocalDateTime())
                            .dateUpdated(rs.getTimestamp("dateupdated").toLocalDateTime())
                            .build();
                    comments.add(comment);
                }

                res.status(200);
                return GsonData.objectToJson(new ResponseGson<>(true, "Comments retrieved", comments));
            }
        } catch (InvalidFieldException e) {
            halt(e.getStatusCode(), GsonData.objectToJson(new ResponseGson<>(false, e.getMessage())));
        } catch (Exception e) {
            e.printStackTrace();
            halt(500, GsonData.objectToJson(new ResponseGson<>(false, "Something went wrong in the server")));
        }
        return null;
    }
}

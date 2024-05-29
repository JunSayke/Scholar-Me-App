package org.example.handler.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.example.Controller;
import org.example.data.GsonData;
import org.example.data.MessageGson;
import org.example.data.ResponseGson;
import org.example.data.UserGson;
import org.example.exception.InvalidFieldException;
import org.example.utils.MySQLConnection;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@WebSocket
public class ChatWebSocketHandler {

    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        URI sessionUri = session.getUpgradeRequest().getRequestURI();
        String query = sessionUri.getQuery();
        Map<String, String> params = Controller.splitQuery(query);

        sessions.add(session);
        System.err.println("Session opened: " + session.getLocalAddress() + " with query: " + params);
        System.err.println("Total sessions: " + sessions.size());
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        sessions.remove(session);
        System.err.println("Session closed: " + session.getLocalAddress() + " with statusCode: " + statusCode + " and reason: " + reason);
        System.err.println("Total sessions: " + sessions.size());
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        JSONObject json;
        try {
            json = new JSONObject(message);
        } catch (Exception e) {
            e.printStackTrace();
            session.getRemote().sendString(GsonData.objectToJson(new ResponseGson<>(false, "Invalid message format")));
            return;
        }

        try (Connection conn = MySQLConnection.getConnection()) {
            if (!json.has("userId")) {
                throw new InvalidFieldException(400, "userId cannot be empty");
            }

            if (!String.valueOf(json.get("userId")).matches("[0-9]+")) {
                throw new InvalidFieldException(400, "userId must be a number");
            }

            if (!json.has("message")) {
                throw new InvalidFieldException(400, "Message cannot be empty");
            }

            if (json.getString("message").length() < 2 || json.getString("message").length() > 255) {
                throw new InvalidFieldException(400, "Message must be between 2 and 255 characters long");
            }

            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO tblcomment (userid, comment) VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, json.getInt("userId"));
                stmt.setString(2, json.getString("message"));
                if (stmt.executeUpdate() == 0) {
                    throw new InvalidFieldException(400, "Failed to save message");
                }

                try (PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO tbldiscussioncomment (commentid) VALUES (?)", PreparedStatement.RETURN_GENERATED_KEYS)) {

                    ResultSet rs = stmt.getGeneratedKeys();

                    if (!rs.next()) {
                        throw new InvalidFieldException(400, "Failed to save message");
                    }

                    stmt2.setInt(1, rs.getInt(1));

                    if (stmt2.executeUpdate() == 0) {
                        throw new InvalidFieldException(400, "Failed to save message");
                    }

                    try (PreparedStatement stmt3 = conn.prepareStatement("SELECT dc.discussioncommentid, c.*, up.firstname, up.lastname, up.profilepic FROM tbldiscussioncomment dc JOIN tblcomment c ON dc.commentid = c.commentid JOIN tbluserprofile up ON c.userid = up.acctid WHERE dc.discussioncommentid = ? AND c.userid = ? LIMIT 1")) {

                        ResultSet rs2 = stmt2.getGeneratedKeys();

                        if (!rs2.next()) {
                            throw new InvalidFieldException(400, "Failed to save message");
                        }

                        stmt3.setInt(1, rs2.getInt(1));
                        stmt3.setInt(2, json.getInt("userId"));

                        ResultSet rs3 = stmt3.executeQuery();
                        if (!rs3.next()) {
                            throw new InvalidFieldException(400, "Failed to save message");
                        }

                        UserGson.UserGsonBuilder builder = UserGson.builder();
                        builder.userId(rs3.getInt("userid"));
                        builder.firstName(rs3.getString("firstname"));
                        builder.lastName(rs3.getString("lastname"));
                        builder.profilePic(rs3.getString("profilepic"));
                        UserGson user = builder
                                .build();

                        MessageGson messageGson = MessageGson.builder()
                                .discussionCommentId(rs3.getInt("discussioncommentid"))
                                .commendId(rs3.getInt("commentid"))
                                        .sender(user)
                                                .comment(rs3.getString("comment"))
                                                        .dateAdded(rs3.getTimestamp("dateadded").toLocalDateTime())
                                                                .dateUpdated(rs3.getTimestamp("dateupdated").toLocalDateTime())
                                                                        .build();

                        conn.commit();

                        System.err.println("Message saved: " + messageGson);

                        for (Session s : sessions) {
                            System.err.println("Message received: " + message);
                            s.getRemote().sendString(GsonData.objectToJson(ResponseGson.builder().status(true).message("Message received").code(200).data(messageGson).build()));
                        }
                    }
                }
            }
        } catch (InvalidFieldException e) {
            e.printStackTrace();
            session.getRemote().sendString(GsonData.objectToJson(ResponseGson.builder().status(false).message(e.getMessage()).code(e.getStatusCode()).build()));
        } catch (Exception e) {
            e.printStackTrace();
            session.getRemote().sendString(GsonData.objectToJson(ResponseGson.builder().status(false).message("Something went wrong in the server").code(500).build()));
        }
    }
}

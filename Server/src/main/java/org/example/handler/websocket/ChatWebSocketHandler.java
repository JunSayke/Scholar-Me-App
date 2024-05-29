package org.example.handler.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@WebSocket
public class ChatWebSocketHandler {

    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        String queryString = session.getUpgradeRequest().getRequestURI().getQuery();
        sessions.add(session);
        System.err.println("Session opened: " + session.getLocalAddress() + " with query: " + queryString);
        System.err.println("Total sessions: " + sessions.size());
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        sessions.remove(session);
        System.err.println("Session closed: " + session.getLocalAddress());
        System.err.println("Total sessions: " + sessions.size());
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        for (Session s : sessions) {
            System.err.println("Message received: " + message);
            s.getRemote().sendString("Message received: " + message);
        }
    }
}

package org.example;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.JsonSyntaxException;
import org.eclipse.jetty.websocket.api.Session;
import org.example.data.GsonData;
import org.example.exception.InvalidFieldException;
import org.example.pattern.behavioural.SQLParamsBoolean;
import org.example.pattern.behavioural.SQLParamsChainOfResponsibility;
import org.example.pattern.behavioural.SQLParamsInteger;
import org.example.pattern.behavioural.SQLParamsString;
import org.example.utils.JwtUtil;
import org.example.utils.MySQLConnection;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.routematch.RouteMatch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static spark.Spark.halt;
import static spark.Spark.routes;

public class Controller {

    public static void validateAccessToken(Request req) throws InvalidFieldException {
        String token = req.cookie("access_token");

        if (token == null) {
            throw new InvalidFieldException(401, "Missing access token");
        }

        try {
            DecodedJWT jwt = JwtUtil.verifyToken(token);
            req.attribute("userId", jwt.getClaim("userId").asInt());
            req.attribute("role", jwt.getClaim("role").asString());
        } catch (JWTVerificationException e) {
            throw new InvalidFieldException(401, "Invalid token");
        }
    }

    public static void validateParams(Request request, String ...params) throws InvalidFieldException {
        for (String param : params) {
            if (request.queryParams(param) == null) {
                throw new InvalidFieldException(400, "Missing required field: " + param);
            }
        }
    }

    public static Object serveFile(Request req, Response res) {
        Path filePath = Path.of(System.getenv("SERVER_RESOURCE_PATH"), req.pathInfo());
        System.err.println(req.pathInfo());
        if (!Files.exists(filePath)) {
            halt(404, "File not found");
            return null;
        }

        try {
            String mimeType = Files.probeContentType(filePath);
            res.type(mimeType);
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
//            e.printStackTrace();
            halt(500, "Internal Server Error: " + e.getMessage());
            return null;
        }
    }

    public static Object root(Request req, Response res) {
        List<RouteMatch> routes = Spark.routes();
        return "Available Routes: " + routes.size()
                + "\n" + routes.stream().map(route -> route.getHttpMethod() + " - " + route.getMatchUri()).collect(Collectors.joining("\n"));
    }

    public static SQLParamsChainOfResponsibility getSQLParamsChain() {
        SQLParamsString chain = new SQLParamsString();
        SQLParamsInteger chain2 = new SQLParamsInteger();
        SQLParamsBoolean chain3 = new SQLParamsBoolean();
        chain.setNext(chain2);
        chain2.setNext(chain3);
        return chain;
    }

    public static void addNotification(int userId, String title, String message) throws SQLException {
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO tblnotification (userid, title, message) VALUES (?, ?, ?)")) {
            stmt.setInt(1, userId);
            stmt.setString(2, title);
            stmt.setString(3, message);
            stmt.executeUpdate();
        }
    }

    public static Map<String, String> splitQuery(String query) {
        Map<String, String> query_pairs = new LinkedHashMap<>();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8), URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8));
            }
        }
        return query_pairs;
    }
}

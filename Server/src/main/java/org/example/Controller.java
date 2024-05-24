package org.example;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.exception.InvalidFieldException;
import org.example.utils.JwtUtil;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.routematch.RouteMatch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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
}

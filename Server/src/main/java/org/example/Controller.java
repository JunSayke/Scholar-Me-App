package org.example;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.exception.InvalidFieldException;
import org.example.utils.JwtUtil;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static spark.Spark.halt;

public class Controller {
    public static void validateAccessToken(Request req) throws InvalidFieldException {
        String token = req.cookie("access_token");
        if (token == null) {
            throw new InvalidFieldException(401, "Missing access token");
        }

        try {
            DecodedJWT jwt = JwtUtil.verifyToken(token);
            req.attribute("userid", jwt.getClaim("userid").asInt());
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
        Path filePath = Path.of("src/main/resources/", req.pathInfo());
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
}

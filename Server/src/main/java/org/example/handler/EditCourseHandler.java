package org.example.handler;

import jakarta.servlet.MultipartConfigElement;
import org.example.Controller;
import org.example.exception.InvalidFieldException;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.List;

public class EditCourseHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp.txt"));
        res.type("application/json");

        /*try {
            Controller.validateAccessToken(req);

            Controller.validateParams(req, "courseId");

            if (req.queryParams("courseId").isEmpty()) {
                throw new InvalidFieldException(400, "Course ID is required");
            }

            if (req.queryParams("courseId").matches("[^0-9]+")) {
                throw new InvalidFieldException(400, "Course ID must be a number");
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
                if (req.queryParams("description").length() < 2 || req.queryParams("description").length() > 255) {
                    throw new InvalidFieldException(400, "Description must be between 2 and 255 characters long");
                }

                updates.add("description = ?");
                params.add(req.queryParams("description"));
            }

            if (updates.isEmpty()) {
                throw new InvalidFieldException(200, "No changes made");
            }
        }*/
        return null;
    }
}

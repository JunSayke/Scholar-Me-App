package org.example.handler.course.lesson;

import spark.Request;
import spark.Response;
import spark.Route;

public class GetCourseLessonsHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        res.type("application/json");


        return null;
    }
}

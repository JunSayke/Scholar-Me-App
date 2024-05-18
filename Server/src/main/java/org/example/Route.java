package org.example;

import org.example.handler.*;

import static spark.Spark.*;

public class Route {
    private static final String API_KEY = System.getenv("API_KEY");

    public static void launch(int port) {
        port(port);

        // Security for unauthorized use of the API
        before((req, res) -> {
//            if (req.pathInfo().startsWith("/images/")) {
//                return;
//            }
            String apiToken = req.headers("Authorization");
            if (apiToken == null || !apiToken.equals(API_KEY)) {
                halt(401, "Unauthorized");
            }
        });

        // Routes
        get("/", (req, res) -> "Root");
        get("images/profile/:filename", Controller::serveFile);

        post("/user/register", new RegisterHandler());
        post("/user/login", new LoginHandler());
        post("/user/apply-creator", new ApplyCreatorHandler());

        get("/user/profile", (req, res) -> "Get profile");
        put("/user/edit-profile", (req, res) -> "Edit profile");
        put("/user/change-password", (req, res) -> "Change password");
        delete("/user/delete-profile", (req, res) -> "Delete profile");

        post("/user/create-flashcard-set", (req, res) -> "Create flashcard set");
        put("/user/edit-flashcard-set", (req, res) -> "Edit flashcard set");
        delete("/user/delete-flashcard-set", (req, res) -> "Delete flashcard set");
        get("/user/flashcard-sets", (req, res) -> "Get flashcard sets");

        post("/user/create-flashcard", (req, res) -> "Create flashcard");
        put("/user/edit-flashcard", (req, res) -> "Edit flashcard");
        delete("/user/delete-flashcard", (req, res) -> "Delete flashcard");

        post("/flashcard/add-choice", (req, res) -> "Add choice");
        put("/flashcard/edit-choice", (req, res) -> "Edit choice");
        delete("/flashcard/delete-choice", (req, res) -> "Delete choice");

        get("/user/admin/creator-applicants", new CreatorApplicantsHandler());
        post("/user/admin/approve-creator", new ApproveCreatorApplicantHandler());
        post("/user/admin/reject-creator", new RejectCreatorApplicantHandler());
        post("/user/admin/demote-creator", new DemoteCreatorHandler());

        post("/user/creator/create-course", (req, res) -> "Create course");
        put("/user/creator/edit-course", (req, res) -> "Edit course");
        delete("/user/creator/delete-course", (req, res) -> "Delete course");
        get("/user/courses", (req, res) -> "Get user enrolled courses");
        get("/user/creator/courses", (req, res) -> "Get creator created courses");

        post("/course/add-lesson", (req, res) -> "Add lesson");
        put("/course/edit-lesson", (req, res) -> "Edit lesson");
        delete("/course/delete-lesson", (req, res) -> "Delete lesson");

        post("/user/enroll-course", (req, res) -> "Enroll course");
        delete("/user/unenroll-course", (req, res) -> "Unenroll course");

        get("/user/notifications", (req, res) -> "Get notifications");
        delete("/user/notification", (req, res) -> "Delete notification");

        post("/course/add-review", (req, res) -> "Add review");

        put("/user/edit-review", (req, res) -> "Edit review");
        delete("/user/delete-review", (req, res) -> "Delete review");
        get("/user/reviews", (req, res) -> "Get user reviews");

        post("/user/add-reply", (req, res) -> "Add reply");
        put("/user/edit-reply", (req, res) -> "Edit reply");
        delete("/user/delete-reply", (req, res) -> "Delete reply");
        get("/user/replies", (req, res) -> "Get user replies");

        post("/user/favorite-course", (req, res) -> "Favorite course");
        delete("/user/unfavorite-course", (req, res) -> "Unfavorite course");
    }
}

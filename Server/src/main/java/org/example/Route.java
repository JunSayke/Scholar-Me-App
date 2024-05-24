package org.example;

import org.example.handler.*;
import spark.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static spark.Spark.*;

public class Route {
    private static final String API_KEY = System.getenv("API_KEY");

    public static void launch(int port) {
        port(port);

        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "*");
            res.header("Access-Control-Allow-Headers", "*");
        });

        // Security for unauthorized use of the API
        before((req, res) -> {
            // Exclude images from authorization
            if (req.pathInfo().matches("^/images/.+/.*$")) {
                return;
            }

            List<String> excludedRoutes = List.of("/user/profile");

            if (!excludedRoutes.contains(req.pathInfo())) {
                String apiToken = req.headers("Authorization");
                if (apiToken == null || !apiToken.equals(API_KEY)) {
                    halt(401, "Unauthorized");
                }
            }
        });
;
        // Routes
        get("/", Controller::root);
        get("images/profile/:filename", Controller::serveFile);
        get("images/course-thumbnail/:filename", Controller::serveFile);

        post("/user/register", new RegisterHandler());
        post("/user/login", new LoginHandler());
        post("/user/apply-creator", new ApplyCreatorHandler());

        get("/user/profile", new UserProfileHandler());
        put("/user/edit-profile", new EditProfileHandler());
        delete("/user/delete-account", new DeleteAccountHandler());

        post("/user/create-flashcard-set", new CreateFlashcardSetHandler());
        put("/user/edit-flashcard-set", new EditFlashcardSetHandler());
        delete("/user/delete-flashcard-set", new DeleteFlashcardSetHandler());
        get("/user/flashcard-sets", new FlashcardSetsHandler());

        post("/user/create-flashcard", new CreateFlashcardHandler());
        put("/user/edit-flashcard", new EditFlashcardHandler());
        delete("/user/delete-flashcard", new DeleteFlashcardHandler());

        post("/flashcard/add-choice", new AddFlashcardChoiceHandler());
        put("/flashcard/edit-choice", new EditFlashcardChoiceHandler());
        delete("/flashcard/delete-choice", new DeleteFlashcardChoiceHandler());

        get("/user/admin/creator-applicants", new CreatorApplicantsHandler());
        post("/user/admin/approve-creator", new ApproveCreatorApplicantHandler());
        post("/user/admin/reject-creator", new RejectCreatorApplicantHandler());
        post("/user/admin/demote-creator", new DemoteCreatorHandler());

        post("/user/creator/create-course", new CreateCourseHandler());
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
        delete("/user/delete-notification", (req, res) -> "Delete notification");

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

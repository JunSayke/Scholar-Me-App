package org.example;

import org.example.handler.*;
import org.example.handler.course.*;
import org.example.handler.course.lesson.AddCourseLesson;
import org.example.handler.flashcard.GetFlashcardSetFlashcardsHandler;
import org.example.handler.flashcard.choice.AddFlashcardChoiceHandler;
import org.example.handler.flashcard.choice.DeleteFlashcardChoiceHandler;
import org.example.handler.flashcard.choice.EditFlashcardChoiceHandler;
import org.example.handler.flashcard.choice.GetFlashcardChoicesHandler;
import org.example.handler.flashcardset.CreateFlashcardSetHandler;
import org.example.handler.flashcardset.DeleteFlashcardSetHandler;
import org.example.handler.flashcardset.EditFlashcardSetHandler;
import org.example.handler.flashcardset.GetFlashcardSetsHandler;
import org.example.handler.flashcard.CreateFlashcardHandler;
import org.example.handler.flashcard.DeleteFlashcardHandler;
import org.example.handler.flashcard.EditFlashcardHandler;

import java.util.List;

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

        get("/user/profile", new GetUserProfileHandler());
        put("/user/edit-profile", new EditProfileHandler());
        delete("/user/delete-account", new DeleteAccountHandler());

        post("/user/create-flashcard-set", new CreateFlashcardSetHandler());
        put("/user/edit-flashcard-set", new EditFlashcardSetHandler());
        delete("/user/delete-flashcard-set", new DeleteFlashcardSetHandler());
        get("/user/flashcard-sets", new GetFlashcardSetsHandler());

        post("/user/create-flashcard", new CreateFlashcardHandler());
        put("/user/edit-flashcard", new EditFlashcardHandler());
        delete("/user/delete-flashcard", new DeleteFlashcardHandler());
        get("/user/flashcard-set-flashcards", new GetFlashcardSetFlashcardsHandler());

        post("/flashcard/add-choice", new AddFlashcardChoiceHandler());
        put("/flashcard/edit-choice", new EditFlashcardChoiceHandler());
        delete("/flashcard/delete-choice", new DeleteFlashcardChoiceHandler());
        get("/flashcard/choices", new GetFlashcardChoicesHandler());

        get("/user/admin/creator-applicants", new GetCreatorApplicantsHandler());
        post("/user/admin/approve-creator", new ApproveCreatorApplicantHandler());
        post("/user/admin/reject-creator", new RejectCreatorApplicantHandler());
        post("/user/admin/demote-creator", new DemoteCreatorHandler());

        post("/user/creator/create-course", new CreateCourseHandler());
        put("/user/creator/edit-course", new EditCourseHandler());
        delete("/user/creator/delete-course", new DeleteCourseHandler());
        get("/user/courses", new GetUserCoursesHandler());
        get("/user/creator/courses", new GetCreatorCoursesHandler());

        post("/course/add-lesson", new AddCourseLesson());
        put("/course/edit-lesson", new EditCourseHandler());
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

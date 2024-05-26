package org.example;

import org.example.handler.*;
import org.example.handler.course.*;
import org.example.handler.course.lesson.AddCourseLessonHandler;
import org.example.handler.course.lesson.DeleteCourseLessonHandler;
import org.example.handler.course.lesson.EditCourseLessonHandler;
import org.example.handler.course.lesson.GetCourseLessonsHandler;
import org.example.handler.course.review.AddCourseReviewHandler;
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
import org.example.handler.reply.AddReplyHandler;
import org.example.handler.reply.DeleteReplyHandler;
import org.example.handler.reply.EditReplyHandler;
import org.example.handler.reply.GetUserRepliesHandler;
import org.example.handler.review.DeleteReviewHandler;
import org.example.handler.review.EditReviewHandler;
import org.example.handler.review.GetUserReviewsHandler;

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
        get("/flashcard-set/flashcards", new GetFlashcardSetFlashcardsHandler());

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

        get("/courses", new GetCoursesHandler());
        get("/course/lessons", new GetCourseLessonsHandler());

        post("/course/add-lesson", new AddCourseLessonHandler());
        put("/course/edit-lesson", new EditCourseLessonHandler());
        delete("/course/delete-lesson", new DeleteCourseLessonHandler());

        post("/user/enroll-course", new EnrollCourseHandler());
        delete("/user/unenroll-course", new UnenrollCourseHandler());

        get("/user/notifications", new GetUserNotificationsHandler());
        delete("/user/delete-notification", new DeleteUserNotification());

        post("/course/add-review", new AddCourseReviewHandler());

        put("/user/edit-review", new EditReviewHandler());
        delete("/user/delete-review", new DeleteReviewHandler());
        get("/user/reviews", new GetUserReviewsHandler());

        post("/user/add-reply", new AddReplyHandler());
        put("/user/edit-reply", new EditReplyHandler());
        delete("/user/delete-reply", new DeleteReplyHandler());
        get("/user/replies", new GetUserRepliesHandler());

        post("/user/mark-favorite-course", new MarkFavoriteCourseHandler());
        delete("/user/unmark-favorite-course", new UnMarkFavoriteCourseHandler());
    }
}

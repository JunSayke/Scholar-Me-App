package com.example.solutionsproject.classes.retrofit;

import androidx.annotation.Nullable;

import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.model.gson.data.ApplicantsGson;
import com.example.solutionsproject.model.gson.data.CommentGson;
import com.example.solutionsproject.model.gson.data.CourseGson;
import com.example.solutionsproject.model.gson.data.FlashcardChoiceGson;
import com.example.solutionsproject.model.gson.data.FlashcardGson;
import com.example.solutionsproject.model.gson.data.FlashcardSetGson;
import com.example.solutionsproject.model.gson.data.GsonData;
import com.example.solutionsproject.model.gson.data.LessonGson;
import com.example.solutionsproject.model.gson.data.NotificationGson;
import com.example.solutionsproject.model.gson.data.UserGson;
import com.example.solutionsproject.model.gson.data.response.SuccessGson;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface RetrofitService {

    @FormUrlEncoded
    @Headers({"Authorization: " + MainFacade.API_KEY})
    @POST("user/login")
    Call<SuccessGson<UserGson>> login(
            @Field("email") String email,
            @Field("password") String password
    );
    @Multipart
    @Headers({"Authorization: " + MainFacade.API_KEY})
    @POST("user/register")
    Call<SuccessGson<GsonData>> register(
            @Part @Nullable MultipartBody.Part profilePic,
            @PartMap Map<String, RequestBody> fields
    );

    @Multipart
    @Headers({"Authorization: " + MainFacade.API_KEY})
    @PUT("user/edit-profile")
    Call<SuccessGson<GsonData>> updateProfile(
            @Part @Nullable MultipartBody.Part thumbnail,
            @PartMap Map<String, RequestBody> fields
    );

    // -- ROLE FUNCTIONS
    @Headers({"Authorization: " + MainFacade.API_KEY})
    @POST("/user/apply-creator")
    Call<SuccessGson<GsonData>> applyCreator();

    @Headers({"Authorization: " + MainFacade.API_KEY})
    @GET("/user/admin/creator-applicants")
    Call<SuccessGson<List<ApplicantsGson>>> getApplicants();

    @FormUrlEncoded
    @Headers({"Authorization: " + MainFacade.API_KEY})
    @POST("/user/admin/approve-creator")
    Call<SuccessGson<GsonData>> approveCreator(
            @Field("creatorapplicantid") String creatorApplicantId
    );

    @FormUrlEncoded
    @Headers({"Authorization: " + MainFacade.API_KEY})
    @POST("/user/admin/reject-creator")
    Call<SuccessGson<GsonData>> rejectCreator(
            @Field("creatorapplicantid") String creatorApplicantId
    );

    // -- COURSE CRUD

    @Multipart
    @Headers({"Authorization: " + MainFacade.API_KEY})
    @POST("/user/creator/create-course")
    Call<SuccessGson<GsonData>> createCourse(
        @Part @Nullable MultipartBody.Part thumbnail,
        @PartMap Map<String, RequestBody> fields
    );

    @Multipart
    @Headers({"Authorization: " + MainFacade.API_KEY})
    @PUT("/user/creator/edit-course")
    Call<SuccessGson<GsonData>> updateCourse(
            @Part @Nullable MultipartBody.Part thumbnail,
            @PartMap Map<String, RequestBody> fields
    );

    @Headers({"Authorization: " + MainFacade.API_KEY})
    @GET("/user/creator/courses")
    Call<SuccessGson<List<CourseGson>>> getCreatorCourses();

    @Headers({"Authorization: " + MainFacade.API_KEY})
    @GET("/user/courses")
    Call<SuccessGson<List<CourseGson>>> getUserCourses();

    @Headers({"Authorization: " + MainFacade.API_KEY})
    @GET("/courses")
    Call<SuccessGson<List<CourseGson>>> getCourses();

    // -- LESSON CRUD
    @FormUrlEncoded
    @Headers({"Authorization: " + MainFacade.API_KEY})
    @POST("/course/add-lesson")
    Call<SuccessGson<GsonData>> createLesson(
            @Query("courseId") int courseId,
            @Field("title") String title,
            @Field("lessonNumber") String lessonNumber,
            @Field("description") String description,
            @Field("content") String content,
            @Field("duration") String duration
    );

    @Headers({"Authorization: " + MainFacade.API_KEY})
    @GET("/course/lessons")
    Call<SuccessGson<List<LessonGson>>> getCourseLesson(
            @Query("courseId") int courseId
    );

    // -- FLASHCARDS CRUD

    @Headers({"Authorization: " + MainFacade.API_KEY})
    @FormUrlEncoded
    @POST("/user/create-flashcard-set")
    Call<SuccessGson<GsonData>> createFlashcardSet(
            @Field("title") String title,
            @Field("description") String description
    );

    @Headers({"Authorization: " + MainFacade.API_KEY})
    @FormUrlEncoded
    @PUT("/user/edit-flashcard-set")
    Call<SuccessGson<GsonData>> editFlashcardSet(
            @Field("flashcardSetId") int flashcardSetId,
            @Nullable @Field("title") String title,
            @Nullable @Field("description") String description
    );

    @Headers({"Authorization: " + MainFacade.API_KEY})
    @DELETE("/user/delete-flashcard-set")
    Call<SuccessGson<GsonData>> deleteFlashcardSet(
            @Query("flashcardSetId") int flashcardSetId
    );

    @Headers({"Authorization: " + MainFacade.API_KEY})
    @GET("/user/flashcard-sets")
    Call<SuccessGson<List<FlashcardSetGson>>> getFlashcardSets();

    @Headers({"Authorization: " + MainFacade.API_KEY})
    @FormUrlEncoded
    @POST("/user/create-flashcard")
    Call<SuccessGson<GsonData>> createFlashcard(
            @Field("flashcardSetId") int flashcardSetId,
            @Field("question") String question
    );

    @Headers({"Authorization: " + MainFacade.API_KEY})
    @GET("/flashcard-set/flashcards")
    Call<SuccessGson<List<FlashcardGson>>> getFlashcardSetFlashcards(
            @Query("flashcardSetId") int flashcardSetId
    );

    @Headers({"Authorization: " + MainFacade.API_KEY})
    @GET("/user/notifications")
    Call<SuccessGson<List<NotificationGson>>> getUserNotifications();

    @Headers({"Authorization: " + MainFacade.API_KEY})
    @FormUrlEncoded
    @POST("/flashcard/add-choice")
    Call<SuccessGson<GsonData>> addFlashcardChoice(
            @Field("flashcardId") int flashcardId,
            @Field("choice") String choice,
            @Field("isAnswer") boolean isAnswer
    );

    @Headers({"Authorization: " + MainFacade.API_KEY})
    @GET("/flashcard/choices")
    Call<SuccessGson<List<FlashcardChoiceGson>>> getFlashcardChoices(
            @Query("flashcardId") int flashcardId
    );

    @Headers({"Authorization: " + MainFacade.API_KEY})
    @FormUrlEncoded
    @POST("/discussion/add-comment")
    Call<SuccessGson<GsonData>> addDiscussionComment(
            @Field("comment") String comment
    );

    @Headers({"Authorization: " + MainFacade.API_KEY})
    @GET("/discussion/get-comments")
    Call<SuccessGson<List<CommentGson>>> getDiscussionComments();
}

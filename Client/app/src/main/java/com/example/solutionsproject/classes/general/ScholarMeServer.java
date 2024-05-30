package com.example.solutionsproject.classes.general;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.solutionsproject.classes.retrofit.RetrofitFacade;
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
import com.example.solutionsproject.model.gson.data.response.ErrorGson;
import com.example.solutionsproject.model.gson.data.response.ResponseGson;
import com.example.solutionsproject.model.gson.data.response.SuccessGson;
import com.google.gson.Gson;

import java.io.File;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScholarMeServer extends RetrofitFacade {
    private static final String TAG = "ScholarMeServer";
    private static String ipAddress;

    static {
//        try {
//            MainFacade mainFacade = MainFacade.getInstance();
//            ipAddress = mainFacade.getIpAddress() + ":" + mainFacade.getServerPort();
//        } catch (Exception e) {
//            ipAddress = "10.0.2.2:6969";
//            throw new RuntimeException(e);
//        }
        ipAddress = "10.0.2.2:6969";
    }

    public ScholarMeServer(){
        super("http://" + ipAddress);
    }

    public void login(
            final Callback<SuccessGson<UserGson>> callback,
            final String user,
            final String password
    ){
        getRetrofitService().login(user, password).enqueue(callback);
    }

    public void register(
        final Callback<SuccessGson<GsonData>> callback,
        @Nullable final File profileImage,
        final String email,
        final String firstname,
        final String lastname,
        final String username,
        final String password,
        final String phoneno
    ) {
        MultipartBody.Part imagePart = null;
        if(profileImage != null){
            RequestBody requestBody = RequestBody.create(profileImage, MediaType.parse(URLConnection.guessContentTypeFromName(profileImage.getName())));
            imagePart = MultipartBody.Part.createFormData("profilePic", profileImage.getName(), requestBody);
        }

        Map<String, RequestBody> fields = new HashMap<>();
        fields.put("email", RequestBody.create(email, MediaType.parse("text/plain")));
        fields.put("firstName", RequestBody.create(firstname, MediaType.parse("text/plain")));
        fields.put("lastName", RequestBody.create(lastname, MediaType.parse("text/plain")));
        fields.put("username", RequestBody.create(username, MediaType.parse("text/plain")));
        fields.put("password", RequestBody.create(password, MediaType.parse("text/plain")));
        fields.put("phoneNumber", RequestBody.create(phoneno, MediaType.parse("text/plain")));

        getRetrofitService().register(imagePart, fields).enqueue(callback);
    }

    public void updateProfile(
            final Callback<SuccessGson<GsonData>> callback,
            @Nullable final File profileImage,
            @Nullable final String email,
            @Nullable final String firstname,
            @Nullable final String lastname,
            @Nullable final String password,
            @Nullable final String phoneno
    ){
        MultipartBody.Part imagePart = null;
        if(profileImage != null){
            RequestBody requestBody = RequestBody.create(profileImage, MediaType.parse(URLConnection.guessContentTypeFromName(profileImage.getName())));
            imagePart = MultipartBody.Part.createFormData("profilePic", profileImage.getName(), requestBody);
        }

        Map<String, RequestBody> fields = new HashMap<>();
        if (email != null) {
            fields.put("email", RequestBody.create(email, MediaType.parse("text/plain")));
        }
        if (firstname != null) {
            fields.put("firstName", RequestBody.create(firstname, MediaType.parse("text/plain")));
        }
        if (lastname != null) {
            fields.put("lastName", RequestBody.create(lastname, MediaType.parse("text/plain")));
        }
        if (password != null) {
            fields.put("password", RequestBody.create(password, MediaType.parse("text/plain")));
        }
        if (phoneno != null) {
            fields.put("phoneNumber", RequestBody.create(phoneno, MediaType.parse("text/plain")));
        }

        //Log.d(TAG, fields.toString());

        getRetrofitService().updateProfile(imagePart, fields).enqueue(callback);
    }

    public void getUserProfile(
            final Callback<SuccessGson<UserGson>> callback,
            final int userId
    ) {
        getRetrofitService().getUserProfile(userId).enqueue(callback);
    }

    // -- ROLE FUNCTIONS
    public  void applyCreator(final Callback<SuccessGson<GsonData>> callback){
        getRetrofitService().applyCreator().enqueue(callback);
    }

    public void getApplicants(final Callback<SuccessGson<List<ApplicantsGson>>> callback){
        getRetrofitService().getApplicants().enqueue(callback);
    }

    public void approveCreator(
            final Callback<SuccessGson<GsonData>> callback,
            final String creatorApplicantId
    ){
        getRetrofitService().approveCreator(creatorApplicantId).enqueue(callback);
    }

    public void rejectCreator(
            final Callback<SuccessGson<GsonData>> callback,
            final String creatorApplicantId
    ){
        getRetrofitService().rejectCreator(creatorApplicantId).enqueue(callback);
    }

    // -- COURSE CRUD

    public void createCourse(
            final Callback<SuccessGson<GsonData>> callback,
            @Nullable final File thumbnail,
            final String title,
            final String description
    ) {
        MultipartBody.Part imagePart = null;
        if(thumbnail != null){
            RequestBody requestBody = RequestBody.create(thumbnail, MediaType.parse(URLConnection.guessContentTypeFromName(thumbnail.getName())));
            imagePart = MultipartBody.Part.createFormData("thumbnail", thumbnail.getName(), requestBody);
        }

        Map<String, RequestBody> fields = new HashMap<>();
        fields.put("title", RequestBody.create(title, MediaType.parse("text/plain")));
        fields.put("description", RequestBody.create(description, MediaType.parse("text/plain")));

        getRetrofitService().createCourse(imagePart, fields).enqueue(callback);
    }

    public void updateCourse(
            final Callback<SuccessGson<GsonData>> callback,
            final int courseId,
            @Nullable final File thumbnail,
            @Nullable final String title,
            @Nullable final String description
    ){
        MultipartBody.Part imagePart = null;
        if(thumbnail != null){
            RequestBody requestBody = RequestBody.create(thumbnail, MediaType.parse(URLConnection.guessContentTypeFromName(thumbnail.getName())));
            imagePart = MultipartBody.Part.createFormData("thumbnail", thumbnail.getName(), requestBody);
        }

        Map<String, RequestBody> fields = new HashMap<>();
        if (title != null) {
            fields.put("title", RequestBody.create(title, MediaType.parse("text/plain")));
        }
        if (description != null) {
            fields.put("description", RequestBody.create(description, MediaType.parse("text/plain")));
        }

        getRetrofitService().updateCourse(imagePart, courseId, fields).enqueue(callback);
    }

    public void deleteCourse(
            final Callback<SuccessGson<GsonData>> callback,
            final int courseId
    ) {
        getRetrofitService().deleteCourse(courseId).enqueue(callback);
    }

    public void getCreatorCourses(
            final Callback<SuccessGson<List<CourseGson>>> callback
    ){
        getRetrofitService().getCreatorCourses().enqueue(callback);
    }

    public void getUserCourses(
            final Callback<SuccessGson<List<CourseGson>>> callback
    ){
        getRetrofitService().getUserCourses().enqueue(callback);
    }

    public void getUserCourseFavorites(
            final Callback<SuccessGson<List<CourseGson>>> callback
    ){
        getRetrofitService().getUserCourseFavorites().enqueue(callback);
    }

    public void getCourses(
            final Callback<SuccessGson<List<CourseGson>>> callback
    ){
        getRetrofitService().getCourses().enqueue(callback);
    }

    public void enrollCourse(
            final Callback<SuccessGson<GsonData>> callback,
            final int courseId
    ) {
        getRetrofitService().enrollCourse(courseId).enqueue(callback);
    }

    public void unenrollCourse(
            final Callback<SuccessGson<GsonData>> callback,
            final int courseId
    ) {
        getRetrofitService().unenrollCourse(courseId).enqueue(callback);
    }

    public void markFavoriteCourse(
            final Callback<SuccessGson<GsonData>> callback,
            final int courseId
    ) {
        getRetrofitService().markFavoriteCourse(courseId).enqueue(callback);
    }

    public void unmarkFavoriteCourse(
            final Callback<SuccessGson<GsonData>> callback,
            final int courseId
    ) {
        getRetrofitService().unmarkFavoriteCourse(courseId).enqueue(callback);
    }

    // -- LESSON CRUD
    public void createLesson(
            final Callback<SuccessGson<GsonData>> callback,
            final int courseId,
            final String title,
            final String lessonNumber,
            @Nullable final String description,
            final String content,
            final String duration
    ) {
        getRetrofitService().createLesson(courseId, title, lessonNumber, description, content, duration).enqueue(callback);
    }

    public void getCourseLesson(
            final Callback<SuccessGson<List<LessonGson>>> callback,
            final int courseId
    ){
        getRetrofitService().getCourseLesson(courseId).enqueue(callback);
    }

    public void updateLesson(
            final Callback<SuccessGson<GsonData>> callback,
            final int lessonId,
            @Nullable final String title,
            @Nullable final String lessonNumber,
            @Nullable final String description,
            @Nullable final String content,
            @Nullable final String duration
    ) {
        getRetrofitService().updateLesson(lessonId, title, lessonNumber, description, content, duration).enqueue(callback);
    }

    public void deleteLesson(
            final Callback<SuccessGson<GsonData>> callback,
            final int courseLessonId
    ) {
        getRetrofitService().deleteLesson(courseLessonId).enqueue(callback);
    }

    // -- FLASHCARD CRUD

    public void createFlashcardSet(
            final Callback<SuccessGson<GsonData>> callback,
            final String title,
            final String description
    ){
        getRetrofitService().createFlashcardSet(title, description).enqueue(callback);
    }

    public void editFlashcardSet(
            final Callback<SuccessGson<GsonData>> callback,
            final int flashcardSetId,
            @Nullable final String title,
            @Nullable final String description
    ){
        getRetrofitService().editFlashcardSet(flashcardSetId, title, description).enqueue(callback);
    }

    public void deleteFlashcardSet(
            final Callback<SuccessGson<GsonData>> callback,
            final int flashcardSetId
    ){
        getRetrofitService().deleteFlashcardSet(flashcardSetId).enqueue(callback);
    }

    public void getFlashcardSets(
            final Callback<SuccessGson<List<FlashcardSetGson>>> callback
    ){
        getRetrofitService().getFlashcardSets().enqueue(callback);
    }

    public void createFlashcard(
            final Callback<SuccessGson<GsonData>> callback,
            final int flashcardSetId,
            final String question
    ){
        getRetrofitService().createFlashcard(flashcardSetId, question).enqueue(callback);
    }

    public void deleteFlashcard(
            final Callback<SuccessGson<GsonData>> callback,
            final int flashcardId
    ){
        getRetrofitService().deleteFlashcard(flashcardId).enqueue(callback);
    }

    public void getFlashcardSetFlashcards(
            final Callback<SuccessGson<List<FlashcardGson>>> callback,
            final int flashcardSetId
    ){
        getRetrofitService().getFlashcardSetFlashcards(flashcardSetId).enqueue(callback);
    }

    public void addFlashcardChoice(
            final Callback<SuccessGson<GsonData>> callback,
            final int flashcardId,
            final String choice,
            final boolean isAnswer
    ){
        getRetrofitService().addFlashcardChoice(flashcardId, choice, isAnswer).enqueue(callback);
    }

    public void deleteFlashcardChoice(
            final Callback<SuccessGson<GsonData>> callback,
            final int flashcardChoiceId
    ){
        getRetrofitService().deleteFlashcardChoice(flashcardChoiceId).enqueue(callback);
    }

    public void getFlashcardChoices(
            final Callback<SuccessGson<List<FlashcardChoiceGson>>> callback,
            final int flashcardId
    ){
        getRetrofitService().getFlashcardChoices(flashcardId).enqueue(callback);
    }

    // -- NOTIFICATIONS
    public void getUserNotifications(
            final Callback<SuccessGson<List<NotificationGson>>> callback
    ){
        getRetrofitService().getUserNotifications().enqueue(callback);
    }

    public void deleteNotification(
            final Callback<SuccessGson<GsonData>> callback,
            final int notificationId
    ){
        getRetrofitService().deleteNotification(notificationId).enqueue(callback);
    }

    public void getDiscussionComments(
            final Callback<SuccessGson<List<CommentGson>>> callback
    ){
        getRetrofitService().getDiscussionComments().enqueue(callback);
    }

    // -- END OF SERVER FUNC


    public void addCookies(Set<String> cookies) {
        RetrofitFacade.cookies.addAll(cookies);
    }

    public void removeCookies(Set<String> cookies) {
        RetrofitFacade.cookies.removeAll(cookies);
    }

    public Set<String> getCookies() {
        return cookies;
    }

    public interface ResponseListener<T> {
        void onSuccess(T data);

        void onFailure(String message);
    }

    public static <T1> Callback<SuccessGson<T1>> getCallback(ResponseListener<T1> responseListener) {
        return new Callback<SuccessGson<T1>>() {

            @Override
            public void onResponse(@NonNull Call<SuccessGson<T1>> call, @NonNull Response<SuccessGson<T1>> response) {
                ResponseGson<?> body = null;
                try{
                    if(response.isSuccessful()){
                        assert response.body() != null;
                        body = response.body();

                        @SuppressWarnings("unchecked")
                        T1 data = (T1) ((SuccessGson<?>) body).getData();
                        responseListener.onSuccess(data);
                    }else{
                        assert response.errorBody() != null;
                        body = new Gson().fromJson(response.errorBody().string(), ErrorGson.class);
                    }
                }catch (Exception e){
                    Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                } finally {
                    String message = "Null Response!";
                    Log.d(TAG, String.valueOf(body));
                    if (body == null || body instanceof ErrorGson || !body.getStatus()){
                        if(body != null)
                            message = body.getMessage();
                        responseListener.onFailure(message);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<SuccessGson<T1>> call, @NonNull Throwable t) {
                Log.e(TAG, "Network Error! " + t.getMessage());
                responseListener.onFailure(t.getMessage());
            }
        };
    }
}

package com.example.solutionsproject.classes.general;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.InetAddresses;
import android.net.wifi.WifiManager;
import android.os.Looper;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.solutionsproject.R;
import com.example.solutionsproject.activities.MainActivity;
import com.example.solutionsproject.databinding.ActivityMainBinding;
import com.example.solutionsproject.model.data.livedata.UserGsonViewModel;
import com.example.solutionsproject.model.data.livedata.UserGsonViewModelFactory;
import com.example.solutionsproject.model.gson.data.ApplicantsGson;
import com.example.solutionsproject.model.gson.data.CourseGson;
import com.example.solutionsproject.model.gson.data.FlashcardChoiceGson;
import com.example.solutionsproject.model.gson.data.FlashcardGson;
import com.example.solutionsproject.model.gson.data.FlashcardSetGson;
import com.example.solutionsproject.model.gson.data.GsonData;
import com.example.solutionsproject.model.gson.data.LessonGson;
import com.example.solutionsproject.model.gson.data.NotificationGson;
import com.example.solutionsproject.model.gson.data.UserGson;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;

import lombok.Getter;
import lombok.Setter;

@Getter
public class MainFacade {

    private final String IpAddress = "192.168.1.11";
    private final String serverPort = "6969";

    public static final String TAG = "Main Facade";
    private static final MainFacade mainFacade = new MainFacade(null);
    private ScholarMeServer server;
    private static FragmentActivity mainActivity;
    @Setter
    private ActivityMainBinding mainBinding;
    @Setter
    private NavController currentNavController, mainNavController, openingNavController, homepageNavController,
            homeNavController, coursesNavController, messagesNavController, accountNavController;

    private UserGsonViewModelFactory userGsonViewModelFactory;
    private SessionManager sessionManager;

    public static final String PREF_NAME = "PomodoroPrefs";
    public static final String KEY_WORK_TIME = "work_time_minutes";
    public static final String KEY_BREAK_TIME = "break_time_minutes";
    public static final String KEY_LONG_BREAK_TIME = "long_break_time_minutes";
    public static final String KEY_BREAK_INTERVAL = "break_time_interval";
    public static final String KEY_VOLUME_LEVEL = "volume_level";

    private MainFacade(@Nullable MainActivity mainActivity){
        if(mainActivity != null) setMainActivity(mainActivity);
    }

    public static synchronized MainFacade getInstance(MainActivity mainActivity){
        mainFacade.setMainActivity(mainActivity);
        return mainFacade;
    }

    public static synchronized MainFacade getInstance() throws Exception{
        if(mainActivity == null){
            throw new Exception("Main Activity has not been instantiated.");
        }
        return mainFacade;
    }

    public FragmentActivity getMainActivity() { return mainActivity; }

    public void setMainActivity(FragmentActivity mainActivity) {
        MainFacade.mainActivity = mainActivity;
        sessionManager = new SessionManager(mainActivity.getApplicationContext());
        userGsonViewModelFactory = new UserGsonViewModelFactory(mainActivity.getApplicationContext());
        //TODO: other server related stuff
        server = new ScholarMeServer();

        Set<String> PREF_COOKIES = sessionManager.getCookies();
        if (!PREF_COOKIES.isEmpty()) {
            server.addCookies(PREF_COOKIES);
        }
    }

    // -- START OF SESSION FUNCTIONS --
    public UserGsonViewModel getUserGsonViewModel() {
        return new ViewModelProvider(mainActivity, userGsonViewModelFactory).get(UserGsonViewModel.class);
    }
    public void startLoginSession(UserGson userGson) {
        Set<String> PREF_COOKIES = server.getCookies();
        server.addCookies(PREF_COOKIES);
        sessionManager.startSession(userGson, PREF_COOKIES);
    }

    public void stopLoginSession() {
        server.removeCookies(sessionManager.getCookies());
        sessionManager.stopSession();
    }

    public boolean isLoggedIn() {
        return sessionManager.getUserGson() != null;
    }

    // -- START OF SERVER FUNCTIONS --
    public void login(
            final ScholarMeServer.ResponseListener<UserGson> responseListener,
            final String user,
            final String password
    ) {
        server.login(ScholarMeServer.getCallback(responseListener), user, password);
    }
    public void register(
        final ScholarMeServer.ResponseListener<GsonData> responseListener,
        final File profileImage,
        final String email,
        final String firstname,
        final String lastname,
        final String username,
        final String password,
        final String phoneno
    ) {
        server.register(ScholarMeServer.getCallback(responseListener), profileImage, email, firstname, lastname, username, password, phoneno);
    }

    public void updateProfile(
            final ScholarMeServer.ResponseListener<GsonData> responseListener,
            @Nullable final File profileImage,
            @Nullable final String email,
            @Nullable final String firstname,
            @Nullable final String lastname,
            @Nullable final String password,
            @Nullable final String phoneno
    ) {
        server.updateProfile(ScholarMeServer.getCallback(responseListener), profileImage, email, firstname, lastname, password, phoneno);
    }

    // -- ROLE FUNCTIONS
    public void applyCreator(final ScholarMeServer.ResponseListener<GsonData> responseListener){
        server.applyCreator(ScholarMeServer.getCallback(responseListener));
    }

    public void getApplicants(final ScholarMeServer.ResponseListener<List<ApplicantsGson>> responseListener){
        server.getApplicants(ScholarMeServer.getCallback(responseListener));
    }

    public void approveCreator(
            final ScholarMeServer.ResponseListener<GsonData> responseListener,
            final String creatorApplicantId
    ){
        server.approveCreator(ScholarMeServer.getCallback(responseListener), creatorApplicantId);
    }

    public void rejectCreator(
            final ScholarMeServer.ResponseListener<GsonData> responseListener,
            final String creatorApplicantId
    ){
        server.rejectCreator(ScholarMeServer.getCallback(responseListener), creatorApplicantId);
    }

    // -- COURSE CRUD
    public void createCourse(
            final ScholarMeServer.ResponseListener<GsonData> responseListener,
            final File thumbnail,
            final String title,
            final String description
    ) {
        server.createCourse(ScholarMeServer.getCallback(responseListener), thumbnail, title, description);
    }

    public void updateCourse(
            final ScholarMeServer.ResponseListener<GsonData> responseListener,
            final int courseId,
            @Nullable final File thumbnail,
            @Nullable final String title,
            @Nullable final String description
    ){
        server.updateCourse(ScholarMeServer.getCallback(responseListener), courseId, thumbnail, title, description);
    }

    public void deleteCourse(
            final ScholarMeServer.ResponseListener<GsonData> responseListener,
            final int courseId
    ) {
        server.deleteCourse(ScholarMeServer.getCallback(responseListener), courseId);
    }

    public void getCreatorCourses(
            final ScholarMeServer.ResponseListener<List<CourseGson>> responseListener
    ){
        server.getCreatorCourses(ScholarMeServer.getCallback(responseListener));
    }

    public void getUserCourses(
            final ScholarMeServer.ResponseListener<List<CourseGson>> responseListener
    ){
        server.getUserCourses(ScholarMeServer.getCallback(responseListener));
    }

    public void getUserCourseFavorites(
            final ScholarMeServer.ResponseListener<List<CourseGson>> responseListener
    ){
        server.getUserCourseFavorites(ScholarMeServer.getCallback(responseListener));
    }

    public void getCourses(
            final ScholarMeServer.ResponseListener<List<CourseGson>> responseListener
    ){
        server.getCourses(ScholarMeServer.getCallback(responseListener));
    }

    public void enrollCourse(
            final ScholarMeServer.ResponseListener<GsonData> responseListener,
            final int courseId
    ) {
        server.enrollCourse(ScholarMeServer.getCallback(responseListener), courseId);
    }

    public void unenrollCourse(
            final ScholarMeServer.ResponseListener<GsonData> responseListener,
            final int courseId
    ) {
        server.unenrollCourse(ScholarMeServer.getCallback(responseListener), courseId);
    }

    public void markFavoriteCourse(
            final ScholarMeServer.ResponseListener<GsonData> responseListener,
            final int courseId
    ) {
        server.markFavoriteCourse(ScholarMeServer.getCallback(responseListener), courseId);
    }

    public void unmarkFavoriteCourse(
            final ScholarMeServer.ResponseListener<GsonData> responseListener,
            final int courseId
    ) {
        server.unmarkFavoriteCourse(ScholarMeServer.getCallback(responseListener), courseId);
    }

    // -- LESSON CRUD

    public void createLesson(
            final ScholarMeServer.ResponseListener<GsonData> responseListener,
            final int courseId,
            final String title,
            final String lessonNumber,
            @Nullable final String description,
            final String content,
            final String duration
    ){
        server.createLesson(ScholarMeServer.getCallback(responseListener), courseId, title, lessonNumber, description, content, duration);
    }

    public void getCourseLesson(
            final ScholarMeServer.ResponseListener<List<LessonGson>> responseListener,
            final int courseId
    ){
        server.getCourseLesson(ScholarMeServer.getCallback(responseListener), courseId);
    }

    public void updateLesson(
            final ScholarMeServer.ResponseListener<GsonData> responseListener,
            final int lessonId,
            @Nullable final String title,
            @Nullable final String lessonNumber,
            @Nullable final String description,
            @Nullable final String content,
            @Nullable final String duration
    ) {
        server.updateLesson(ScholarMeServer.getCallback(responseListener), lessonId, title, lessonNumber, description, content, duration);
    }

    public void deleteLesson(
            final ScholarMeServer.ResponseListener<GsonData> responseListener,
            final int courseLessonId
    ) {
        server.deleteLesson(ScholarMeServer.getCallback(responseListener), courseLessonId);
    }

    // -- FLASHCARD CRUD

    public void createFlashcardSet(
            final ScholarMeServer.ResponseListener<GsonData> responseListener,
            final String title,
            final String description
    ){
        server.createFlashcardSet(ScholarMeServer.getCallback(responseListener), title, description);
    }

    public void editFlashcardSet(
            final ScholarMeServer.ResponseListener<GsonData> responseListener,
            final int flashcardSetId,
            @Nullable final String title,
            @Nullable final String description
    ){
        server.editFlashcardSet(ScholarMeServer.getCallback(responseListener), flashcardSetId, title, description);
    }

    public void deleteFlashcardSet(
            final ScholarMeServer.ResponseListener<GsonData> responseListener,
            final int flashcardSetId
    ){
        server.deleteFlashcardSet(ScholarMeServer.getCallback(responseListener), flashcardSetId);
    }

    public void getFlashcardSets(
            final ScholarMeServer.ResponseListener<List<FlashcardSetGson>> responseListener
    ){
        server.getFlashcardSets(ScholarMeServer.getCallback(responseListener));
    }

    public void createFlashcard(
            final ScholarMeServer.ResponseListener<GsonData> responseListener,
            final int flashcardSetId,
            final String question
    ){
        server.createFlashcard(ScholarMeServer.getCallback(responseListener), flashcardSetId, question);
    }

    public void getFlashcardSetFlashcards(
            final ScholarMeServer.ResponseListener<List<FlashcardGson>> responseListener,
            final int flashcardSetId
    ){
        server.getFlashcardSetFlashcards(ScholarMeServer.getCallback(responseListener), flashcardSetId);
    }

    public void addFlashcardChoice(
            final ScholarMeServer.ResponseListener<GsonData> responseListener,
            final int flashcardId,
            final String choice,
            final boolean isAnswer
    ){
        server.addFlashcardChoice(ScholarMeServer.getCallback(responseListener), flashcardId, choice, isAnswer);
    }

    public void getFlashcardChoices(
            final ScholarMeServer.ResponseListener<List<FlashcardChoiceGson>> responseListener,
            final int flashcardId
    ){
        server.getFlashcardChoices(ScholarMeServer.getCallback(responseListener), flashcardId);
    }

    // -- NOTIFICATIONS
    public void getUserNotifications(
            final ScholarMeServer.ResponseListener<List<NotificationGson>> responseListener
    ){
        server.getUserNotifications(ScholarMeServer.getCallback(responseListener));
    }

    public void deleteNotification(
            final ScholarMeServer.ResponseListener<GsonData> responseListener,
            final int notificationId
    ){
        server.deleteNotification(ScholarMeServer.getCallback(responseListener), notificationId);
    }

    //-- START OF MISCELLANEOUS --
    public void showLoadingScreen() {
        mainBinding.progressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoadingScreen() {
        mainBinding.progressBar.setVisibility(View.GONE);
    }
    public void makeToast(Object message, int duration){
        Toast.makeText(getMainActivity().getApplicationContext(), String.valueOf(message), duration).show();
    }

    public void popupPomodoro(View view) {
        LayoutInflater inflater = (LayoutInflater) getMainActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_pomodoro_settings, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // pomodoro fields
        SharedPreferences preferences = getMainActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Button btnClose = popupView.findViewById(R.id.timer_btn_close);
        Button btnSave = popupView.findViewById(R.id.timer_btn_save);
        EditText ettWork = popupView.findViewById(R.id.timer_ett_work);
        EditText ettLong = popupView.findViewById(R.id.timer_ett_long_break);
        EditText ettBreak = popupView.findViewById(R.id.timer_ett_break);
        EditText ettBreakInterval = popupView.findViewById(R.id.timer_ett_break_interval);
        SeekBar sbVolume = popupView.findViewById(R.id.timer_sb_volume);

        ettWork.setText(String.valueOf(preferences.getInt(KEY_WORK_TIME, 25)));
        ettBreak.setText(String.valueOf(preferences.getInt(KEY_BREAK_TIME, 5)));
        ettLong.setText(String.valueOf(preferences.getInt(KEY_LONG_BREAK_TIME, 30)));
        ettBreakInterval.setText(String.valueOf(preferences.getInt(KEY_BREAK_INTERVAL, 4)));
        sbVolume.setProgress(preferences.getInt(KEY_VOLUME_LEVEL, 50));

//        // pomodoro methods
        btnClose.setOnClickListener(v -> popupWindow.dismiss());
        btnSave.setOnClickListener(v -> {
            int valWork = Integer.parseInt(String.valueOf(ettWork.getText()));
            editor.putInt(KEY_WORK_TIME, valWork);
            int valBreak = Integer.parseInt(String.valueOf(ettBreak.getText()));
            editor.putInt(KEY_BREAK_TIME, valBreak);
            int valLong = Integer.parseInt(String.valueOf(ettLong.getText()));
            editor.putInt(KEY_LONG_BREAK_TIME, valLong);
            int valBreakInterval = Integer.parseInt(String.valueOf(ettBreakInterval.getText()));
            editor.putInt(KEY_BREAK_INTERVAL, valBreakInterval);
            int valVolume = sbVolume.getProgress();
            editor.putInt(KEY_VOLUME_LEVEL, valVolume);

            editor.apply();
            makeToast("Timer configurations saved", Toast.LENGTH_SHORT);
            popupWindow.dismiss();
        });
    }

    public void popupCreateFlashcardSet(View view) {
        LayoutInflater inflater = (LayoutInflater) getMainActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_create_flashcardset, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // flashcard fields
        Button btnClose = popupView.findViewById(R.id.popup_flashcardset_btn_close);
        Button btnSave = popupView.findViewById(R.id.popup_flashcardset_btn_create);
        EditText ettTitle = popupView.findViewById(R.id.popup_flashcardset_ett_title);
        EditText ettDescription = popupView.findViewById(R.id.popup_flashcardset_ett_desc);

        // flashcard methods
        btnClose.setOnClickListener(v -> popupWindow.dismiss());
        btnSave.setOnClickListener(v -> {
            String valTitle = String.valueOf(ettTitle.getText());
            String valDescription = String.valueOf(ettDescription.getText());

            createFlashcardSet(new ScholarMeServer.ResponseListener<GsonData>() {
                @Override
                public void onSuccess(GsonData data) {
                    makeToast("Flashcard set created", Toast.LENGTH_SHORT);
                    popupWindow.dismiss();
                }

                @Override
                public void onFailure(String message) {
                    makeToast(message, Toast.LENGTH_SHORT);
                }
            }, valTitle, valDescription);
        });
    }

    public void popupUpdateCourse(View view, int courseId) {
        LayoutInflater inflater = (LayoutInflater) getMainActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_edit_course, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        Button btnClose = popupView.findViewById(R.id.edit_course_btn_close);
        Button btnUpdate = popupView.findViewById(R.id.edit_course_btn_update);
        EditText ettTitle = popupView.findViewById(R.id.edit_course_ett_title);
        EditText ettDescription = popupView.findViewById(R.id.edit_course_ett_desc);

        btnClose.setOnClickListener(v -> popupWindow.dismiss());
        btnUpdate.setOnClickListener(v -> {
            String valTitle = String.valueOf(ettTitle.getText()).isEmpty() ? null : String.valueOf(ettTitle.getText());
            String valDescription = String.valueOf(ettDescription.getText()).isEmpty() ? null : String.valueOf(ettTitle.getText());;

            updateCourse(new ScholarMeServer.ResponseListener<GsonData>() {
                @Override
                public void onSuccess(GsonData data) {
                    makeToast("Successfully updated course", Toast.LENGTH_SHORT);
                    popupWindow.dismiss();
                }

                @Override
                public void onFailure(String message) {
                    makeToast(message, Toast.LENGTH_SHORT);
                }
            }, courseId,null ,valTitle, valDescription);
        });
    }

    public void popupDeleteCourseWarning(View view, int courseId) {
        LayoutInflater inflater = (LayoutInflater) getMainActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_warning, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        Button btnNo = popupView.findViewById(R.id.warning_btn_no);
        Button btnYes = popupView.findViewById(R.id.warning_btn_yes);
        TextView tvContent = popupView.findViewById(R.id.warning_txt_content);

        tvContent.setText("Are you sure you want to delete this course?");
        btnNo.setOnClickListener(v -> popupWindow.dismiss());
        btnYes.setOnClickListener(v -> {

            deleteCourse(new ScholarMeServer.ResponseListener<GsonData>() {
                @Override
                public void onSuccess(GsonData data) {
                    makeToast("Successfully deleted course", Toast.LENGTH_SHORT);
                    popupWindow.dismiss();
                }

                @Override
                public void onFailure(String message) {
                    makeToast(message, Toast.LENGTH_SHORT);
                }
            }, courseId);

        });
    }

    public void popupUnenrollWarning(View view, int courseId) {
        LayoutInflater inflater = (LayoutInflater) getMainActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_warning, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        Button btnNo = popupView.findViewById(R.id.warning_btn_no);
        Button btnYes = popupView.findViewById(R.id.warning_btn_yes);
        TextView tvContent = popupView.findViewById(R.id.warning_txt_content);

        tvContent.setText("Are you sure you want to unenroll from this course?");
        btnNo.setOnClickListener(v -> popupWindow.dismiss());
        btnYes.setOnClickListener(v -> {

            unenrollCourse(new ScholarMeServer.ResponseListener<GsonData>() {
                @Override
                public void onSuccess(GsonData data) {
                    makeToast("Successfully unenrolled", Toast.LENGTH_SHORT);
                    popupWindow.dismiss();
                }

                @Override
                public void onFailure(String message) {
                    makeToast(message, Toast.LENGTH_SHORT);
                }
            }, courseId);

        });
    }

    public void popupDeleteLessonWarning(View view, int courseLessonId) {
        LayoutInflater inflater = (LayoutInflater) getMainActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_warning, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        Button btnNo = popupView.findViewById(R.id.warning_btn_no);
        Button btnYes = popupView.findViewById(R.id.warning_btn_yes);
        TextView tvContent = popupView.findViewById(R.id.warning_txt_content);

        tvContent.setText("Are you sure you want to delete this lesson from this course?");
        btnNo.setOnClickListener(v -> popupWindow.dismiss());
        btnYes.setOnClickListener(v -> {

            deleteLesson(new ScholarMeServer.ResponseListener<GsonData>() {
                @Override
                public void onSuccess(GsonData data) {
                    makeToast("Successfully deleted", Toast.LENGTH_SHORT);
                    popupWindow.dismiss();
                }

                @Override
                public void onFailure(String message) {
                    makeToast(message, Toast.LENGTH_SHORT);
                }
            }, courseLessonId);

        });
    }
}

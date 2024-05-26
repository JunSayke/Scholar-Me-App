package com.example.solutionsproject.classes.general;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
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
import com.example.solutionsproject.model.gson.data.GsonData;
import com.example.solutionsproject.model.gson.data.UserGson;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import java.io.File;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
public class MainFacade {
    public static final String TAG = "Main Facade";
    private static final MainFacade mainFacade = new MainFacade(null);
    private static FragmentActivity mainActivity;
    private final ScholarMeServer server = new ScholarMeServer();
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
            @Nullable final File thumbnail,
            @Nullable final String title,
            @Nullable final String description
    ){
        server.updateCourse(ScholarMeServer.getCallback(responseListener), thumbnail, title, description);
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

    public void getCourses(
            final ScholarMeServer.ResponseListener<List<CourseGson>> responseListener
    ){
        server.getCourses(ScholarMeServer.getCallback(responseListener));
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
}
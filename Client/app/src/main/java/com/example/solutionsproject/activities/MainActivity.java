package com.example.solutionsproject.activities;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.solutionsproject.R;
import com.example.solutionsproject.classes.general.MainFacade;
import com.example.solutionsproject.classes.retrofit.ChatWebSocketService;
import com.example.solutionsproject.databinding.ActivityMainBinding;
import com.example.solutionsproject.model.gson.data.UserGson;
import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private MainFacade mainFacade;
    private String ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mainFacade = MainFacade.getInstance(this);
        mainFacade.setMainBinding(binding);
        ipAddress = mainFacade.getIpAddress() + ":" + mainFacade.getServerPort();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.mainFragmentContainer);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        ChatWebSocketService client = new ChatWebSocketService();
        client.connect();
        client.sendMessage(1, "Hello, world!");

        mainFacade.setOpeningNavController(navController);
        mainFacade.setCurrentNavController(navController);

        OnBackPressedCallback callback = new OnBackPressedCallback(true){

            @Override
            public void handleOnBackPressed() {
                NavController currentNavController = mainFacade.getCurrentNavController();
                if(currentNavController != null && !currentNavController.popBackStack()){
                    finish();
                }
            }
        };

        this.getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Uri uri = getIntent().getData();
        if (uri != null) {
            if (Objects.equals(uri.getHost(), "http://" + ipAddress)) {
                String apiAccessToken = uri.getQueryParameter("api_access_token");

                if (apiAccessToken != null) {
                    DecodedJWT jwt = JWT.decode(apiAccessToken);

                    String payload = jwt.getClaims().toString();
                    UserGson userGson = new Gson().fromJson(payload, UserGson.class);
                    Set<String> cookies = new HashSet<>();
                    cookies.add("api_access_token=" + apiAccessToken);
                    mainFacade.getServer().addCookies(cookies);
                    mainFacade.startLoginSession(userGson);
                    mainFacade.getUserGsonViewModel().setUserGsonLiveData(userGson);
                }
            }
        }
    }
}
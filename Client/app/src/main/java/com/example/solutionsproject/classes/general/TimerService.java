package com.example.solutionsproject.classes.general;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.solutionsproject.R;
import com.example.solutionsproject.activities.FlashcardActivity;

public class TimerService extends Service {
    private int valWork;
    private int valBreak;
    private int valLong;
    private int valBreakInterval;
    private int valVolume;

    private CountDownTimer timer;
    private int ctr;
    private int currentPhase; // 0 - Pomodoro, 1 - Short Break, 2 - Long Break
    private int remainingTime;

    private  Vibrator vibrator;
    private MainFacade mainFacade;

    @Override
    public void onCreate() {
        super.onCreate();

        try{
            mainFacade = MainFacade.getInstance();
        }catch(Exception e){
            throw new RuntimeException(e);
        }

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        SharedPreferences preferences = getSharedPreferences(MainFacade.PREF_NAME, Context.MODE_PRIVATE);
        valWork = preferences.getInt(MainFacade.KEY_WORK_TIME, 25) * 60 * 1000;
        valBreak = preferences.getInt(MainFacade.KEY_BREAK_TIME, 5) * 60 * 1000;
        valLong = preferences.getInt(MainFacade.KEY_LONG_BREAK_TIME, 30) * 60 * 1000;
        valBreakInterval = preferences.getInt(MainFacade.KEY_BREAK_INTERVAL, 4);
        valVolume = preferences.getInt(MainFacade.KEY_VOLUME_LEVEL, 50);

        ctr = 0;
        currentPhase = 0;
        remainingTime = valWork;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTimer();
        showNotification();
        return START_STICKY; // Re-create the service if needed
    }

    private void startTimer() {
        timer = new CountDownTimer(remainingTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime = (int) millisUntilFinished;
                updateNotification();
            }

            @Override
            public void onFinish() {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                switch (currentPhase) {
                    case 0: // Pomodoro finished, start short break
                        ctr++;
                        currentPhase = 1;
                        remainingTime = valWork;
                        break;
                    case 1: // Short break finished, start pomodoro again
                        currentPhase = 0;
                        remainingTime = valBreak;
                        break;
                    case 2: // Long break finished, reset timer
                        currentPhase = 0;
                        remainingTime = valLong;
                        break;
                }
                startTimer();
            }
        };
        timer.start();
    }

    @SuppressLint("ForegroundServiceType")
    private void showNotification() {
        String title, content;
        switch (currentPhase) {
            case 0:
                if(ctr > valBreakInterval){
                    currentPhase = 2;
                    ctr = 0;
                }
                title = "Pomodoro";
                content = "Focus for " + formatTime(remainingTime);
                break;
            case 1:
                title = "Short Break";
                content = "Take a short break: " + formatTime(remainingTime);
                break;
            case 2:
                title = "Long Break";
                content = "Take a longer break: " + formatTime(remainingTime);
                break;
            default:
                title = "Pomodoro Timer";
                content = "";
                break;
        }

        Intent notificationIntent = new Intent(this, FlashcardActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.stopwatch)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
    }

    private void updateNotification() {
        showNotification();
    }

    private String formatTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Replace with your actual channel ID
    private static final String CHANNEL_ID = "POMODORO_CHANNEL";

    // Implement other methods like onBind and onUnbind (optional)
}

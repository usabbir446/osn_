package com.defense.notecase;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.google.firebase.iid.FirebaseInstanceId;

import static com.defense.notecase.LoginActivity.IS_LOGGED_IN;
import static com.defense.notecase.LoginActivity.SHARED_PREFS;

public class SplashActivity extends AppCompatActivity {
    private SharedPreferences sharedPref;
    private Boolean isLoggedIn;
    private Intent intent;


    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID =
            "officers_noteCase_primary_notification_channel";

    private NotificationManager mNotifyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        sharedPref = getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        isLoggedIn = sharedPref.getBoolean(IS_LOGGED_IN,false);


        if(isLoggedIn)
        {
            intent = new Intent(SplashActivity.this,DirectoryActivity.class);
        }
        else{

            intent = new Intent(SplashActivity.this,LoginActivity.class);
        }
        // Create the notification channel.
        createNotificationChannel();

        new CountDownTimer(2000, 1000) {
            @Override
            public void onFinish() {

                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
            }
            @Override
            public void onTick(long millisUntilFinished) {
            }
        }.start();
    }

    private void createNotificationChannel() {
        // Create a notification manager object.
        mNotifyManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            getString(R.string.notification_channel_name),
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    (getString(R.string.notification_channel_description));

            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }


}
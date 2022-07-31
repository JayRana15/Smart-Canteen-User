package com.example.smartcanteen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.navyBlue));

        TextView logo = findViewById(R.id.logo);

        Animation anime = AnimationUtils.loadAnimation(this,R.anim.logo_anime);

        logo.setAnimation(anime);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int temp = sp.getInt("user_id",00);
        Log.d("uid",String.valueOf(temp));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (temp != 00 ) {
                    startActivity(new Intent(MainActivity.this,dashBoardActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(MainActivity.this,logInActivity.class));
                    finish();
                }
            }
        },1500);
    }
}
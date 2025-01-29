package com.app.easy_patient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.Fade;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.app.easy_patient.R;
import com.app.easy_patient.activity.onboarding.OnBoardingActivity;
import com.app.easy_patient.model.AlarmListModel;
import com.app.easy_patient.util.SharedPrefs;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;
import io.paperdb.Paper;

public class SplashActivity extends BaseActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusBar(R.color.statusBarColor);
        setContentView(R.layout.activity_splash);
        imageView = findViewById(R.id.logo);

        showHideIcon(true);
        new Handler().postDelayed(() -> {
            showHideIcon(false);
        }, 1500);

        int SPLASH_TIME_OUT = 3000;
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this,
                    OnBoardingActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_TIME_OUT);
    }

    private void showHideIcon(boolean show) {
        if (show)
            YoYo.with(Techniques.FadeIn)
                    .duration(700)
                    .playOn(imageView);
        else
            YoYo.with(Techniques.FadeOut)
                    .duration(700)
                    .playOn(imageView);
    }
}

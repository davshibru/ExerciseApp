package com.example.databaseexercisetest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;

public class InformationAboutSport extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_about_sport);

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl("https://calorizator.ru/article/exercise/the-first-time");
        webView.startAnimation(anim);
    }
}
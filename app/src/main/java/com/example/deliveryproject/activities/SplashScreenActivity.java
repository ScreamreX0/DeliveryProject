package com.example.deliveryproject.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import com.example.deliveryproject.R;

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Индикатор выполнения (круглый)
        ProgressBar progressBar = findViewById(R.id.a_splash_progress_bar);

        // Настройка индикатора загрузки
        progressBar.setMax(5);
        progressBar.setProgress(0);
        progressBar.incrementSecondaryProgressBy(1);

        // Ожидание
        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, AuthActivity.class));
        }, 3000);
    }
}
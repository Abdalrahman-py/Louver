package com.example.louver;

import androidx.core.splashscreen.SplashScreen;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.louver.ui.home.HomeFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Only add fragment the first time (avoid re-adding on rotation)
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainContainer, new HomeFragment())
                    .commit();
        }
    }
}

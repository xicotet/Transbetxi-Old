package com.example.transbetxi.ui.main.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.transbetxi.R;
import com.example.transbetxi.databinding.ActivityMainBinding;
import com.example.transbetxi.ui.main.view.MainFragment;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }
    }
}
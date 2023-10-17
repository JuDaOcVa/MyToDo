package com.dev.ToDo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListToDoFragment listToDoFragment = new ListToDoFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentHome, listToDoFragment)
                .commit();
    }
}
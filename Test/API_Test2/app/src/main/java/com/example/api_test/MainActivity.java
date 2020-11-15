package com.example.api_test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import kr.safe.map.api.SafeMap3DAPI;

public class MainActivity extends AppCompatActivity {
    SafeMap3DAPI aa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
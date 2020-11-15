package com.example.api_test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import kr.safe.map.api.SafeMap3DAPI;

public class MainActivity extends AppCompatActivity {
    SafeMap3DAPI aa;
    Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        aa = new SafeMap3DAPI(this);
        System.out.println("BT8E2FHH-BT8E-BT8E-BT8E-BT8E2FHH14");
        aa.setSvcKey("BT8E2FHH-BT8E-BT8E-BT8E-BT8E2FHH14");
        System.out.println("result : " + aa.getSvcKey());
        b1 = (Button) findViewById(R.id.Button1);
        b1.setText(aa.getSvcKey());
    }
}
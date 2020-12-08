package com.example.test_set_parent;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.NestedScrollingChildHelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class Information extends AppCompatActivity {

    TextView parent;
    TextView child;

    SharedPreferences sf;
    String Child_PhoneNum;
    String Parent_PhoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        parent = (TextView)findViewById(R.id.parentphone);
        child = (TextView)findViewById(R.id.childphone);

        sf = getSharedPreferences("test",MODE_PRIVATE);

        Child_PhoneNum = sf.getString("Child_Phonenum","Nothing");
        Parent_PhoneNum = sf.getString("Parent_Phonenum","Nothing");

        Intent intent = getIntent();

        parent.setText(Parent_PhoneNum);
        child.setText(Child_PhoneNum);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("개인정보 얻어오기");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
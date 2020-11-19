package com.example.test_set_parent;

import android.Manifest;
import android.app.AppComponentFactory;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.os.Handler;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {
    String PhoneNum = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button Send_Button = (Button) findViewById(R.id.Button01);
        Button Recv_Button = (Button) findViewById(R.id.Button02);
        TextView t = (TextView) findViewById(R.id.Test);

//        TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);//Phone number 얻어오기
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            return; //신경 쓸 필요 x
//        }
//        //Phone number얻어오기
//        PhoneNum = telManager.getLine1Number();
//        if(PhoneNum.startsWith("+82"))
//        { PhoneNum = PhoneNum.replace("+82", "0"); }

//        Send_Button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//             Intent intent = new Intent(MainActivity.this, Send_Activity.class);
//             intent.putExtra("Phone_number",PhoneNum);
//             startActivity(intent);
//            }
//        });
//
//        Recv_Button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, Recv_Activity.class);
//                startActivity(intent);
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.send_item :
                Intent intent = new Intent(MainActivity.this, Send_Activity.class);
                intent.putExtra("Phone_number",PhoneNum);
                startActivity(intent);
                break;
            case R.id.recv_item :
                Intent intent2 = new Intent(MainActivity.this, Recv_Activity.class);
                startActivity(intent2);
                break;
            case R.id.get_info :
                Toast.makeText(this, "개인정보 얻어오기", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
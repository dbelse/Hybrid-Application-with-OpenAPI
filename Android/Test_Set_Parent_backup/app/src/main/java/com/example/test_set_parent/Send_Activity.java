package com.example.test_set_parent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

//Send Rand_num

public class Send_Activity extends AppCompatActivity {
    private Handler mHandler;
    Socket socket;
    private String ip = "192.168.0.3";
    private int SEND_PORT = 10000;
    TextView msgTV;
    String Phone_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_);

        mHandler = new Handler();
        Button btn = (Button) findViewById(R.id.button);
        msgTV = (TextView) findViewById(R.id.textView);

        Intent intent = getIntent();
        Phone_num = intent.getExtras().getString("Phone_number");

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    Send_Activity.ConnectThread th = new Send_Activity.ConnectThread();
                    th.start();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //난수를 만들어서 server로 보내기
    class ConnectThread extends Thread {
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(ip);
                socket = new Socket(serverAddr, SEND_PORT);
                int num = (int) (Math.random() * 8999) + 1000; // 난수
                String sndMsg = Integer.toString(num);
                mHandler.post(new Send_Activity.msgUpdate(sndMsg));
                //Log.d("=============", sndMsg);
                String msg = sndMsg;
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                out.println(Phone_num);
                out.println(sndMsg);

                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String read = input.readLine();
                mHandler.post(new Send_Activity.msgUpdate(read));
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    class msgUpdate implements Runnable {
        private String msg;

        public msgUpdate(String str) {
            this.msg = str;
        }

        public void run() {
            msgTV.setText(msgTV.getText().toString() + msg + "\n");
        }
    }
}
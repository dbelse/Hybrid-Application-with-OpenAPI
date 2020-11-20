package com.example.test_set_parent;

import androidx.appcompat.app.AppCompatActivity;

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

//Receive Result

public class Recv_Activity extends AppCompatActivity {
    private Handler mHandler;
    Socket socket;
    private String ip = "192.168.0.3";
    private int RECV_PORT = 10001;
    EditText et;
    TextView msgTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recv_);

        mHandler = new Handler();
        et = (EditText) findViewById(R.id.editText);
        Button btn = (Button) findViewById(R.id.send_button);
        msgTV = (TextView) findViewById(R.id.recv_textView);

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (et.getText().toString() != null) {
                    Recv_Activity.RecvThread th = new Recv_Activity.RecvThread();
                    th.start();
                }
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

    //Server로 난수를 보내서 결과를 받기
    class RecvThread extends Thread {
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(ip);
                socket = new Socket(serverAddr, RECV_PORT);

                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                String sndMsg = et.getText().toString();
                out.println(sndMsg);

                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String read = input.readLine();
                mHandler.post(new Recv_Activity.msgUpdate(read));
                Log.d("=============", sndMsg);
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
            msgTV.setText(msg + "\n");
        }
    }
}
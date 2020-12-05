package com.example.test_set_parent;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.MissingFormatArgumentException;

//Receive Result
//Server to Android
public class Recv_Activity extends AppCompatActivity {
    private Handler mHandler;
    Socket socket;
    private String ip = "220.66.217.94";
    private int RECV_PORT = 10001;
    EditText et;
    TextView msgTV;
    String Phone_num;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recv_);

        Intent intent = getIntent();
        Phone_num = intent.getExtras().getString("Phone_number");

        preferences = getSharedPreferences("test",MODE_PRIVATE);
        editor = preferences.edit();

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

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("인증키 입력하기");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    void setChild_Phonenum(final String Child_Phonenum)
    {
        new AlertDialog.Builder(Recv_Activity.this)
                .setTitle("")
                .setMessage(Child_Phonenum +"으로 피보호자를 설정하시겠습니까?")
                .setPositiveButton("네",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick (DialogInterface arg0,int arg1){
                                Intent intent = new Intent(Recv_Activity.this, MainActivity.class);
                                editor.putString("Child_Phonenum",Child_Phonenum);
                                editor.commit();
                                startActivity(intent); //test용
                            }
                        }
                ).setNegativeButton("아니요",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick (DialogInterface arg0,int arg1){
                    }
                }
        ).show();
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        try {
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    //Server로 난수를 보내서 결과를 받기
    class RecvThread extends Thread {
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(ip);
                socket = new Socket(serverAddr, RECV_PORT);

                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                String sndMsg = et.getText().toString();
                out.println(Phone_num);
                out.println(sndMsg);

                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String Child_Phonenum = input.readLine();
                socket.close();
                mHandler.post(new Recv_Activity.msgTest(Child_Phonenum));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class msgTest implements  Runnable{
        String Child_Phonenum;
        msgTest(String Child_Phonenum){this.Child_Phonenum = Child_Phonenum;}
        public void run() {
            if(!Child_Phonenum.equals("Wrong"))
                setChild_Phonenum(Child_Phonenum);
        }
    }
}
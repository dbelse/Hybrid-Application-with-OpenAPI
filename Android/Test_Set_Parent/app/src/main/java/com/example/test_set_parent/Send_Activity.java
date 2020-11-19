package com.example.test_set_parent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
    private String ip = "20.20.1.191";
    private int SEND_PORT = 10000;
    TextView msgTV;
    String Phone_num;
    mytimer timer;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_);

        mHandler = new Handler();
        btn = (Button) findViewById(R.id.button);
        msgTV = (TextView) findViewById(R.id.textView);

        timer = new mytimer(60000,1000);

        Intent intent = getIntent();
        Phone_num = intent.getExtras().getString("Phone_number");

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    Send_Activity.ConnectThread th = new Send_Activity.ConnectThread();
                    timer.start();
                    th.start();
                    btn.setEnabled(false);
            }
        });
    }

    void setParent_Phonenum(final String Parent_Phonenum)
    {
        new AlertDialog.Builder(Send_Activity.this)
                .setTitle("")
                .setMessage(Parent_Phonenum +"으로 보호자를 설정하시겠습니까?")
                .setPositiveButton("네",
            new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick (DialogInterface arg0,int arg1){
                    Intent intent = new Intent(Send_Activity.this, MainActivity.class);
                    intent.putExtra("Child_Phonenum",Parent_Phonenum); //test용
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

    class mytimer extends CountDownTimer
    {
        TextView tv2 = (TextView)findViewById(R.id.textView2);
        TextView tv3 = (TextView)findViewById(R.id.textView3);
        mytimer(long millisInFutuer, long countDownInterval)
        {
            super(millisInFutuer,countDownInterval);
        }
        @Override
        public void onTick(long millisUntilFinished)
        {
            tv3.setText(millisUntilFinished/1000 + "");
            tv2.setVisibility(View.VISIBLE);
        }
        @Override
        public void onFinish()
        {
            btn.setEnabled(true);
            tv3.setText("");
            tv2.setVisibility(View.INVISIBLE);
        }
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
                int num = (int) (Math.random() * 89999) + 10000; // 난수생성
                String sndMsg = Integer.toString(num);
                mHandler.post(new Send_Activity.msgUpdate(sndMsg));
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                out.println(Phone_num);
                out.println(sndMsg);

                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String Parent_Phonenum = input.readLine(); //get Parent_Phonenum
                socket.close();
                mHandler.post(new Send_Activity.msgTest(Parent_Phonenum));
                timer.onFinish();
                timer.cancel();

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
    //스레드 종료 후 다이얼로그 msg 띄우기
    class msgTest implements  Runnable{
        String Parent_Phonenum;
        msgTest(String Parent_Phonenum){this.Parent_Phonenum = Parent_Phonenum;}
        public void run() {
            if(!Parent_Phonenum.equals("null"))
            setParent_Phonenum(Parent_Phonenum);
        }
    }
}
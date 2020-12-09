package com.example.test_set_parent;

import android.Manifest;
import android.app.AppComponentFactory;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;


import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.os.Handler;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {
    public String Child_PhoneNum;
    public String PhoneNum;
    public String Parent_PhoneNum;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    NotificationManager m_NotiManager;
    // 문자 메세지 보내기 임시 세팅
    public String EmergencyMessage = "보호자가 위험 지역으로 접근 중입니다.";
    public String SendMessage;
    WebView wv;
    private static final String ENTRY_URL = "file:///android_asset/test.html";
    Location myLocation;

    String xx, yy;
    static final Integer APP_PERMISSION = 1;


    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(this, "" + permission + " is already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    public void NotificationSomethings() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground)) //BitMap 이미지 요구
                .setContentTitle("피보호자가 위험한 지역에 진입했습니다.")
                .setContentText("피보호자가 위험지역 반경 500m안에 접근했습니다.")
                // 더 많은 내용이라서 일부만 보여줘야 하는 경우 아래 주석을 제거하면 setContentText에 있는 문자열 대신 아래 문자열을 보여줌
                //.setStyle(new NotificationCompat.BigTextStyle().bigText("더 많은 내용을 보여줘야 하는 경우..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            CharSequence channelName = "노티페케이션 채널";
            String description = "오레오 이상을 위한 것임";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance);
            channel.setDescription(description);

            // 노티피케이션 채널을 시스템에 등록
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        } else
            builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        assert notificationManager != null;
        notificationManager.notify(1234, builder.build()); // 고유숫자로 노티피케이션 동작시킴

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //test용

        SharedPreferences sf = getSharedPreferences("test", MODE_PRIVATE);
        Child_PhoneNum = sf.getString("Child_Phonenum", "Nothing");
        Parent_PhoneNum = sf.getString("Parent_Phonenum", "Nothing");

        TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);//Phone number 얻어오기
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return; //신경 쓸 필요 x
        }

        //Phone number얻어오기
        PhoneNum = telManager.getLine1Number();
        if (PhoneNum.startsWith("+82")) {
            PhoneNum = PhoneNum.replace("+82", "0");
        }

        //   TextView aa = (TextView)findViewById(R.id.tv1);
        //   aa.setText("My Child Phone Number : "+ Child_PhoneNum);

        //   TextView bb = (TextView)findViewById(R.id.tv2);
        //   bb.setText("My Parent Phone Number : "+ Parent_PhoneNum);

        m_NotiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, APP_PERMISSION);
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                myLocation = location;
                Log.d("Main", "\n피보호자 위치: (" + myLocation.getLatitude() + "," + myLocation.getLongitude() + ")" +
                        getAddress(getApplicationContext(), myLocation.getLatitude(), myLocation.getLongitude()) +
                        " 위험지역과 거리: " + (int) DistanceByDegree(myLocation.getLatitude(), myLocation.getLongitude(), dp.y, dp.x) + "m" );
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            return;
        if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        myLocation = locationManager.getLastKnownLocation(locationProvider);

        wv = (WebView) findViewById(R.id.webview);
        wv.getSettings().setJavaScriptEnabled(true);
//        wv.setWebViewClient(new WebViewClient(){
//                                @Override
//                                public void onPageFinished(WebView view, String url)
//                                {
//                                    if(url.equals(ENTRY_URL)){
//                                        String token = Double.toString(myLocation.getLatitude());
//                                        String token1 = Double.toString(myLocation.getLongitude());
//                                        String script = "javascript:function afterLoad() {"
//                                                + "document.getElementById('x').value = '" + token + "', document.getElementById('y').value = '" +  token1 +"';"
//                                                + "};"
//                                                + "afterLoad();";
//                                        view.loadUrl(script);
//                                    }
//                                }
//                            }
//        );

        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                super.onGeolocationPermissionsShowPrompt(origin, callback);
                callback.invoke(origin, true, false);
            }
        });
        wv.loadUrl(ENTRY_URL);
        Loc loc = new Loc();
        wv.addJavascriptInterface(loc, "Android");

        wv.getSettings().setGeolocationEnabled(true);
    }

    class Loc {
    @JavascriptInterface
    public String get_xy(String inner) {
        xx = String.valueOf(myLocation.getLatitude());
        yy = String.valueOf(myLocation.getLongitude());
        if(inner.equals("x"))
            return xx;
        else
            return yy;
    }
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
                Intent intent1 = new Intent(MainActivity.this, Recv_Activity.class);
                intent1.putExtra("Phone_number",PhoneNum);
                startActivity(intent1);
                break;
            case R.id.get_info :
                Toast.makeText(this, "개인정보 얻어오기", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(MainActivity.this, Information.class);
                startActivity(intent2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class dangerPoint {//임시로 위험지혁 좌표 우리학교
        double x = 128.611637;
        double y = 35.887482;
    }

    dangerPoint dp = new dangerPoint();

    /*class myLocation {//나의 현위치 좌표
        double x = 0;
        double y = 0;
    }*/

    boolean mBreak = true;
    Random random = new Random();

    public void startWork(View view) {

        if (mBreak) {
            mBreak = false;
//            Button start = (Button) findViewById(R.id.button1);
//            start.setText("작업 중지!");
            Thread thread = new Thread(new BackCounter());
            thread.setDaemon(true);
            thread.start();
        } else {
            mBreak = true;
//            Button start = (Button) findViewById(R.id.button1);
//            start.setText("작업 시작!");
        }
    }

    public double DistanceByDegree(double _latitude1, double _longitude1, double _latitude2, double _longitude2) {
        double theta, dist;
        theta = _longitude1 - _longitude2;
        dist = Math.sin(DegreeToRadian(_latitude1)) * Math.sin(DegreeToRadian(_latitude2)) + Math.cos(DegreeToRadian(_latitude1))
                * Math.cos(DegreeToRadian(_latitude2)) * Math.cos(DegreeToRadian(theta));
        dist = Math.acos(dist);
        dist = RadianToDegree(dist);

        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;    // 단위 mile 에서 km 변환.
        dist = dist * 1000.0;      // 단위  km 에서 m 로 변환

        return dist;
    }

    //degree->radian 변환
    public double DegreeToRadian(double degree) {
        return degree * Math.PI / 180.0;
    }

    //randian -> degree 변환
    public double RadianToDegree(double radian) {
        return radian * 180d / Math.PI;
    }

    class BackCounter implements Runnable {
        public int ISENTERING = 0;
        public int cnt = 0;

        public void run() {
            while (!mBreak) {
                /*myLocation ml = new myLocation();
                ml.x = random.nextInt(3) + random.nextDouble() + 127;//임시로 현위치 x좌표 랜덤생성
                ml.y = random.nextInt(1) + random.nextDouble() + 35;//임시로 현위치 y좌표 랜덤생성*/

                if (DistanceByDegree(myLocation.getLatitude(), myLocation.getLongitude(), dp.y,
                        dp.x) <= 500) {//임시로 위험지역과 거리 500m 이내 모두 경고
                    NotificationSomethings();
                    ISENTERING = 1;
                    cnt++;
                } else {
                    ISENTERING = 0;
                    cnt = 0;
                }

                Log.d("Main",
                        "myLocation: (" + myLocation.getLatitude() + "," + myLocation.getLongitude() + ")" +
                                " 위험지역과 " +
                                "거리: " + (int) DistanceByDegree(myLocation.getLatitude(), myLocation.getLongitude(), dp.y,
                                dp.x) + "m ");

                if (ISENTERING == 1 && cnt == 1) {
                    try {
                        SendMessage = EmergencyMessage + "\n피보호자 위치: (" + myLocation.getLatitude() + "," + myLocation.getLongitude() + ")" +
                                getAddress(getApplicationContext(), myLocation.getLatitude(), myLocation.getLongitude()) +
                                " 위험지역과 거리: " + (int) DistanceByDegree(myLocation.getLatitude(), myLocation.getLongitude(), dp.y, dp.x) + "m";
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(Parent_PhoneNum, null, SendMessage, null, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // 위험 지역 접근 시 보호자에게 문자 알림
                }
//
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    ;
//                }
            }
        }
    }

    static public String getAddress(Context mContext, double lat, double lng) {
        String nowAddress = "현 위치를 찾고 있습니다";
        Geocoder geocoder = new Geocoder(mContext, Locale.KOREA);
        List<Address> address;
        try {
            //세번째 파라미터는 좌표에 대해 주소를 리턴 받는 갯수로
            //한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 최대갯수 설정
            address = geocoder.getFromLocation(lat, lng, 1);

            if (address != null && address.size() > 0) {
                // 주소 받아오기
                nowAddress = address.get(0).getAddressLine(0);

            }

        } catch (IOException e) {
            //Toast.makeText(MainActivity.this, "잘못된 포인트 설정입니다.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return nowAddress;
    }
}

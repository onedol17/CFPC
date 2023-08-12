package com.example.gps_speed;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;




public class MainActivity extends AppCompatActivity implements LocationListener {
    int value = 0;

    String threadEnd = "False";
    double num = 0;

    int CarbonAmount = 0;
    private LocationManager locationManager;
    private Location mLastlocation = null;
    private TextView text1;
    private TextView tCarbonAmount;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text1 = (TextView) findViewById(R.id.text1);

        tCarbonAmount = (TextView) findViewById(R.id.tCarbonAmount);

        //권한 체크
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String formatDate = sdf.format(new Date(lastKnownLocation.getTime()));
        }
        // GPS 사용 가능 여부 확인
        boolean isEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        double deltaTime = 0;

        //  getSpeed() 함수를 이용하여 속도를 계산
        double getSpeed = Double.parseDouble(String.format("%.3f", location.getSpeed()));
        text1.setText(String.valueOf(getSpeed) + " m/s");  //Get Speed
        String formatDate = sdf.format(new Date(location.getTime()));
        boolean isStart = false;

        if (getSpeed >= 23 && isStart == false) {
            isStart = true;
            BackgroundThread thread = new BackgroundThread();
            num += getSpeed;
            thread.start();
        }
        if (threadEnd == "True"){
            num = (getSpeed + num) / 2;
            CarbonAmount += (num + 1);
            tCarbonAmount.setText(String.valueOf(CarbonAmount) + " g");
            threadEnd = "False";
            value = 0;
        }
        // 위치 변경이 두번째로 변경된 경우 계산에 의해 속도 계산
        if (mLastlocation != null) {
            //시간 간격
            deltaTime = (location.getTime() - mLastlocation.getTime()) / 1000.0;
            // 속도 계산
            double speed = mLastlocation.distanceTo(location) / deltaTime;

        }
        // 현재위치를 지난 위치로 변경
        mLastlocation = location;
    }

    @Override
    public void onProviderEnabled(String provider) {
        //권한 체크
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // 위치정보 업데이트
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //권한이 없을 경우 최초 권한 요청 또는 사용자에 의한 재요청 확인
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // 권한 재요청
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            }
        }

    }
    class BackgroundThread extends Thread {
        public void run() {
                try {
                    Thread.sleep(1000);

                    value += 1;
                    Log.d("Thread", "value : " + value);
                    if (value == 60) {
                            threadEnd = "True";
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }



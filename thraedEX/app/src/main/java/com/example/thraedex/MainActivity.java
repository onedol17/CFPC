package com.example.thraedex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;





public class  MainActivity extends AppCompatActivity implements LocationListener{

    private LocationManager locationManager;
    private Location mLastlocation = null;
    int value = 0;

    int hour_flag = 0;

    int Speed;

    int sum_list;
    List<Integer> listA = new ArrayList<>();

    double CarbonAmount= 0.0;

    String logFlag;
    String getTime;
    TextView test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = (TextView)findViewById(R.id.test);
        //권한 체크
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        test= (TextView)findViewById(R.id.tv_carbon_amount);
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        }
        // GPS 사용 가능 여부 확인
        boolean isEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);


        BackgroundThread thread = new BackgroundThread();
        thread.start();
        ViewThread thread2 = new ViewThread();
        thread2.start();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        double deltaTime;

        //  getSpeed() 함수를 이용하여 속도를 계산
        @SuppressLint("DefaultLocale") double getSpeed = Double.parseDouble(String.format("%.3f", location.getSpeed()));
        String formatDate = sdf.format(new Date(location.getTime()));


        Speed = (int) getSpeed;
        test.setText(Speed);


        if (mLastlocation != null) {
            //시간 간격
            deltaTime = (location.getTime() - mLastlocation.getTime()) / 1000.0;
            // 속도 계산
            double speed = mLastlocation.distanceTo(location) / deltaTime;

        }
        /* 현재위치를 지난 위치로 변경 */
        mLastlocation = location;
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //권한이 없을 경우 최초 권한 요청 또는 사용자에 의한 재요청 확인
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // 권한 재요청
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            }
        }

    }

    class ViewThread extends Thread {
        public void run() {
            while (true) {
                try {

                } catch (Exception e) {
                }
                if (logFlag == "true") {
                    Log.d("VAL", "VALUE: " + value);
                    Log.d("VAL", "VALUE: " + listA);

                    test.setText(String.valueOf(CarbonAmount));
                    logFlag = "False";
                }
            }
        }
    }

    class BackgroundThread extends Thread {
        public void run(){

            while (true){
                try {
                    Thread.sleep(1000);
                }catch(Exception e){ }

                long now = System.currentTimeMillis();
                Date date = new Date(now);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
                getTime = dateFormat.format(date);
                Calendar cToday = Calendar.getInstance();
                int sec = cToday.get(Calendar.SECOND); // 초
                int hour = cToday.get(Calendar.HOUR);
                if (!Integer.valueOf(hour).equals(hour_flag)) {
                    for(int i=0; i < listA.size(); i++){
                        sum_list += listA.get(i);
                    }
                    double dt = (((double)sum_list/listA.size())*((double)listA.size()*30));
                    CarbonAmount = ((((dt/ 16.04) * 2.097) / 0.1) * 0.1);


                    Log.i("VAL", String.valueOf(CarbonAmount));


                    String fileName = "testnote.txt";
                    String textToWrite = String.valueOf(CarbonAmount);
                    FileOutputStream outputStream;

                    try {
                        outputStream = openFileOutput(fileName , Context.MODE_PRIVATE);
                        outputStream.write(textToWrite.getBytes());
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    listA.clear();
                }

                Log.i("VAL", String.valueOf(sec));
                if (sec%30 == 0 && Speed >= 7) {

                    value += 30;
                    listA.add(Speed);
                    logFlag = "true";
                }

                Log.i("VAL", read_file(getApplicationContext(), "testnote.txt"));
                hour_flag = hour;
            }
        }
    }
    public String read_file(Context context, String filename) {
        try {
            FileInputStream fis = context.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        } catch (IOException e) {
            return "";
        }
    }
}

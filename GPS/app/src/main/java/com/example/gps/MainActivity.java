package com.example.gps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
   TextView test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test = findViewById(R.id.text1);
        String loc = get_loc();
        test.setText(loc);
    }

    public String get_loc() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //마지막 위치 받아오기
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
                //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return "";
        }
        Location loc_Current = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        double cur_lat = loc_Current.getLatitude(); //위도
        double cur_lon = ((Location) loc_Current).getLongitude(); //경도
//        Toast.makeText(getApplicationContext(), String.valueOf(cur_lat), Toast.LENGTH_LONG).show();
        return String.valueOf(cur_lat) + "/" + String.valueOf(cur_lon);
    }
}
package com.example.thraedex;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    int value = 0;
    String x = "0";

    String logFlag;
    String getTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BackgroundThread thread = new BackgroundThread();
        thread.start();

        ViewThread thread2 = new ViewThread();
        thread2.start();
    }
    class ViewThread extends Thread {
        public void run() {
            while (true) {
                try {

                } catch (Exception e) {
                }
                if (logFlag == "true") {
                    Log.d("VAL", "VALUE: " + String.valueOf(value));
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
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
                getTime = dateFormat.format(date);

                if (!String.valueOf(getTime.charAt(6)).equals(x)) {
                    value += 10;
                    logFlag = "true";
                }

                x = String.valueOf(getTime.charAt(6));
            }
        }
    }

}


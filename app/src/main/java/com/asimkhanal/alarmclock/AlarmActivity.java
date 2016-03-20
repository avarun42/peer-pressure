package com.asimkhanal.alarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity {
    AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        setTitle("Alarm Actions");
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    public void stopButtonClicked(View V){
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void snoozeButtonClicked(View V){
        String contactNumber = "1234567890";

        Log.d("MyActivity", "Alarm On");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 1);
        Intent myIntent = new Intent(AlarmActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, 0, myIntent, 0);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
        Log.d("AddActivity", "alarm");

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(contactNumber));
        try {
            startActivity(callIntent);
        } catch (SecurityException e) {
            Log.d("callIntent", "CALL_PHONE permission not granted");
        }

        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
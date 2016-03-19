package com.asimkhanal.alarmclock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    TextClock timeView;
    Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Text Alarm");

        //view the time
        timeView = (TextClock)findViewById(R.id.clock);
    }

    public void addButtonClick(View V){
        //open the new activity
        Intent intent = new Intent(MainActivity.this,AddActivity.class);
        startActivity(intent);
    }
}

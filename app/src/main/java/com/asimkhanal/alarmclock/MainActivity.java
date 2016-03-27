package com.asimkhanal.alarmclock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;

import java.util.Locale;

import static android.text.format.DateFormat.getBestDateTimePattern;

public class MainActivity extends Activity {
    TextClock timeView, dateView;
    Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Text Alarm");

        DatabaseHelper db = DatabaseHelper.getInstance(this);
        // If db is empty, must be populated
        if (db.isEmpty()) {
            Intent initialContactsIntent = new Intent(MainActivity.this, InitialContactsActivity.class);
            startActivity(initialContactsIntent);
        }

        dateView = (TextClock) findViewById(R.id.dateClock);
        String skeleton = "MMMddyyyy";
        CharSequence bestPattern = getBestDateTimePattern(Locale.getDefault(), skeleton);
        dateView.setFormat12Hour(bestPattern);
        dateView.setFormat24Hour(bestPattern);

        //view the time
        timeView = (TextClock)findViewById(R.id.clock);
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        stopService(intent);
    }

    public void addButtonClick(View V) {
        //open the new activity
        Intent intent = new Intent(MainActivity.this, AddActivity.class);
        startActivity(intent);
    }

    public void contactButtonClicked(View V){
        Intent intent = new Intent(MainActivity.this,AddContactActivity.class);
        startActivity(intent);
    }
}

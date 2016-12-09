package me.arora.varun.peerpressure;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.util.Calendar;

public class AddAlarmActivity extends Activity {
    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TextView alarmTextView;
    private TimePicker alarmTimePicker;
    private DatePicker alarmDatePicker;
    private static AddAlarmActivity inst;
    private String alarmText;

    public static AddAlarmActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);
        setTitle("Add an alarm");
        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
        alarmDatePicker = (DatePicker)findViewById(R.id.datePicker);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        ToggleButton alarmToggle = (ToggleButton) findViewById(R.id.alarmToggle);
        alarmTextView = (TextView) findViewById(R.id.text_time);
    }

    public void onToggleClicked(View view) {
        if (((ToggleButton) view).isChecked()) {
            Log.d("MyActivity", "Alarm On");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
            /*calendar.set(Calendar.YEAR,alarmDatePicker.getYear());
            calendar.set(Calendar.MONTH,alarmDatePicker.getMonth());
            calendar.set(Calendar.MONTH,alarmDatePicker.getDayOfMonth());*/
            Intent myIntent = new Intent(AddAlarmActivity.this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(AddAlarmActivity.this, 0, myIntent, 0);
            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
            Log.d("AddAlarmActivity", "alarm");
        } else {
            alarmManager.cancel(pendingIntent);
            setAlarmText("");
            Log.d("MyActivity", "Alarm Off");
        }
    }

    public void setAlarmText(String alarmText) {
        this.alarmText = alarmText;
        alarmTextView.setText(alarmText);
    }
}

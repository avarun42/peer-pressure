package com.asimkhanal.alarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity {
    AlarmManager alarmManager;
    AudioManager audioManager;
    private PendingIntent pendingIntent;
    private static final String LOG_TAG = "callIntent";

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

    private class EndCallListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (TelephonyManager.CALL_STATE_RINGING == state) {
                Log.d(LOG_TAG, "RINGING, number: " + incomingNumber);
            }
            if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                // wait for phone to go offhook (probably set a boolean flag) so you know your app initiated the call.
                Log.d(LOG_TAG, "OFFHOOK");
            }
            if (TelephonyManager.CALL_STATE_IDLE == state) {
                // // when this state occurs, and your flag is set, restart your app
                Log.d(LOG_TAG, "IDLE");

                audioManager.setSpeakerphoneOn(false);
            }
        }
    }

    public void snoozeButtonClicked(View V){
        // May need to run following block in service
        EndCallListener callListener = new EndCallListener();
        TelephonyManager mTM = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mTM.listen(callListener, PhoneStateListener.LISTEN_CALL_STATE);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);

        String contactNumber = "tel:6097907855";

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
            audioManager.setSpeakerphoneOn(true);
            startActivity(callIntent);
        } catch (SecurityException e) {
            Log.d("callIntent", "CALL_PHONE permission not granted");
            Log.d("callIntent", e.getMessage());
        }

        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
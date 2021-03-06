package me.arora.varun.peerpressure;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class AlarmActivity extends Activity implements SensorListener {
    private static final int SHAKE_DURATION =100000 ;
    private AlarmManager alarmManager;
    private AudioManager audioManager;
    private static final String LOG_TAG = "callIntent";
    private SensorManager sensorManager;
    private static final int SHAKE_THRESHOLD = 3200;
    private long lastUpdate;
    private float last_x, last_y, last_z;
    private long mLastShake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        setTitle("Alarm Actions");
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,
                SensorManager.SENSOR_ACCELEROMETER,
                SensorManager.SENSOR_DELAY_GAME);
        lastUpdate = System.currentTimeMillis();
        //audioManager.setMode(AudioManager.MODE_NORMAL);

        //audioManager.setMicrophoneMute(true);
    }

// --Commented out by Inspection START (4/2/16, 12:05 AM):
//    public void stopButtonClicked(View V){
//        android.os.Process.killProcess(android.os.Process.myPid());
//    }
// --Commented out by Inspection STOP (4/2/16, 12:05 AM)

//    private class EndCallListener extends PhoneStateListener {
//        @Override
//        public void onCallStateChanged(int state, String incomingNumber) {
//            if (TelephonyManager.CALL_STATE_RINGING == state) {
//                Log.d(LOG_TAG, "RINGING, number: " + incomingNumber);
//            }
//            if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
//                // wait for phone to go offhook (probably set a boolean flag) so you know your app initiated the call.
//                Log.d(LOG_TAG, "OFFHOOK");
//            }
//            if (TelephonyManager.CALL_STATE_IDLE == state) {
//                // // when this state occurs, and your flag is set, restart your app
//                Log.d(LOG_TAG, "IDLE");
//
////                audioManager.setSpeakerphoneOn(false);
//            }
//        }
//    }

    public void snoozeButtonClicked(View V) {
        String tier = "LOW";

        Log.d("MyActivity", "Alarm On");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 1);
        Intent myIntent = new Intent(AlarmActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, 0, myIntent, 0);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
        Log.d("AddAlarmActivity", "alarm");

        try {
            DatabaseHelper db = DatabaseHelper.getInstance(this);
            List<Contact> contacts = db.getContactsByTier(tier);
            Contact contactToCall = contacts.get((new Random()).nextInt(contacts.size()));

            // May need to run following block in service
//            EndCallListener callListener = new EndCallListener();
//            TelephonyManager mTM = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//            mTM.listen(callListener, PhoneStateListener.LISTEN_CALL_STATE);
            Log.d("atag",contactToCall.phone_number);
            String contactNumber = "tel:" + contactToCall.phone_number;

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse(contactNumber));
            try {
                //audioManager.setSpeakerphoneOn(true);
                startActivity(callIntent);
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                audioManager.setSpeakerphoneOn(true);
            } catch (SecurityException e) {
                Log.d("callIntent", "CALL_PHONE permission not granted");
                Log.d("callIntent", e.getMessage());
            }
        } catch (IllegalArgumentException e) {
            Log.w("snoozeButton", "No contacts found, call cannot be made");
        }

        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onSensorChanged(int sensor, float[] values) {
        if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float x = values[SensorManager.DATA_X];
                float y = values[SensorManager.DATA_Y];
                float z = values[SensorManager.DATA_Z];

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD ) {
                    if(curTime-mLastShake>SHAKE_DURATION) {
                        mLastShake=curTime;
                        Log.d("sensor", "shake detected w/ speed: " + speed);
                        Toast.makeText(this, "shake detected w/ speed: " + speed, Toast.LENGTH_SHORT).show();
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }

    }

    @Override
    public void onAccuracyChanged(int sensor, int accuracy) {

    }
}
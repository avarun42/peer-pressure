package com.asimkhanal.alarmclock;

import android.app.Activity;
import android.os.Bundle;

public class InitialContactsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_contacts);

        // In any activity just pass the context and use the singleton method
        DatabaseHelper db = DatabaseHelper.getInstance(this);
    }
}

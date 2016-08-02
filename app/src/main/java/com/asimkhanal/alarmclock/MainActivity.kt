package com.asimkhanal.alarmclock

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextClock

import java.util.Locale

import android.text.format.DateFormat.getBestDateTimePattern

class MainActivity : Activity() {
    internal var timeView: TextClock = findViewById(R.id.clock) as TextClock
    internal var dateView: TextClock = findViewById(R.id.dateClock) as TextClock
    internal var addButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Text Alarm"

        val db = DatabaseHelper.getInstance(this)

        /*
            If db is empty, must be populated
         */
        if (db.isEmpty) {
            val initialContactsIntent = Intent(this@MainActivity, InitialContactsActivity::class.java)
            startActivity(initialContactsIntent)
        }

        val skeleton = "MMMddyyyy"
        val bestPattern = getBestDateTimePattern(Locale.getDefault(), skeleton)
        dateView.format12Hour = bestPattern
        dateView.format24Hour = bestPattern

        //view the time
        val intent = Intent(this@MainActivity, AlarmReceiver::class.java)
        stopService(intent)
    }

    fun addButtonClick(V: View) {
        //open the new activity
        val intent = Intent(this@MainActivity, AddAlarmActivity::class.java)
        startActivity(intent)
    }

    fun contactButtonClicked(V: View) {
        val intent = Intent(this@MainActivity, AddContactActivity::class.java)
        startActivity(intent)
    }
}

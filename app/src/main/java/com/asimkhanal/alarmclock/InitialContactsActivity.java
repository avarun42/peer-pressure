package com.asimkhanal.alarmclock;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.Random;

public class InitialContactsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_contacts);

        // In any activity just pass the context and use the singleton method
        DatabaseHelper db = DatabaseHelper.getInstance(this);

        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String tier;

            int tierNum = new Random().nextInt(3);

            switch (tierNum) {
                case 0:
                    tier = "low";
                    break;
                case 1:
                    tier = "medium";
                    break;
                case 2:
                default:
                    tier = "high";
                    break;
            }

            Contact contact = new Contact();
            contact.name = name;
            contact.phone_number = phoneNumber;
            contact.tier = tier.toUpperCase();

            db.addOrUpdateContact(contact);

            Log.d("contact", contact.toString());
        }
        cursor.close();

        Intent mainIntent = new Intent(InitialContactsActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }
}

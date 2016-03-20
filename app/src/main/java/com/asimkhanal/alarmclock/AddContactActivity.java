package com.asimkhanal.alarmclock;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class AddContactActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        DatabaseHelper db = new DatabaseHelper(this);

        /**
         * CRUD Operations
         * */
        // Inserting Contacts
        Log.d("Insert: ", "Inserting ..");
        db.addContact(new Contact("Ravi", "9100000000","Low"));
        db.addContact(new Contact("Srinivas", "9199999999","High"));
        db.addContact(new Contact("Tommy", "9522222222","Medium"));
        db.addContact(new Contact("Karthik", "9533333333","Low"));

        // Reading all contacts
        Log.d("Reading: ", "Reading all contacts..");
        List<Contact> contacts = db.getAllContacts();

        for (Contact cn : contacts) {
            String log = "Id: " + cn.getID() + " ,Name: " + cn.getName() + " ,Phone: " + cn.getPhoneNumber();
            // Writing Contacts to log
            Log.d("Name: ", log);
        }
    }
}

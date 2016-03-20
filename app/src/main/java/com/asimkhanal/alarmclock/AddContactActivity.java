package com.asimkhanal.alarmclock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

public class AddContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        // Create sample data
        Contact c1 = new Contact();
        c1.name = "Ravi";
        c1.phone_number = "9100000000";
        c1.tier = "Low";

        Contact c2 = new Contact();
        c2.name = "Srinivas";
        c2.phone_number = "9199999999";
        c2.tier = "High";

        Contact c3 = new Contact();
        c3.name = "Tommy";
        c3.phone_number = "9522222222";
        c3.tier = "Medium";

        Contact c4 = new Contact();
        c4.name = "Karthik";
        c4.phone_number = "9533333333";
        c4.tier = "Low";

        // Get singleton instance of database
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);

        // Add sample contacts to the database
        Log.d("Insert: ", "Inserting ..");
        databaseHelper.addOrUpdateContact(c1);
        databaseHelper.addOrUpdateContact(c2);
        databaseHelper.addOrUpdateContact(c3);
        databaseHelper.addOrUpdateContact(c4);

        // Get all contacts from database
        Log.d("Reading: ", "Reading all contacts..");
        List<Contact> contacts = databaseHelper.getAllContacts();
        for (Contact cn : contacts) {
            String log = "Name: " + cn.name + ", Phone #: " + cn.phone_number + ", Tier: " + cn.tier;
            Log.d("Name: ", log);
        }
    }
}

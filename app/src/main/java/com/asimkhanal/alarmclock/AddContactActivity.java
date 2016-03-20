package com.asimkhanal.alarmclock;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class AddContactActivity extends Activity {
    EditText nameText,phoneNo;
    Spinner tierText;
    String tier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        setTitle("Add Contact");
        Spinner spinner = (Spinner) findViewById(R.id.spinner_tier);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tier_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        nameText = (EditText)findViewById(R.id.edit_name);
        phoneNo = (EditText)findViewById(R.id.edit_phone);
        tierText = (Spinner)findViewById(R.id.spinner_tier);



    }

    public void addContactClicked(View V){

        if (tierText.getSelectedItem().toString().equals("Low Key")){
            tier = "Low";
        } else if(tierText.getSelectedItem().toString().equals("Slightly Awkward")){
            tier = "Medium";
        } else if(tierText.getSelectedItem().toString().equals("You don\'t wanna call this person")){
            tier = "high";
        }

        // Create sample data
        Contact c1 = new Contact();
        c1.name = nameText.getText().toString();
        c1.phone_number = "tel:" + phoneNo.getText().toString();
        c1.tier = tier;


        // Get singleton instance of database
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);

        // Add sample contacts to the database
        Log.d("Insert: ", "Inserting ..");
        databaseHelper.addOrUpdateContact(c1);
//        databaseHelper.addOrUpdateContact(c2);
//        databaseHelper.addOrUpdateContact(c3);
//        databaseHelper.addOrUpdateContact(c4);

        // Get all contacts from database
        Log.d("Reading: ", "Reading all contacts..");
        List<Contact> contacts = databaseHelper.getAllContacts();
        for (Contact cn : contacts) {
            String log = "Name: " + cn.name + ", Phone #: " + cn.phone_number + ", Tier: " + cn.tier;
            Log.d("Name: ", log);
        }
    }
}

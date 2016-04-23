package com.asimkhanal.alarmclock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class AddContactActivity extends Activity {
    EditText nameText, phoneNo;
    Spinner tierText;
    String tier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        setTitle("Add Contact");

        nameText = (EditText) findViewById(R.id.edit_name);
        phoneNo = (EditText) findViewById(R.id.edit_phone);
        tierText = (Spinner) findViewById(R.id.spinner_tier);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tier_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        tierText.setAdapter(adapter);

        nameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                    startActivityForResult(intent, 1);
                }
            }
        });
    }


    public void addContactClicked(View V){

        // Can we use tierText.getSelectedItemId() or tierText.getSelectedItemPosition() instead?
        switch (tierText.getSelectedItem().toString()) {
            case "Low Key":
                tier = "low";
                break;
            case "Slightly Awkward":
                tier = "medium";
                break;
            case "You don't wanna call this person":
                tier = "high";
                break;
        }

        // Create contact data
        Contact c1 = new Contact();
        c1.name = nameText.getText().toString();
        c1.phone_number = phoneNo.getText().toString();
        c1.tier = tier.toUpperCase();

        // Get singleton instance of database
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);

        // Add contact to the database
        Log.d("Insert: ", "Inserting ..");
        int contactId = databaseHelper.addOrUpdateContact(c1);

        // Get contact from database
        Log.d("Reading: ", "Reading inserted contact..");
        Contact cn = databaseHelper.getContact(contactId);
        Log.d("Name: ", cn.toString());
    }
}

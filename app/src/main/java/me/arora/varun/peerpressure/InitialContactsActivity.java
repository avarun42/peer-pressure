package me.arora.varun.peerpressure;

import android.app.Activity;
import android.content.ContentResolver;
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
            String id;
            String name;
            String phoneNumber = "";
            String tier;

            int idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            id = cursor.getString(idIndex);

            int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            name = cursor.getString(nameIndex);

            int hasPhoneNumberIndex = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);

            if (Integer.parseInt(cursor.getString(hasPhoneNumberIndex)) > 0) {
                Cursor phoneCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{id}, null);

                while (phoneCur.moveToNext()) {
                    int type = phoneCur.getInt(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

                    int phoneIndex = phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    if (type == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                        phoneNumber = phoneCur.getString(phoneIndex);
                    }
                }

                phoneCur.close();
            }

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

            if (contact.name.equals("") || contact.phone_number.equals("")) {
                Log.d("contactNotAdded", contact.toString());
                continue;
            }

            db.addOrUpdateContact(contact);

            Log.d("contact", contact.toString());
        }
        cursor.close();

        finish();
    }
}

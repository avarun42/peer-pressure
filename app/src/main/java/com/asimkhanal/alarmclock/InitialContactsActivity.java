package com.asimkhanal.alarmclock;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

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
            //get name
            int nameFiledColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            String contact = cursor.getString(nameFiledColumnIndex);
            String phoneString = "";

            String[] PHONES_PROJECTION = new String[]{"_id", "display_name", "data1", "data3"};//
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
            //name type ..
            while (phone.moveToNext()) {
                int i = phone.getInt(0);
                phoneString += phone.getString(1);
                phoneString += phone.getString(2);
                phoneString += phone.getString(3);
            }
            phone.close();

//            //addr
//            Cursor addrCur = cr.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
//                    new String[]{"_id", "data1", "data2", "data3"}, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
//            while (addrCur.moveToNext()) {
//                int i = addrCur.getInt(0);
//                String str = addrCur.getString(1);
//                str = addrCur.getString(2);
//                str = addrCur.getString(3);
//            }
//            addrCur.close();
//
//            //email
//            Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
//                    new String[]{"_id", "data1", "data2", "data3"}, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
//            while (emailCur.moveToNext()) {
//                int i = emailCur.getInt(0);
//                String str = emailCur.getString(1);
//                str = emailCur.getString(2);
//                str = emailCur.getString(3);
//            }
//            emailCur.close();

            Log.d("contact", "Name: " + contact + ", Phone: " + phoneString);

        }
        cursor.close();

        Intent mainIntent = new Intent(InitialContactsActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }
}

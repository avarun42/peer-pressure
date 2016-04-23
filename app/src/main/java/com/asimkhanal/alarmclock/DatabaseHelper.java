package com.asimkhanal.alarmclock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DB";

    private static DatabaseHelper sInstance;

    // Database Info
    private static final String DATABASE_NAME = "contactsManager";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_CONTACTS = "contacts";

    // Contacts Table Columns
    private static final String KEY_CONTACT_ID = "id";
    private static final String KEY_CONTACT_NAME = "name";
    private static final String KEY_CONTACT_PH_NO = "phone_number";
    private static final String KEY_CONTACT_TIER = "tier";

    public static synchronized DatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS +
                "(" +
                KEY_CONTACT_ID + " INTEGER PRIMARY KEY," +
                KEY_CONTACT_NAME + " TEXT," +
                KEY_CONTACT_PH_NO + " TEXT," +
                KEY_CONTACT_TIER + " TEXT" +
                ")";

        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
            onCreate(db);
        }
    }

    // Insert or update a contact in the database
    // Since SQLite doesn't support "upsert" we need to fall back on an attempt to UPDATE (in case the
    // contact already exists) optionally followed by an INSERT (in case the contact does not already exist).
    // Unfortunately, there is a bug with the insertOnConflict method
    // (https://code.google.com/p/android/issues/detail?id=13045) so we need to fall back to the more
    // verbose option of querying for the contact's primary key if we did an update.
    public long addOrUpdateContact(Contact contact) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        long contactID = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_CONTACT_NAME, contact.name);
            values.put(KEY_CONTACT_PH_NO, contact.phone_number);
            values.put(KEY_CONTACT_TIER, contact.tier);

            // First try to update the contact in case the contact already exists in the database
            // This assumes contact phone numbers are unique
            int rows = db.update(TABLE_CONTACTS, values, KEY_CONTACT_PH_NO + "= ?", new String[]{contact.phone_number});

            // Check if update succeeded
            if (rows == 1) {
                // Get the primary key of the contact we just updated
                String CONTACTS_SELECT_QUERY = String.format("SELECT %s FROM %s WHERE %s = ?",
                        KEY_CONTACT_ID, TABLE_CONTACTS, KEY_CONTACT_PH_NO);
                Cursor cursor = db.rawQuery(CONTACTS_SELECT_QUERY, new String[]{String.valueOf(contact.phone_number)});
                try {
                    if (cursor.moveToFirst()) {
                        contactID = cursor.getInt(0);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                // contact with this phone_number did not already exist, so insert new contact
                contactID = db.insertOrThrow(TABLE_CONTACTS, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add or update contact");
        } finally {
            db.endTransaction();
        }

        return contactID;
    }

    // Get all contacts in the database
    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();

        // SELECT * FROM CONTACTS
        String CONTACTS_SELECT_QUERY =
                String.format("SELECT * FROM %s",
                        TABLE_CONTACTS);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(CONTACTS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Contact newContact = new Contact();
                    newContact.name = cursor.getString(cursor.getColumnIndex(KEY_CONTACT_NAME));
                    newContact.phone_number = cursor.getString(cursor.getColumnIndex(KEY_CONTACT_PH_NO));
                    newContact.tier = cursor.getString(cursor.getColumnIndex(KEY_CONTACT_TIER));

                    contacts.add(newContact);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get contacts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return contacts;
    }

    // Get all contacts of a certain tier from the database
    public List<Contact> getContactsByTier(String tier) {
        List<Contact> contacts = new ArrayList<>();

        // SELECT * FROM CONTACTS
        // WHERE TIER = tier
        String CONTACTS_SELECT_QUERY = String.format("SELECT * FROM %s WHERE %s = ?",
                TABLE_CONTACTS, KEY_CONTACT_TIER);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(CONTACTS_SELECT_QUERY, new String[]{tier});
        try {
            if (cursor.moveToFirst()) {
                do {
                    Contact newContact = new Contact();
                    newContact.name = cursor.getString(cursor.getColumnIndex(KEY_CONTACT_NAME));
                    newContact.phone_number = cursor.getString(cursor.getColumnIndex(KEY_CONTACT_PH_NO));
                    newContact.tier = cursor.getString(cursor.getColumnIndex(KEY_CONTACT_TIER));

                    contacts.add(newContact);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get contacts of tier " + tier + " from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return contacts;
    }

    // Getting single contact
    public Contact getContact(int id) {
        Contact contact = null;

        // SELECT * FROM CONTACTS
        // WHERE ID = id
        String CONTACTS_SELECT_QUERY = String.format("SELECT * FROM %s WHERE %s = ?",
                TABLE_CONTACTS, KEY_CONTACT_ID);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(CONTACTS_SELECT_QUERY, new String[]{String.valueOf(id)});
        try {
            if (cursor.moveToFirst()) {
                Contact newContact = new Contact();
                newContact.name = cursor.getString(cursor.getColumnIndex(KEY_CONTACT_NAME));
                newContact.phone_number = cursor.getString(cursor.getColumnIndex(KEY_CONTACT_PH_NO));
                newContact.tier = cursor.getString(cursor.getColumnIndex(KEY_CONTACT_TIER));

                contact = newContact;
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get specific contact from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return contact;
    }

    // Update the contact's tier
    public int updateContactTier(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CONTACT_TIER, contact.tier);

        // Updating tier for contact with that phone number
        return db.update(TABLE_CONTACTS, values, KEY_CONTACT_PH_NO + " = ?",
                new String[]{String.valueOf(contact.phone_number)});
    }

    // Delete all contacts in the database
    public void deleteAllContacts() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            db.delete(TABLE_CONTACTS, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all contacts");
        } finally {
            db.endTransaction();
        }
    }

    // Getting contacts Count
    public int getContactsCount() {
        // SELECT * FROM CONTACTS
        String CONTACTS_SELECT_QUERY =
                String.format("SELECT * FROM %s",
                        TABLE_CONTACTS);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(CONTACTS_SELECT_QUERY, null);

        return cursor.getCount();
    }

    public boolean isEmpty() {
        return getContactsCount() == 0;
    }

    // Deleting single contact
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_CONTACTS, KEY_CONTACT_PH_NO + " = ?",
                    new String[]{String.valueOf(contact.phone_number)});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete a contact");
        } finally {
            db.endTransaction();
        }
    }
}
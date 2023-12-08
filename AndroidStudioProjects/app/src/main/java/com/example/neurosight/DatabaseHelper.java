package com.example.neurosight;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Neurosight.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DatabaseContract.User.TABLE_NAME + " (" +
                    BaseColumns._ID + " INTEGER PRIMARY KEY," +
                    DatabaseContract.User.COLUMN_NAME_FIRST_NAME + " TEXT," +
                    DatabaseContract.User.COLUMN_NAME_LAST_NAME + " TEXT," +
                    DatabaseContract.User.COLUMN_NAME_EMAIL + " TEXT," +
                    DatabaseContract.User.COLUMN_NAME_PASSWORD + " TEXT)";


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DatabaseContract.User.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void updateUserPassword(String email, String newPassword) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.User.COLUMN_NAME_PASSWORD, newPassword);
        String selection = DatabaseContract.User.COLUMN_NAME_EMAIL + " = ?";
        String[] selectionArgs = {email};
        db.update(DatabaseContract.User.TABLE_NAME, values, selection, selectionArgs);
    }

    public DatabaseContract.User getUser(Context context, String email, String password) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                DatabaseContract.User.COLUMN_NAME_FIRST_NAME,
                DatabaseContract.User.COLUMN_NAME_LAST_NAME,
                DatabaseContract.User.COLUMN_NAME_EMAIL,
                DatabaseContract.User.COLUMN_NAME_PASSWORD


        };

        String selection = DatabaseContract.User.COLUMN_NAME_EMAIL + " = ? AND " +
                DatabaseContract.User.COLUMN_NAME_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(
                DatabaseContract.User.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        DatabaseContract.User user = null;

        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID));
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.User.COLUMN_NAME_FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.User.COLUMN_NAME_LAST_NAME));
            String userEmail = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.User.COLUMN_NAME_EMAIL));
            String userPassword = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.User.COLUMN_NAME_PASSWORD));
            user = new DatabaseContract.User(userId, firstName, lastName, userEmail, userPassword);
            Toast.makeText(context, "Authentification réussie", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Authentification échouée", Toast.LENGTH_SHORT).show();
        }

        cursor.close();

        return user;
    }

    public boolean checkUser(Context context, String email, String password) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                BaseColumns._ID
        };

        String selection = DatabaseContract.User.COLUMN_NAME_EMAIL + " = ?";

        String[] selectionArgs = {email};

        Cursor cursor = db.query(
                DatabaseContract.User.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean userExists = cursor.getCount() > 0;

        cursor.close();

        if (userExists) {
            Toast.makeText(context, "Authentification réussie", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Authentification échouée", Toast.LENGTH_SHORT).show();
        }

        return userExists;
    }

}



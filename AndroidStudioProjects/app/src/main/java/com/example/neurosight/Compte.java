package com.example.neurosight;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class Compte extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;
    private ImageView profilePictureImageView;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;

    private String firstName;
    private String lastName;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compte);

        profilePictureImageView = findViewById(R.id.profile_picture);
        firstNameEditText = findViewById(R.id.first_name_edittext);
        lastNameEditText = findViewById(R.id.last_name_edittext);
        emailEditText = findViewById(R.id.email_edittext);

        // Get user details from database and update the UI
        getUserDetails();
    }

    private void getUserDetails() {
        // Query the database for user details
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                DatabaseContract.User.COLUMN_NAME_FIRST_NAME,
                DatabaseContract.User.COLUMN_NAME_LAST_NAME,
                DatabaseContract.User.COLUMN_NAME_EMAIL
        };
        Cursor cursor = db.query(
                DatabaseContract.User.TABLE_NAME,   // The table to query
                projection,                         // The array of columns to return (pass null to get all)
                null,                               // The columns for the WHERE clause
                null,                               // The values for the WHERE clause
                null,                               // don't group the rows
                null,                               // don't filter by row groups
                null                                // don't sort the order
        );

        // Update the UI with user details
        if (cursor.moveToNext()) {
            firstName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.User.COLUMN_NAME_FIRST_NAME));
            lastName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.User.COLUMN_NAME_LAST_NAME));
            email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.User.COLUMN_NAME_EMAIL));

            firstNameEditText.setText(firstName);
            lastNameEditText.setText(lastName);
            emailEditText.setText(email);
        }
        cursor.close();
    }


}
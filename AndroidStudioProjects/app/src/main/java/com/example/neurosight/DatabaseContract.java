package com.example.neurosight;

import android.provider.BaseColumns;

public class DatabaseContract {


    public static final String TABLE_NAME = "User";
    public static final String COLUMN_NAME_FIRST_NAME = "first_name";
    public static final String COLUMN_NAME_LAST_NAME = "last_name";
    public static final String COLUMN_NAME_EMAIL = "email";
    public static final String COLUMN_NAME_PASSWORD = "password";
    public static final String COLUMN_NAME_IMAGE_PATH = "imagePath";

    private DatabaseContract() {
    }

    public static class User implements BaseColumns {
        public static final String TABLE_NAME = "User";
        public static final String COLUMN_NAME_FIRST_NAME = "first_name";
        public static final String COLUMN_NAME_LAST_NAME = "last_name";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_PASSWORD = "password";


        public User(int userId, String firstName, String lastName, String userEmail, String userPassword){
            // Add any additional constructor code here if necessary
        }
    }
}

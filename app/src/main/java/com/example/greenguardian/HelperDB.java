package com.example.greenguardian;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HelperDB extends SQLiteOpenHelper {
    private SQLiteDatabase db;
    public static final String MY_DB_FILE = "all_users.db";
    public static final String TABLE_USERS = "Users";
    public static final String COLUMN_ID = "Id";
    public static final String NAME = "Name";
    public static final String PASSWORD = "Password";
    public static final String PROFILE_PIC = "ProfilePic";  // BLOB column for storing image

    public HelperDB(Context context) {
        super(context, MY_DB_FILE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create table with profile picture as BLOB
        String st = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (";
        st += COLUMN_ID + " INTEGER PRIMARY KEY, ";
        st += NAME + " TEXT, ";
        st += PASSWORD + " TEXT, ";
        st += PROFILE_PIC + " BLOB);";  // Store the profile picture as a BLOB
        sqLiteDatabase.execSQL(st);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Basic versioning logic
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(sqLiteDatabase);
    }

    public void addUser(Users user) {
        db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NAME, user.getName());
        cv.put(PASSWORD, user.getPassword());

        // Convert Bitmap to byte[] for storing as BLOB
        if (user.getProfilePic() != null) {
            byte[] profilePicBlob = convertImageToBlob(user.getProfilePic());
            cv.put(PROFILE_PIC, profilePicBlob);
        }

        db.insert(TABLE_USERS, null, cv);
        db.close();
    }

    // Helper method to convert Bitmap to byte[]
    private byte[] convertImageToBlob(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream); // Compress as PNG (you can change format)
        return byteArrayOutputStream.toByteArray();
    }

    public boolean isUsernameTaken(String username) {
        db = getReadableDatabase();
        String[] columns = {NAME};
        Cursor cursor = db.query(TABLE_USERS, columns, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            @SuppressLint("Range") String currentUsername = cursor.getString(cursor.getColumnIndex(NAME));
            if (username.equals(currentUsername)) {
                cursor.close();
                db.close();
                return true;
            }
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        return false;
    }

    public boolean isUserExists(String username, String password) {
        db = getReadableDatabase();
        String[] columns = {NAME, PASSWORD};
        Cursor cursor = db.query(TABLE_USERS, columns, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            @SuppressLint("Range") String currentUsername = cursor.getString(cursor.getColumnIndex(NAME));
            @SuppressLint("Range") String currentPassword = cursor.getString(cursor.getColumnIndex(PASSWORD));
            if (username.equals(currentUsername) && password.equals(currentPassword)) {
                cursor.close();
                db.close();
                return true;
            }
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        return false;
    }

    public int getUserIdByName(String username) {
        db = getReadableDatabase();
        int userId = -1;
        String[] columns = {COLUMN_ID, NAME};
        String selection = NAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int idIndex = cursor.getColumnIndex(COLUMN_ID);
            if (idIndex >= 0) {
                userId = cursor.getInt(idIndex);
            }
            cursor.close();
        }

        db.close();
        return userId;
    }

    // New: Get full user object by username
    public Users getUserByName(String username) {
        db = getReadableDatabase();
        Users user = null;

        String[] columns = {NAME, PASSWORD, PROFILE_PIC};
        String selection = NAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(NAME));
            @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex(PASSWORD));
            @SuppressLint("Range") byte[] profilePicBlob = cursor.getBlob(cursor.getColumnIndex(PROFILE_PIC));

            // Convert the BLOB back to Bitmap
            Bitmap profilePicBitmap = BitmapFactory.decodeByteArray(profilePicBlob, 0, profilePicBlob.length);

            user = new Users(name, password, profilePicBitmap);  // Store Bitmap directly in user
            cursor.close();
        }

        db.close();
        return user;
    }

    // Optional: update user's profile picture
    public void updateUserProfilePic(String username, Bitmap newProfilePic) {
        db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PROFILE_PIC, convertImageToBlob(newProfilePic));

        db.update(TABLE_USERS, cv, NAME + " = ?", new String[]{username});
        db.close();
    }

    // Optional: update username
    public void updateUsername(String oldUsername, String newUsername) {
        db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NAME, newUsername);

        db.update(TABLE_USERS, cv, NAME + " = ?", new String[]{oldUsername});
        db.close();
    }
}


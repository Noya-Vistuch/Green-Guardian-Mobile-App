package com.example.greenguardian;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class CropDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CropDatabase.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "crops";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SIZE = "size";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_OWNER_ID = "owner";
    public static final String COLUMN_PLANTING_DATE = "planting_date";

    public CropDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_SIZE + " TEXT, " +
                COLUMN_TYPE + " TEXT, " +
                COLUMN_OWNER_ID + " INTEGER, " +
                COLUMN_PLANTING_DATE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For now: just drop and recreate
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Add a crop to the database
    public void addCrop(Crop crop) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, crop.getName());
        values.put(COLUMN_SIZE, crop.getSize());
        values.put(COLUMN_TYPE, crop.getType());
        values.put(COLUMN_OWNER_ID, crop.getUserId());
        values.put(COLUMN_PLANTING_DATE, crop.getPlantingDate());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // Get all crops for a specific user ID
    public List<Crop> getAllCropsByUserId(int userId) {
        List<Crop> cropList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                COLUMN_OWNER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                @SuppressLint("Range") String size = cursor.getString(cursor.getColumnIndex(COLUMN_SIZE));
                @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex(COLUMN_TYPE));
                @SuppressLint("Range") String plantingDate = cursor.getString(cursor.getColumnIndex(COLUMN_PLANTING_DATE));

                Crop crop = new Crop(id, userId, name, size, type, plantingDate);
                cropList.add(crop);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        return cropList;
    }

    // Delete a crop by its ID
    public void deleteCropById(int cropId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(cropId)});
        db.close();
    }
}



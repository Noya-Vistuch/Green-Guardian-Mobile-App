package com.example.greenguardian;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HelperDBProducts extends SQLiteOpenHelper {

    // Database Name and Version
    private static final String DATABASE_NAME = "market.db";
    private static final int DATABASE_VERSION = 2;

    // Table Name
    private static final String TABLE_PRODUCTS = "products";

    // Column Names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_IMAGE = "image";  // Store image as BLOB
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_COMMENT = "comment";

    private static final String COLUMN_USER_ID = "user_id";


    // Table Creation Query
    private static final String CREATE_TABLE_PRODUCTS = "CREATE TABLE " + TABLE_PRODUCTS + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_IMAGE + " BLOB, "
            + COLUMN_PRICE + " TEXT, "
            + COLUMN_COMMENT + " TEXT, "
            + COLUMN_USER_ID + " INTEGER);";


    public HelperDBProducts(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PRODUCTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    // Insert Product
    public boolean insertProduct(byte[] image, String price, String comment, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE, image);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_COMMENT, comment);
        values.put(COLUMN_USER_ID, userId); // Add this line

        long result = db.insert(TABLE_PRODUCTS, null, values);
        db.close();
        return result != -1;
    }


    // Retrieve all products
    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);
    }
    // Delete a product by ID
    public void deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
}
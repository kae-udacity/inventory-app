package com.example.android.inventoryapp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Handles the updating and reading of data from the inventory database.
 */

public class BookPresenter {

    private BookDbHelper bookDbHelper;

    public BookPresenter(BookDbHelper bookDbHelper) {
        this.bookDbHelper = bookDbHelper;
    }

    public void insertBook(String name, int priceInCents, String supplierName, int supplierPhoneNumber) {
        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.COLUMN_PRODUCT_NAME, name);
        values.put(BookContract.BookEntry.COLUMN_PRODUCT_PRICE, priceInCents);
        values.put(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierName);
        values.put(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, supplierPhoneNumber);

        SQLiteDatabase database = bookDbHelper.getWritableDatabase();
        database.insert(BookContract.BookEntry.TABLE_NAME, null, values);
    }

    public Cursor readData(String[] projection) {
        SQLiteDatabase database = bookDbHelper.getReadableDatabase();
        return database.query(BookContract.BookEntry.TABLE_NAME, projection, null, null, null, null, null);
    }
}

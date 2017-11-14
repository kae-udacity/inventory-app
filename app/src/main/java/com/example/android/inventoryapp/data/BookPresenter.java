package com.example.android.inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Handles the updating and reading of data from the inventory database.
 */

public class BookPresenter {

    private static final String TAG = BookPresenter.class.getSimpleName();

    private BookDbHelper bookDbHelper;

    public BookPresenter(Context context) {
        bookDbHelper = new BookDbHelper(context);
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

    public void readData() {
        SQLiteDatabase database = bookDbHelper.getReadableDatabase();

        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_PRODUCT_NAME,
                BookContract.BookEntry.COLUMN_PRODUCT_PRICE,
                BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY,
                BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL,
                BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER,
        };

        Cursor cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, null, null, null, null, null);

        try {
            int idColumnIndex = cursor.getColumnIndex(BookContract.BookEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierEmailColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);

            while (cursor.moveToNext()) {
                int currentId = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                String currentSupplierEmail = cursor.getString(supplierEmailColumnIndex);
                int currentSupplierPhoneNumber = cursor.getInt(supplierPhoneNumberColumnIndex);

                Log.i(TAG, "ID: " + currentId +
                        ", name: " + currentName +
                        ", price: " + currentPrice +
                        ", quantity: " + currentQuantity +
                        ", supplier name: " + currentSupplierName +
                        ", supplier email: " + currentSupplierEmail +
                        ", supplier phone number: " + currentSupplierPhoneNumber);
            }
        } finally {
            cursor.close();
        }
    }
}

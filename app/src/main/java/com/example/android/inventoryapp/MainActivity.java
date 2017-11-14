package com.example.android.inventoryapp;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.android.inventoryapp.data.BookContract;
import com.example.android.inventoryapp.data.BookDbHelper;
import com.example.android.inventoryapp.data.BookPresenter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BookPresenter bookPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bookPresenter = new BookPresenter(new BookDbHelper(this));
        bookPresenter.insertBook("The Martian", 1199, "Del Rey", 555923391);
        bookPresenter.insertBook("Lord of the Flies", 899, "Faber", 555978057);
        readData();
    }

    private void readData() {
        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_PRODUCT_NAME,
                BookContract.BookEntry.COLUMN_PRODUCT_PRICE,
                BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY,
                BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL,
                BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER,
        };

        Cursor cursor = bookPresenter.readData(projection);
        try {
            if (cursor.moveToFirst()) {
                Log.i(TAG, "Cursor: " + DatabaseUtils.dumpCursorToString(cursor));
            }
        } finally {
            cursor.close();
        }
    }
}

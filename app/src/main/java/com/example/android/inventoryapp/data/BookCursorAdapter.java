package com.example.android.inventoryapp.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.data.BookContract.BookEntry;

/**
 * Manages the views and populates the list using data from the Cursor.
 */

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.layout_list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView nameTextView = view.findViewById(R.id.name);
        TextView quantityTextView = view.findViewById(R.id.quantity);
        TextView priceTextView = view.findViewById(R.id.price);
        Button saleTextView = view.findViewById(R.id.sale);

        int idColumnIndex = cursor.getColumnIndexOrThrow(BookEntry._ID);
        int nameColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_PRICE);
        int supplierNameColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
        int supplierPhoneColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);

        final int id = cursor.getInt(idColumnIndex);
        final String name = cursor.getString(nameColumnIndex);
        final int quantity = cursor.getInt(quantityColumnIndex);
        final String quantityString = String.valueOf(quantity);
        final int priceInCents = cursor.getInt(priceColumnIndex);
        String price = String.valueOf(priceInCents / 100d);
        final String supplierName = cursor.getString(supplierNameColumnIndex);
        final String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

        nameTextView.setText(name);
        quantityTextView.setText(context.getString(R.string.quantity_header) + quantityString);
        priceTextView.setText(context.getString(R.string.currency) + price);

        saleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put(BookEntry.COLUMN_PRODUCT_NAME, name);
                values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, quantity - 1);
                values.put(BookEntry.COLUMN_PRODUCT_PRICE, priceInCents);
                values.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierName);
                values.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, supplierPhone);
                Uri uri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                context.getContentResolver().update(uri, values, null, null);
            }
        });
    }
}

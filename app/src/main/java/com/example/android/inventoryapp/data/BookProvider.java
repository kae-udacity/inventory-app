package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.data.BookContract.BookEntry;

/**
 * Provides content from the database through ContentResolver.
 */

public class BookProvider extends ContentProvider {

    private static final String TAG = BookProvider.class.getSimpleName();
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int BOOKS = 100;
    private static final int BOOK_ID = 101;

    static {
        URI_MATCHER.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        URI_MATCHER.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    private BookDbHelper bookDbHelper;

    @Override
    public boolean onCreate() {
        bookDbHelper = new BookDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = bookDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Error: unknown URI " + uri);
        }

        Context context = getContext();
        if (context != null) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion not supported for uri: " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {
        if (values.size() == 0) {
            return null;
        }

        if (values.containsKey(BookEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(BookEntry.COLUMN_PRODUCT_NAME);
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(getContext(), R.string.requires_name, Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        if (values.containsKey(BookEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(BookEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity == null || quantity < 0) {
                Toast.makeText(getContext(), R.string.requires_valid_quantity, Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        if (values.containsKey(BookEntry.COLUMN_PRODUCT_PRICE)) {
            Integer price = values.getAsInteger(BookEntry.COLUMN_PRODUCT_PRICE);
            if (price == null || price < 0) {
                Toast.makeText(getContext(), R.string.requires_valid_price, Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        if (values.containsKey(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            if (TextUtils.isEmpty(supplierName)) {
                Toast.makeText(getContext(), R.string.requires_supplier_name, Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        if (values.containsKey(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER)) {
            Integer supplierPhone = values.getAsInteger(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
            if (supplierPhone == null || supplierPhone < 0) {
                Toast.makeText(getContext(), R.string.requires_valid_supplier_phone, Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        SQLiteDatabase database = bookDbHelper.getWritableDatabase();
        long id = database.insert(BookEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(TAG, "Failed to insert row for " + uri);
            return null;
        }

        notifyChange(uri);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case BOOKS:
                notifyChange(uri);
                return updateBook(uri, values, selection, selectionArgs);
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateBook(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }

        if (values.containsKey(BookEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(BookEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                Toast.makeText(getContext(), R.string.requires_name, Toast.LENGTH_SHORT).show();
                return 0;
            }
        }

        if (values.containsKey(BookEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(BookEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity == null || quantity < 0) {
                Toast.makeText(getContext(), R.string.requires_valid_quantity, Toast.LENGTH_SHORT).show();
                return 0;
            }
        }

        if (values.containsKey(BookEntry.COLUMN_PRODUCT_PRICE)) {
            Integer price = values.getAsInteger(BookEntry.COLUMN_PRODUCT_PRICE);
            if (price == null || price < 0) {
                Toast.makeText(getContext(), R.string.requires_valid_price, Toast.LENGTH_SHORT).show();
                return 0;
            }
        }

        if (values.containsKey(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            if (supplierName == null) {
                Toast.makeText(getContext(), R.string.requires_supplier_name, Toast.LENGTH_SHORT).show();
                return 0;
            }
        }

        if (values.containsKey(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER)) {
            Integer supplierPhone = values.getAsInteger(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
            if (supplierPhone == null || supplierPhone < 0) {
                Toast.makeText(getContext(), R.string.requires_valid_supplier_phone, Toast.LENGTH_SHORT).show();
                return 0;
            }
        }

        SQLiteDatabase database = bookDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            notifyChange(uri);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    private void notifyChange(Uri uri) {
        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }
    }
}

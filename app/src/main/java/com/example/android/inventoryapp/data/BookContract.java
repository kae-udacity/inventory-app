package com.example.android.inventoryapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines constants for the inventory database.
 */

public final class BookContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOKS = "books";

    private BookContract() {
        throw new AssertionError("Instances of BookContract cannot be created.");
    }

    public static final class BookEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);
        public static final String TABLE_NAME = "books";
        public static final String _ID = "_id";
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_IMAGE = "image";
        public static final String COLUMN_PRODUCT_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_PRODUCT_SUPPLIER_EMAIL = "supplier_email";
        public static final String COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";
    }
}

package com.example.android.inventoryapp.activity;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.data.BookContract.BookEntry;
import com.example.android.inventoryapp.data.BookCursorAdapter;
import com.example.android.inventoryapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private BookCursorAdapter adapter;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        getLoaderManager().initLoader(0, null, this);

        adapter = new BookCursorAdapter(this, null);
        binding.listView.setAdapter(adapter);
        binding.listView.setEmptyView(binding.emptyView);
        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditProductActivity.class);
                Uri uri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditProductActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRODUCT_QUANTITY,
                BookEntry.COLUMN_PRODUCT_PRICE,
                BookEntry.COLUMN_PRODUCT_IMAGE,
                BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                BookEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL,
                BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER
        };

        return new CursorLoader(this, BookEntry.CONTENT_URI, projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }
}

package com.example.android.inventoryapp.activity;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.data.BookContract.BookEntry;
import com.example.android.inventoryapp.databinding.ActivityEditProductBinding;

public class EditProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ActivityEditProductBinding binding;
    private Uri currentBookUri;
    private boolean bookInfoChanged;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            bookInfoChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_product);

        currentBookUri = getIntent().getData();
        if (currentBookUri != null) {
            setTitle(getString(R.string.edit_book));
            invalidateOptionsMenu();
            getLoaderManager().initLoader(1, null, this);
        } else {
            setTitle(getString(R.string.add_book));
        }

        binding.name.setOnTouchListener(touchListener);
        binding.quantity.setOnTouchListener(touchListener);
        binding.price.setOnTouchListener(touchListener);
        binding.supplierName.setOnTouchListener(touchListener);
        binding.supplierEmail.setOnTouchListener(touchListener);
        binding.supplierPhone.setOnTouchListener(touchListener);

        binding.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQuantity = 0;
                if (binding.quantity.getText() != null && !TextUtils.isEmpty(binding.quantity.getText().toString())) {
                    currentQuantity = Integer.parseInt(binding.quantity.getText().toString());
                }

                if (currentQuantity > 0) {
                    currentQuantity--;
                    binding.quantity.setText(String.valueOf(currentQuantity));
                }
            }
        });

        binding.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQuantity = 0;
                if (binding.quantity.getText() != null && !TextUtils.isEmpty(binding.quantity.getText().toString())) {
                    currentQuantity = Integer.parseInt(binding.quantity.getText().toString());
                }

                currentQuantity++;
                binding.quantity.setText(String.valueOf(currentQuantity));
            }
        });

        binding.order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor = getContentResolver().query(currentBookUri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
                    String supplierPhone = cursor.getString(supplierPhoneColumnIndex);
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(getString(R.string.tel), supplierPhone, null));
                    startActivity(intent);
                    cursor.close();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!bookInfoChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (currentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
            binding.order.setVisibility(View.INVISIBLE);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                int result = saveBook();
                if (result >= 0) {
                    finish();
                }
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!bookInfoChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditProductActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int saveBook() {
        String name = binding.name.getText().toString().trim();
        String quantityString = binding.quantity.getText().toString().trim();
        String priceString = binding.price.getText().toString().trim();
        String supplierName = binding.supplierName.getText().toString().trim();
        String supplierEmail = binding.supplierEmail.getText().toString().trim();
        String supplierPhoneString = binding.supplierPhone.getText().toString().trim();

        if (currentBookUri == null && TextUtils.isEmpty(name) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierName) &&
                TextUtils.isEmpty(supplierEmail) && TextUtils.isEmpty(supplierPhoneString)) {
            return 0;
        }

        int priceInCents = -1;
        if (!TextUtils.isEmpty(priceString)) {
            priceInCents = (int) (Float.valueOf(priceString) * 100);
        }
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, name);
        values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, quantityString);
        values.put(BookEntry.COLUMN_PRODUCT_PRICE, priceInCents);
        values.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierName);
        values.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL, supplierEmail);
        values.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, supplierPhoneString);

        int result = -1;
        if (currentBookUri == null) {
            Uri uri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
            if (uri != null) {
                Toast.makeText(this, R.string.book_saved, Toast.LENGTH_SHORT).show();
                result = 1;
            }
        } else {
            result = getContentResolver().update(currentBookUri, values, null, null);
            if (result != 0) {
                Toast.makeText(this, R.string.book_updated, Toast.LENGTH_SHORT).show();
            }
        }
        return result;
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        if (currentBookUri != null) {
            int rowsDeleted = getContentResolver().delete(currentBookUri, null, null);

            String message = getString(R.string.error_deleting_book);
            if (rowsDeleted != 0) {
                message = getString(R.string.book_deleted);
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRODUCT_QUANTITY,
                BookEntry.COLUMN_PRODUCT_PRICE,
                BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                BookEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL,
                BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER
        };

        return new CursorLoader(this, currentBookUri,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_PRICE);
            int supplierNameColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierEmailColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL);
            int supplierPhoneColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);

            String name = cursor.getString(nameColumnIndex);
            String quantity = String.valueOf(cursor.getInt(quantityColumnIndex));
            int priceInCents = cursor.getInt(priceColumnIndex);
            String price = String.valueOf(priceInCents / 100d);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierEmail = cursor.getString(supplierEmailColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            binding.name.setText(name);
            binding.quantity.setText(quantity);
            binding.price.setText(price);
            binding.supplierName.setText(supplierName);
            binding.supplierEmail.setText(supplierEmail);
            binding.supplierPhone.setText(supplierPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        binding.name.setText(null);
        binding.quantity.setText(null);
        binding.price.setText(null);
        binding.supplierName.setText(null);
        binding.supplierEmail.setText(null);
        binding.supplierPhone.setText(null);
    }
}

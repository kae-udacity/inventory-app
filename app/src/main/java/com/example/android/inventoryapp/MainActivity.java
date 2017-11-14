package com.example.android.inventoryapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.inventoryapp.data.BookPresenter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BookPresenter bookPresenter = new BookPresenter(this);
        bookPresenter.insertBook("The Martian", 1199, "Del Rey", 555923391);
        bookPresenter.insertBook("Lord of the Flies", 899, "Faber", 555978057);
        bookPresenter.readData();
    }
}

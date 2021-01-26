package com.remon.books;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class CropImageActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
    }
}

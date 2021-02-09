package com.remon.books;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.remon.books.Function.Function_SharedPreference;

public class Main extends AppCompatActivity {

    Function_SharedPreference fs;

    /*
    뷰변수
     */
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        /*
        뷰연결
         */
        context = getApplicationContext();

        /*
        함수셋팅
         */
        fs = new Function_SharedPreference(context);

        fs.PREFERENCE = "member";
        String Unique_Value = fs.getPreferenceString("Unique_Value");
         Log.d("실행", "Unique_Value="+Unique_Value);


    }
}

package com.remon.books;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.remon.books.Data.Data_Search_Book;

public class PopUp_in_Search_Book extends AppCompatActivity {

    // 뷰연결
    TextView txt_title;
    Spinner category_read_status;
    RatingBar rating;
    Button btn_add;

    // 데이터(책) 객체
    Data_Search_Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_up_in__search__book);

        // 상태바 제거(전체화면 모드)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 뷰연결
        txt_title = findViewById(R.id.txt_title);
        category_read_status = findViewById(R.id.category_read_status);
        rating = findViewById(R.id.rating);
        btn_add = findViewById(R.id.btn_add);



        // (intent에서 온)값받기
        book = (Data_Search_Book) getIntent().getSerializableExtra("book");

        // 제목 셋팅
        txt_title.setText(book.getTitle());


    }

    // 해당 책을 mylist에 저장한다
    public void save_in_my_book_list(View view) {

    } // end save_in_my_book_list
}

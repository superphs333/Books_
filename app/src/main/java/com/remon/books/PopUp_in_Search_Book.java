package com.remon.books;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.remon.books.Data.Data_Search_Book;
import com.remon.books.Function.Function_SharedPreference;

import java.util.HashMap;
import java.util.Map;

public class PopUp_in_Search_Book extends AppCompatActivity {

    // 뷰연결
    TextView txt_title;
    Spinner category_read_status;
    RatingBar rating;
    Button btn_add;

    // 데이터(책) 객체
    Data_Search_Book book;

    // 함수모음
    Function_SharedPreference fshared;

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

        // 함수연결
        fshared = new Function_SharedPreference(getApplicationContext());

        // spinner 셋팅
        final String[] data = getResources().getStringArray(R.array.read_status);
        ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_dropdown_item_1line,data);
        category_read_status.setAdapter(adapter);



        // (intent에서 온)값받기
        book = (Data_Search_Book) getIntent().getSerializableExtra("book");

        // 제목 셋팅
        txt_title.setText(book.getTitle());


    }

    // 해당 책을 mylist에 저장한다
    public void save_in_my_book_list(View view) {

        /*
        보낼 값
         */
        final String title = book.getTitle();
        final String authors = book.getAuthors();
        final String publisher = book.getPublisher();
        final String isbn = book.getIsbn();
        final String contents = book.getContents();
        final String thumbnail = book.getThumbnail();
        final float rating_ = rating.getRating();
        final int status;
        if(category_read_status.getSelectedItem().toString().equals(getString(R.string.read_bucket))){
            // 읽고싶은
            status = 0;
        }else if(category_read_status.getSelectedItem().toString().equals(getString(R.string.read_reading))){
            // 읽는중
            status = 1;
        }else{
            // 읽음
            status = 2;
        }
        final String login_value = fshared.getPreferenceString("member","login_value");


        // 웹페이지 실행하기
        String url = getString(R.string.server_url)+"Book_Add_in_Search.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new com.android.volley.Response.Listener<String>() { // 정상 응답
                    @Override
                    public void onResponse(String response) {
                        Log.d("실행","response=>"+response);

                        String[] string_array= response.split(getString(R.string.seperator));
                        String result = string_array[string_array.length-1];
                        result = result.trim();
                        Log.d("실행", "result="+result);

                        if(result.equals("success")){
                            Toast.makeText(getApplicationContext()
                                    , "책등록을 마쳤습니다",Toast.LENGTH_LONG).show();

                            finish();
                        }else{
                            Toast.makeText(getApplicationContext()
                                    , "죄송합니다. 문제가 생겼습니다. 다시 시도해주세요",Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new com.android.volley.Response.ErrorListener() { // 에러 발생
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("실행","error=>"+error.getMessage());
                        Toast.makeText(getApplicationContext()
                                , "죄송합니다. 문제가 생겼습니다. 다시 시도해주세요",Toast.LENGTH_LONG).show();
                    }
                }

        ){ // Post 방식으로 body에 요청 파라미터를 넣어 전달하고 싶을 경우
            // 만약 헤더를 한 줄 추가하고 싶다면 getHeaders() override
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("title", title);
                params.put("authors",authors );
                params.put("publisher",publisher );
                params.put("isbn",isbn );
                params.put("contents",contents);
                params.put("thumbnail",thumbnail );
                params.put("rating_", String.valueOf(rating_));
                params.put("status", String.valueOf(status));
                params.put("login_value",login_value);
                return params;
            }
        };

        // 요청 객체를 만들었으니 이제 requestQueue 객체에 추가하면 됨.
        // Volley는 이전 결과를 캐싱하므로, 같은 결과가 있으면 그대로 보여줌
        // 하지만 아래 메소드를 false로 set하면 이전 결과가 있더라도 새로 요청해서 응답을 보여줌.
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);

    } // end save_in_my_book_list
}

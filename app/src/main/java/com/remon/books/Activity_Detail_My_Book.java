package com.remon.books;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.remon.books.Data.Data_My_Book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_Detail_My_Book extends AppCompatActivity {

    /*
    뷰변수
     */
    Context context;
    ImageView img_book,img_review_write;
    TextView txt_title, txt_authors, txt_publisher, txt_contents,txt_review;
    Spinner category_read_status;
    RatingBar rating;
    RecyclerView rv_book_memos;

    // unique_book_value
    String unique_book_value;

    // 객체
    Data_My_Book dmb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__detail__my__book);

        /*
        뷰연결
         */
        context = getApplicationContext();
        img_book = findViewById(R.id.img_book);
        txt_title = findViewById(R.id.txt_title);
        txt_authors = findViewById(R.id.txt_authors);
        txt_publisher = findViewById(R.id.txt_publisher);
        txt_contents = findViewById(R.id.txt_contents);
        category_read_status = findViewById(R.id.category_read_status);
        rating = findViewById(R.id.rating);
        rv_book_memos = findViewById(R.id.rv_book_memos);
        txt_review = findViewById(R.id.txt_review);
        img_review_write = findViewById(R.id.img_review_write);



        // Spinner셋팅
        final String[] data = getResources().getStringArray(R.array.read_status);
        ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(context,android.R.layout.simple_dropdown_item_1line,data);
        category_read_status.setAdapter(adapter);

        /*
        데이터 불러오기
         */
        unique_book_value = getIntent().getStringExtra("unique_book_value");
        Log.d("실행", "unique_book_value="+unique_book_value);


        // 카테고리 변경시 -> 데이터베이스 반영
        category_read_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 데이터베이스에 반영
                // 웹페이지 실행하기
                String url = getString(R.string.server_url)+"웹페이지주소";

                StringRequest request = new StringRequest(
                        Request.Method.POST,
                        url,
                        new com.android.volley.Response.Listener<String>() { // 정상 응답
                            @Override
                            public void onResponse(String response) {
                                Log.d("실행","response=>"+response);

                            }
                        },
                        new com.android.volley.Response.ErrorListener() { // 에러 발생
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("실행","error=>"+error.getMessage());
                            }
                        }

                ){ // Post 방식으로 body에 요청 파라미터를 넣어 전달하고 싶을 경우
                    // 만약 헤더를 한 줄 추가하고 싶다면 getHeaders() override
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("idx", "post로 넣을 값");
                        return params;
                    }
                };

                // 요청 객체를 만들었으니 이제 requestQueue 객체에 추가하면 됨.
                // Volley는 이전 결과를 캐싱하므로, 같은 결과가 있으면 그대로 보여줌
                // 하지만 아래 메소드를 false로 set하면 이전 결과가 있더라도 새로 요청해서 응답을 보여줌.
                request.setShouldCache(false);
                AppHelper.requestQueue.add(request);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });






    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
        unique_book_value에 해당하는 데이터 불러오기
         */
        RetrofitConnection retrofitConnection
                = new RetrofitConnection();
        Call<ArrayList<Data_My_Book>> call
                = retrofitConnection.server
                .Get_My_Book(unique_book_value);
        call.enqueue(new Callback<ArrayList<Data_My_Book>>() {
            @Override
            public void onResponse(Call<ArrayList<Data_My_Book>> call, Response<ArrayList<Data_My_Book>> response) {
                if(response.isSuccessful()){
                    Log.d("실행", "resonse=>"+response.message());

                    /*
                    값셋팅
                     */
                    dmb = response.body().get(0);

                    // 값셋팅
                    txt_title.setText(dmb.getTitle());
                    txt_authors.setText(dmb.getAuthors());
                    txt_publisher.setText(dmb.getPublisher());
                    txt_contents.setText(dmb.getContents());
                    category_read_status.setSelection(dmb.getStatus());
                    rating.setRating(dmb.getRating());
                    Glide.with(context).load(dmb.getThumbnail()).into(img_book);
                    txt_review.setText(dmb.getReview());

                }else{
                    Log.d("실행","서버에 연결은 되었으나 오류발생 ");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Data_My_Book>> call, Throwable t) {
                Log.d("실행", "onFailure: " + t.toString()); //서버와 연결 실패

            }
        });
    } // end onResume



    // 액티비티 Activity_Review_Write로 이동
    public void go_to_Activity_Review_Write(View view) {
        Intent intent = new Intent(context,Activity_Review_Write.class);
        intent.putExtra(getString(R.string.unique_book_value),unique_book_value);
        intent.putExtra("review",txt_review.getText().toString());
        startActivity(intent);
    }

    public class RetrofitConnection{
        String URL = getString(R.string.server_url);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JsonPlaceHolderApi server = retrofit.create(JsonPlaceHolderApi.class);
    }
}

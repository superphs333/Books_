package com.remon.books;
import android.app.Activity;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.remon.books.Adapter.Adapter_Book_Memo;
import com.remon.books.Data.Data_Book_Memo;
import com.remon.books.Data.Data_My_Book;
import com.remon.books.Function.Function_Set;
import com.remon.books.Function.Function_SharedPreference;

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
    Activity activity;
    ImageView img_book,img_review_write,img_add_memo;
    TextView txt_title, txt_authors, txt_publisher, txt_contents,txt_review;
    Spinner category_read_status;
    RatingBar rating_bar;
    RecyclerView rv_book_memos;

    // unique_book_value
    String unique_book_value;

    // 객체
    Data_My_Book dmb;

    // 함수
    Function_Set fs;
    Function_SharedPreference fshared;

    // 리싸이클러뷰
    ArrayList<Data_Book_Memo> arrayList;
    Adapter_Book_Memo mainAdapter;
    LinearLayoutManager linearLayoutManager;


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
        rating_bar = findViewById(R.id.rating_bar);
        rv_book_memos = findViewById(R.id.rv_book_memos);
        txt_review = findViewById(R.id.txt_review);
        img_review_write = findViewById(R.id.img_review_write);
        img_add_memo = findViewById(R.id.img_add_memo);


        // 함수
        fs = new Function_Set(context);
        fs.context = context;
        fshared = new Function_SharedPreference(context);




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
//        category_read_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                // status -> 읽고싶은:0, 읽는중:1, 읽음:2
//                int status;
//                if(category_read_status.getSelectedItem().toString().equals(getString(R.string.read_bucket))){
//                    // 읽고싶은
//                    status = 0;
//                }else if(category_read_status.getSelectedItem().toString().equals(getString(R.string.read_reading))){
//                    // 읽는중
//                    status = 1;
//                }else{
//                    // 읽음
//                    status = 2;
//                }
//
//                // 데이터베이스에 반영
//                // 웹페이지 실행하기
//                fs.Update_My_Book_Data(unique_book_value, "status", String.valueOf(status), new Function_Set.VolleyCallback() {
//                    @Override
//                    public void onSuccess(String result) {
//                        Log.d("실행","(int Acitivty Detail My Books)result="+result);
//
//                        if(result.equals("success")){
//                            //Toast.makeText(getApplicationContext(), "성공적으로 반영되었습니다",Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        // 별점 변경시 -> 데이터베이스에 반영
//        rating_bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//            @Override
//            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                fs.Update_My_Book_Data(unique_book_value, "rating", rating_bar.getRating() + "", new Function_Set.VolleyCallback() {
//                    @Override
//                    public void onSuccess(String result) {
//                        Log.d("실행","(int Acitivty Detail My Books)result="+result);
//
//                        if(result.equals("success")){
//                            //Toast.makeText(getApplicationContext(), "성공적으로 반영되었습니다",Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//            }
//        }); // end rating_bar.setOnRatingBarChangeListener






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
                    rating_bar.setRating(dmb.getRating());
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


        /*
        메모 데이터 불러오기
         */
        final String login_value = fshared.getPreferenceString(getString(R.string.member),getString(R.string.login_value));
        Get_Memo_Data(login_value);

    } // end onResume

    private void Get_Memo_Data(String login_value) {
        Log.d("실행", "unique_book_value="+unique_book_value+", login_value="+fshared.getPreferenceString(getString(R.string.member),getString(R.string.login_value)));

        RetrofitConnection retrofitConnection
                = new RetrofitConnection();
        Call<ArrayList<Data_Book_Memo>> call
                = retrofitConnection.server
                .Get_Book_Memo(login_value,unique_book_value);
        call.enqueue(new Callback<ArrayList<Data_Book_Memo>>() {
            @Override
            public void onResponse(Call<ArrayList<Data_Book_Memo>> call, Response<ArrayList<Data_Book_Memo>> response) {

                if(response.isSuccessful()){
                    Log.d("실행", "(Get_Memo_Data)resonse=>"+response.message());

                    arrayList = response.body();
                    mainAdapter = new Adapter_Book_Memo(arrayList,context,activity);
                    rv_book_memos.setAdapter(mainAdapter);
                    linearLayoutManager = new LinearLayoutManager(context);
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    rv_book_memos.setLayoutManager(linearLayoutManager);
                }else{
                    Log.d("실행","(Get_Memo_Data)서버에 연결은 되었으나 오류발생");

                }
            }



            @Override
            public void onFailure(Call<ArrayList<Data_Book_Memo>> call, Throwable t) {
                Log.d("실행", "onFailure: " + t.toString()); //서버와 연결 실패
            }
        });
    }


    // 액티비티 Activity_Review_Write로 이동
    public void go_to_Activity_Review_Write(View view) {
        Intent intent = new Intent(context,Activity_Review_Write.class);
        intent.putExtra(getString(R.string.unique_book_value),unique_book_value);
        intent.putExtra("review",txt_review.getText().toString());
        startActivity(intent);
    }

    // Activity_Add_Memo로 이동
    public void go_to_Activity_Add_Memo(View view) {
        Intent intent = new Intent(context,Activity_Add_Memo.class);
        intent.putExtra(getString(R.string.unique_book_value),unique_book_value);
        intent.putExtra("title",dmb.getTitle());
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

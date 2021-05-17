package com.remon.books;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.remon.books.Adapter.Adapter_Book_Memo;
import com.remon.books.Adapter.Adapter_Follow_People;
import com.remon.books.Data.Data_Follow_People;
import com.remon.books.Function.Function_SharedPreference;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_Management_Follow extends AppCompatActivity {

    String login_value = "";

    String sort = "follower";
    int count = 0;

    // 뷰
    TextView txt_count;
    Button btn_follower, btn_following;
    RecyclerView rv_follows;

    // 리사이클러뷰 관련
    Adapter_Follow_People mainAdapter;
    LinearLayoutManager linearLayoutManager;

    // 데이터
    ArrayList<Data_Follow_People> arrayList;

    // 함수
    Function_SharedPreference fshared;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__management__follow);



        /*
        뷰연결
         */
        txt_count = findViewById(R.id.txt_count);
        rv_follows = findViewById(R.id.rv_follows);
        btn_follower = findViewById(R.id.btn_follower);
        btn_following = findViewById(R.id.btn_following);
        txt_count.setVisibility(View.GONE);

        fshared = new Function_SharedPreference(getApplicationContext());
        login_value = fshared.get_login_value();
        Log.d("실행", "login_value="+login_value);

        // 버튼 초기색
        btn_follower.setBackgroundColor(Color.parseColor(getString(R.string.choose_true)));
        btn_following.setBackgroundColor(Color.parseColor(getString(R.string.choose_false)));

        /*
        버튼 => 해당 버튼에 알맞는 follow를 불러옴
        버튼 클릭시 -> sort, 색상 변경
         */
        btn_follower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort = "follower";
                btn_follower.setBackgroundColor(Color.parseColor(getString(R.string.choose_true)));
                btn_following.setBackgroundColor(Color.parseColor(getString(R.string.choose_false)));
                Get_Follow();
            }
        });
        btn_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort = "following";
                btn_follower.setBackgroundColor(Color.parseColor(getString(R.string.choose_false)));
                btn_following.setBackgroundColor(Color.parseColor(getString(R.string.choose_true)));
                Get_Follow();
            }
        });





    }

    @Override
    protected void onResume() {
        super.onResume();

        // 팔로우 데이터 불러오기
        Get_Follow();
    }

    private void Get_Follow() {

        Log.d("실행", "sort="+sort);

        RetrofitConnection retrofitConnection
                = new RetrofitConnection();
        Call<ArrayList<Data_Follow_People>> call
                = retrofitConnection
                .server
                .Get_Follow(login_value,sort);
        call.enqueue(new Callback<ArrayList<Data_Follow_People>>() {
            @Override
            public void onResponse(Call<ArrayList<Data_Follow_People>> call, Response<ArrayList<Data_Follow_People>> response) {
                if(response.isSuccessful()){
                    Log.d("실행", "Get_Follow success!");

                    if(response.body().size()!=0){
                        Log.d("실행", "test=>"+response.body().get(0).getProfile_url());
                    }



                    arrayList = response.body();
                    mainAdapter = new Adapter_Follow_People(arrayList,getApplicationContext(),Activity_Management_Follow.this);
                    rv_follows.setAdapter(mainAdapter);
                    linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    rv_follows.setLayoutManager(linearLayoutManager);
                    mainAdapter.mode_follow = sort;

                    count = response.body().size();
                    txt_count.setText(count+"명");



                }else{
                    Log.d("실행", "서버에 연결은 되었으나 문제가 발생");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Data_Follow_People>> call, Throwable t) {
                Log.d("실행", "오류->"+t.getMessage());
            }
        });
    } // end Get_Follow

    public class RetrofitConnection{
        String URL = getString(R.string.server_url);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JsonPlaceHolderApi server = retrofit.create(JsonPlaceHolderApi.class);
    }
}

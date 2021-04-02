package com.remon.books;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.remon.books.Adapter.Adapter_Book_Memo;
import com.remon.books.Data.Data_Book_Memo;
import com.remon.books.Data.Data_My_Book;
import com.remon.books.Function.Function_Set;
import com.remon.books.Function.Function_SharedPreference;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_Book_Memos extends AppCompatActivity {

    // 뷰
    TextView txt_title;
    Spinner spin_sort;
    RecyclerView rv_book_memos;

    // 변수
    String unique_book_value;
    String title;
    String login_value;

    // 리싸이클러뷰
    ArrayList<Data_Book_Memo> arrayList;
    Adapter_Book_Memo mainAdapter;
    LinearLayoutManager linearLayoutManager;

    // 함수
    Function_SharedPreference fshared;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__book__memos);

        // 뷰연결
        txt_title = findViewById(R.id.txt_title);
        spin_sort = findViewById(R.id.spin_sort);
        rv_book_memos = findViewById(R.id.rv_book_memos);

        // 함수
        fshared = new Function_SharedPreference(getApplicationContext());

        /*
        데이터 가져오기: unique_book_value,title
         */
        unique_book_value = getIntent().getStringExtra("unique_book_value");
        title = getIntent().getStringExtra("title");
        login_value = fshared.get_login_value();

        // 값셋팅
        txt_title.setText(title);

        /*
        spinner셋팅
         */
        final String[] data = getResources().getStringArray(R.array.select_memo_view);
        ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_dropdown_item_1line,data);
        spin_sort.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // 데이터 불러오기
        Get_Book_Memo_in_SNS();
    }

    // 메모 데이터 불러오기
    private void Get_Book_Memo_in_SNS() {

        // spinner 값 불러오기
        String selected = spin_sort.getSelectedItem().toString();
        Log.d("실행", "selected="+selected);


        RetrofitConnection retrofitConnection
                = new RetrofitConnection();
        Call<ArrayList<Data_Book_Memo>> call
                = retrofitConnection.server.Get_Book_Memo_in_SNS(login_value,unique_book_value,selected);
        call.enqueue(new Callback<ArrayList<Data_Book_Memo>>() {
            @Override
            public void onResponse(Call<ArrayList<Data_Book_Memo>> call, Response<ArrayList<Data_Book_Memo>> response) {
                if(response.isSuccessful()){
                    Log.d("실행", "(Get_Memo_Data)resonse=>"+response.message());

                    arrayList = response.body();
                    mainAdapter = new Adapter_Book_Memo(arrayList,getApplicationContext(),Activity_Book_Memos.this);
                    rv_book_memos.setAdapter(mainAdapter);
                    linearLayoutManager = new LinearLayoutManager(getApplicationContext());
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

    public void go_to_Activity_Add_Memo(View view) {
        Intent intent = new Intent(getApplicationContext(),Activity_Add_Memo.class);
        intent.putExtra(getString(R.string.unique_book_value),unique_book_value);
        intent.putExtra("title",title);
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

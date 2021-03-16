package com.remon.books;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.remon.books.Adapter.Adater_My_Book;
import com.remon.books.Data.Data_My_Book;
import com.remon.books.Function.Function_Set;
import com.remon.books.Function.Function_SharedPreference;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Fragment_Books extends Fragment implements View.OnClickListener {

    /*
    뷰변수
     */
    View v;
    Context context;
    Spinner category_read_status;
    EditText edit_search;
    RecyclerView rv_my_books;
    ImageView img_search;

    //FAB
    Animation fab_open, fab_close;
    Boolean openFlag = false;
    FloatingActionButton floating,floating_self,floating_search;

    /*
    함수
     */
    Function_SharedPreference fshared;
    Function_Set fs;

    // 데이터
    ArrayList<Data_My_Book> arrayList;

    // 리싸이클러뷰용 변수
    private Adater_My_Book mainAdapter;
    private LinearLayoutManager linearLayoutManager;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /*
        뷰연결
         */
        LayoutInflater inflater1 = inflater;
        v = inflater.inflate(R.layout.fragment__books,container,false);
        context = v.getContext();
        floating = v.findViewById(R.id.floating);
        floating_self = v.findViewById(R.id.floating_self);
        floating_search = v.findViewById(R.id.floating_search);
        category_read_status = v.findViewById(R.id.category_read_status);
        edit_search = v.findViewById(R.id.edit_search);
        rv_my_books = v.findViewById(R.id.rv_my_books);
        img_search = v.findViewById(R.id.img_search);


        /*
        함수
         */
        fshared = new Function_SharedPreference(context);

        /*
        floating버튼 설정
         */
        // floating button
        fab_open = AnimationUtils.loadAnimation(v.getContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(v.getContext(),R.anim.fab_close);
        // 버튼 상태 초기화(닫혀있어야 함)
        floating_self.startAnimation(fab_close);
        floating_search.startAnimation(fab_close);
        // 초기 클릭 불가능
        floating_self.setClickable(false);
        floating_search.setClickable(false);
        // 클릭시 동작
        floating.setOnClickListener(this);
        floating_self.setOnClickListener(this);
        floating_search.setOnClickListener(this);

        /*
        카테고리 값 셋팅
         */
        final String[] data = v.getResources().getStringArray(R.array.read_status_books);
        ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(v.getContext(),android.R.layout.simple_dropdown_item_1line,data);
        category_read_status.setAdapter(adapter);



        // 카테고리 변경시 -> 적절한 데이터 불러오기
        category_read_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Get_My_Books();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 돋보기 버튼(img_search) -> 적절한 데이터 불러오기
        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Get_My_Books();
            }
        });

        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        // 데이터 불러오기
        Get_My_Books();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.floating:
                anim();
                break;
            case R.id.floating_search:
                anim();
                Log.d("실행", "찾기");
                Intent intent = new Intent(context,Activity_Book_Search.class);
                startActivity(intent);
                break;
            case R.id.floating_self:
                anim();
                Log.d("실행", "직접추가");
                Intent intent2 = new Intent(context,Activity_Book_Add.class);
                startActivity(intent2);
                break;
        }
    } // end onClick

    public void anim() {

        if (openFlag) {
            floating_self.startAnimation(fab_close);
            floating_search.startAnimation(fab_close);
            floating_self.setClickable(false);
            floating_search.setClickable(false);
            openFlag = false;
        } else {
            floating_self.startAnimation(fab_open);
            floating_search.startAnimation(fab_open);
            floating_self.setClickable(true);
            floating_search.setClickable(true);
            openFlag = true;
        }
    }


    // 책 데이터 가져오기
    private void Get_My_Books(){

        // 보낼값
        String login_value = fshared.getPreferenceString("member","login_value");
        String temp_status = category_read_status.getSelectedItem().toString();
        int status=4;
        if(temp_status.equals(getString(R.string.read_bucket))){
            status = 0;
        }else if(temp_status.equals(getString(R.string.read_reading))){
            status = 1;
        }else if(temp_status.equals(getString(R.string.read_end))){
            status = 2;
        }
        String search = edit_search.getText().toString();

        Log.d("실행", "status="+status+", search="+search);


        RetrofitConnection retrofitConnection
                = new RetrofitConnection();
        Call<ArrayList<Data_My_Book>> call
                = retrofitConnection
                .server
                .Get_My_Books(login_value,status,search);
        call.enqueue(new Callback<ArrayList<Data_My_Book>>() {
            @Override
            public void onResponse(Call<ArrayList<Data_My_Book>> call, Response<ArrayList<Data_My_Book>> response) {
                // 성공적으로 서버에서 데이터 불러옴
                if(response.isSuccessful()){
                    Log.d("실행", String.valueOf(response.code()));

                    // 결과값 임시 확인
                    //Log.d("실행",response.body().get(0).getTitle());

                    /*
                    리사이클러뷰 셋팅
                     */
                    //arrayList = new ArrayList<>();
                    arrayList = response.body();
                    mainAdapter = new Adater_My_Book(arrayList,context);
                    rv_my_books.setAdapter(mainAdapter);
                    linearLayoutManager = new LinearLayoutManager(context);
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    rv_my_books.setLayoutManager(linearLayoutManager);

                }else{
                    Log.d("실행","서버에 연결은 되었으나 오류발생");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Data_My_Book>> call, Throwable t) {
                Log.d("실행", "onFailure: " + t.toString()); //서버와 연결 실패
            }
        });
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

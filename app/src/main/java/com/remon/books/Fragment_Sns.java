package com.remon.books;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gun0912.tedpermission.PermissionListener;
import com.remon.books.Adapter.Adapter_Book_Memo;
import com.remon.books.Data.Data_Book_Memo;
import com.remon.books.Function.Function_Set;
import com.remon.books.Function.Function_SharedPreference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;


public class Fragment_Sns extends Fragment {

    /*
    뷰변수
     */
    View v;
    Activity activity;
    Context context;
    Spinner spinner_open;
    CheckBox chk_like_post;
    RecyclerView rv_book_memos;

    // 리사이클러뷰
    Adapter_Book_Memo mainAdapter;
    ArrayList<Data_Book_Memo> arraylist;
    LinearLayoutManager linearLayoutManager;

    // 함수
    Function_SharedPreference fshared;
    Function_Set fs;

    // 필요변수
    String login_value;
    boolean chk_like;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("실행", "==========Fragment_mypage onCreateView==========");


        /*
        뷰연결
         */
        LayoutInflater inflater1 = inflater;
        v = inflater.inflate(R.layout.fragment__sns,container,false);
        activity = getActivity();
        context = v.getContext();
        spinner_open = v.findViewById(R.id.spinner_open);
        chk_like_post = v.findViewById(R.id.chk_like_post);
        rv_book_memos = v.findViewById(R.id.rv_book_memos);

        /*
        spinner셋팅
         */
        final String[] data = getResources().getStringArray(R.array.select_open2);
        ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(context,android.R.layout.simple_dropdown_item_1line,data);
        spinner_open.setAdapter(adapter);



        // 함수셋팅
        fshared = new Function_SharedPreference(context);
        fs = new Function_Set(context,activity);

        // 변수셋팅
        login_value = fshared.get_login_value();


        // spinner변경 -> 해당 데이터
        spinner_open.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Bring_Memo_Datas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 체크박스 변경경










        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    private void Bring_Memo_Datas(){

        String view = spinner_open.getSelectedItem().toString();
        Log.d("실행", "chk_like_post->"+chk_like_post);


        RetrofitConnection retrofitConnection
                = new RetrofitConnection();
        Call<ArrayList<Data_Book_Memo>> call
                = retrofitConnection.server.Get_Book_Memo_in_SNS(login_value,"",view,chk_like_post.isChecked());
        call.enqueue(new Callback<ArrayList<Data_Book_Memo>>() {
            @Override
            public void onResponse(Call<ArrayList<Data_Book_Memo>> call, Response<ArrayList<Data_Book_Memo>> response) {
                if(response.isSuccessful()){
                    arraylist = response.body();
                    mainAdapter = new Adapter_Book_Memo(arraylist,context,activity);
                    rv_book_memos.setAdapter(mainAdapter);
                    linearLayoutManager = new LinearLayoutManager(context);
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    rv_book_memos.setLayoutManager(linearLayoutManager);
                }else{
                    Log.d("실행", "서버에 연결은 되었으나. 오류 발생");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Data_Book_Memo>> call, Throwable t) {
                Log.d("실행", "에러=>"+t.getMessage());
            }
        });
    } // end Bring_Memo_Datas

    public class RetrofitConnection{
        String URL = getString(R.string.server_url);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JsonPlaceHolderApi server = retrofit.create(JsonPlaceHolderApi.class);
    }



}

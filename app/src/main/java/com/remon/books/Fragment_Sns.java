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

import retrofit2.Retrofit;

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
    Data_Book_Memo arraylist;
    LinearLayoutManager linearLayoutManager;

    // 함수
    Function_SharedPreference fshared;
    Function_Set fs;




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













        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    private void Bring_Memo_Datas(){
//        Activity_Detail_My_Book.RetrofitConnection
    } // end Bring_Memo_Datas


}

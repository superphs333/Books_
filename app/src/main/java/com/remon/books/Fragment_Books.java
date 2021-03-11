package com.remon.books;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class Fragment_Books extends Fragment implements View.OnClickListener {

    /*
    뷰변수
     */
    View v;
    Context context;
    Spinner category_read_status;

    //FAB
    Animation fab_open, fab_close;
    Boolean openFlag = false;
    FloatingActionButton floating,floating_self,floating_search;



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
        final String[] data = v.getResources().getStringArray(R.array.read_status);
        ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(v.getContext(),android.R.layout.simple_dropdown_item_1line,data);
        category_read_status.setAdapter(adapter);




        // Inflate the layout for this fragment
        return v;
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
}

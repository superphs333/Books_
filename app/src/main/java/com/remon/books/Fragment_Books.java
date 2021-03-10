package com.remon.books;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class Fragment_Books extends Fragment implements View.OnClickListener {

    /*
    뷰변수
     */
    View v;

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
        floating = v.findViewById(R.id.floating);
        floating_self = v.findViewById(R.id.floating_self);
        floating_search = v.findViewById(R.id.floating_search);

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
                break;
            case R.id.floating_self:
                anim();
                Log.d("실행", "직접추가");
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

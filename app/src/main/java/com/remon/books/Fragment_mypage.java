package com.remon.books;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.adapters.ImageViewBindingAdapter;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.remon.books.Function.Function_SharedPreference;


public class Fragment_mypage extends Fragment {

    /*
    뷰변수
     */
    View v;
    Activity activity;
    Context context;
    ImageView img_profile,img_setting;
    TextView txt_nickname;

    // 함수
    Function_SharedPreference fshared;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /*
        뷰연결
         */
        LayoutInflater inflater1 = inflater;
        v = inflater.inflate(R.layout.fragment_mypage,container,false);
        activity = getActivity();
        context = v.getContext();
        img_profile = v.findViewById(R.id.img_profile);
        img_setting = v.findViewById(R.id.img_setting);
        txt_nickname = v.findViewById(R.id.txt_nickname);

        // 함수셋팅
        fshared = new Function_SharedPreference(context);
        fshared.PREFERENCE = "member";

        /*
        프로필, 닉네임 셋팅 (서버에서 가져온다)
        by member에 저장된(sharedpreference)
         */
        Log.d("실행", "nickname="+fshared.getPreferenceString("nickname"));
        Log.d("실행", "profile_url="+fshared.getPreferenceString("profile_url"));
        Glide.with(context).load(fshared.getPreferenceString("profile_url")).into(img_profile);
        txt_nickname.setText(fshared.getPreferenceString("nickname"));

        // 세팅 버튼 => Acitvity_Setting으로 이동
        img_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,Activity_Setting.class);
                startActivity(intent);
            }
        });







        // Inflate the layout for this fragment
        return v;
    }


}

package com.remon.books;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.remon.books.Function.Function_Set;
import com.remon.books.Function.Function_SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Main extends AppCompatActivity {

    // 함수
    Function_SharedPreference fshared;
    Function_Set fs;

    /*
    뷰변수
     */
    Context context;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    // 프래그먼트
    Fragment_Books fragment_books;
    Fragment_mypage fragment_mypage;
    Fragment_Chatting_Room fragment_chatting_room;
    Fragment_Sns fragment_sns;


    // 버튼
    Button btn_books, btn_gathering, btn_feed, btn_record,btn_mypage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        /*
        뷰연결
         */
        context = getApplicationContext();
        btn_books = findViewById(R.id.btn_books);
        btn_gathering = findViewById(R.id.btn_gathering);
        btn_feed = findViewById(R.id.btn_feed);
        btn_record = findViewById(R.id.btn_record);
        btn_mypage = findViewById(R.id.btn_mypage);

        /*
        함수셋팅
         */
        fshared = new Function_SharedPreference(context);
        fs = new Function_Set(context);
        fs.context = context;



        /*
        프래그 먼트
         */
        // 프래그먼트 매니저 선언
            // 프래그먼트 트랜젝션 수행(프래그먼트 추가/삭제/교체) 을 하기 위해 필요
        fragmentManager = getSupportFragmentManager();

        // 추가 시켜 줄 프래그먼트 객체 생성
        fragment_books = new Fragment_Books();
        fragment_mypage = new Fragment_mypage();
        fragment_chatting_room = new Fragment_Chatting_Room();
        fragment_sns = new Fragment_Sns();


        // 프래그먼트 트랜잭션 시작
            // 여기에서 프래그먼트 트랜잭션, 백스택, 애니메이션 등을 설정한다
        fragmentTransaction
                 = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment_books).commitAllowingStateLoss();
            // replace() = 이전 프래그먼트를 제거한 후에 새로운 프래그먼트를 추가한다



        /*
        프로필, 닉네임 SharedPreference에 저장한다
        by login_value값
         */
        // 프로필, 닉네임 불러오기
        String login_value = fshared.getPreferenceString("member","login_value");
        Log.d("실행", "login_value="+login_value);
        fs.bring_nick_profile(login_value,"nickname"); // 닉네임
        fs.bring_nick_profile(login_value,"profile_url"); // 프로필 이미지









    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("실행", "=====Main onResume======");

    }

    // 각 버튼을 클릭하면 해당 프래그먼트들이 보인다
    public void clickHandler(View view) {
        fragmentTransaction = fragmentManager.beginTransaction();

        Log.d("실행", "VIEW.GETID="+view.getId());
        switch (view.getId()){
            case R.id.btn_books:
                fragmentTransaction.replace(R.id.frameLayout,fragment_books).commitAllowingStateLoss();
                break;

            case R.id.btn_mypage:
                fragmentTransaction.replace(R.id.frameLayout,fragment_mypage).commitAllowingStateLoss();
                break;

            case R.id.btn_gathering:
                fragmentTransaction.replace(R.id.frameLayout,fragment_chatting_room).commitAllowingStateLoss();
                break;

//            case R.id.btn_chatting:
//                fragmentTransaction.replace(R.id.frameLayout,Chatting).commitAllowingStateLoss();
//                break;
//
            case R.id.btn_feed:
                fragmentTransaction.replace(R.id.frameLayout,fragment_sns).commitAllowingStateLoss();
                break;
        }

    } // end clickHandler
}

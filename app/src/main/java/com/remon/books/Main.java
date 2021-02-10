package com.remon.books;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.remon.books.Function.Function_SharedPreference;

public class Main extends AppCompatActivity {

    Function_SharedPreference fs;

    /*
    뷰변수
     */
    Context context;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    // 프래그먼트
    Fragment_Books fragment_books;
    Fragment_mypage fragment_mypage;

    // 버튼
    Button btn_books, btn_gathering, btn_chatting, btn_record,btn_mypage;


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
        btn_chatting = findViewById(R.id.btn_chatting);
        btn_record = findViewById(R.id.btn_record);
        btn_mypage = findViewById(R.id.btn_mypage);


        /*
        프래그 먼트
         */
        // 프래그먼트 매니저 선언
            // 프래그먼트 트랜젝션 수행(프래그먼트 추가/삭제/교체) 을 하기 위해 필요
        fragmentManager = getSupportFragmentManager();

        // 추가 시켜 줄 프래그먼트 객체 생성
        fragment_books = new Fragment_Books();
        fragment_mypage = new Fragment_mypage();

        // 프래그먼트 트랜잭션 시작
            // 여기에서 프래그먼트 트랜잭션, 백스택, 애니메이션 등을 설정한다
        fragmentTransaction
                 = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment_books).commitAllowingStateLoss();
            // replace() = 이전 프래그먼트를 제거한 후에 새로운 프래그먼트를 추가한다




        /*
        함수셋팅
         */
        fs = new Function_SharedPreference(context);





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
//
//            case R.id.btn_gathering:
//                fragmentTransaction.replace(R.id.frameLayout,Gathering).commitAllowingStateLoss();
//                break;
//
//            case R.id.btn_chatting:
//                fragmentTransaction.replace(R.id.frameLayout,Chatting).commitAllowingStateLoss();
//                break;
//
//            case R.id.btn_community:
//                fragmentTransaction.replace(R.id.frameLayout,COMMUNITY).commitAllowingStateLoss();
//                break;
        }

    } // end clickHandler
}

package com.remon.books;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.remon.books.Adapter.Adapter_Book_Memo;
import com.remon.books.Data.Data_Book_Memo;
import com.remon.books.Function.Function_Set;
import com.remon.books.Function.Function_SharedPreference;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_Feed extends AppCompatActivity {

    // 뷰
    ImageView img_profile;
    TextView txt_nickname, txt_follow;
    RecyclerView rv_memos;

    // 함수
    Function_SharedPreference fshared;
    Function_Set fs;

    // 필요변수
    boolean follow;
    String target_login_value;

    // 리사이클러뷰용
    Adapter_Book_Memo mainAdapter;
    ArrayList<Data_Book_Memo> arraylist;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__feed);

        // 뷰연결
        img_profile = findViewById(R.id.img_profile);
        txt_nickname = findViewById(R.id.txt_nickname);
        txt_follow = findViewById(R.id.txt_follow);
        rv_memos = findViewById(R.id.rv_memos);

        // 함수셋팅
        fshared = new Function_SharedPreference(getApplicationContext());
        fs = new Function_Set(getApplicationContext(),Activity_Feed.this);

         /*
        프로필, 닉네임,팔로우 셋팅
        by Intent에서 가져온
         */
        String nickname = getIntent().getStringExtra("nickname");
        String profile_url = getIntent().getStringExtra("profile_url");
        Glide.with(getApplicationContext()).load(profile_url).into(img_profile);
        txt_nickname.setText(nickname);
        // login_value(상대)
        target_login_value = getIntent().getStringExtra("login_value");
        // 팔로우
        follow = getIntent().getBooleanExtra("follow",false);
        if(follow==true){ // 이미 팔로우 상태 -> txt_follow="팔로우 취소"
            txt_follow.setText("팔로우 취소");
        }else{ // 팔로우 상태x -> txt_follow Visible
            txt_follow.setText("팔로우");
        }

        Bring_Memo_Datas();



    }

    public void Management_Follow(View view) {
        String management="";
        if(follow==true){ // 팔로우 -> 언팔로우 : delete_following
            management = "delete_following";
        }else{ // 팔로우x -> 팔로우 : following
            management = "following";
        }
        fs.Management_Follow(fshared.get_login_value(), target_login_value, management, new Function_Set.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("실행", "");

                String[] string_array= result.trim().split("§");
                if(string_array[0].equals("success")){
                    Toast.makeText(getApplicationContext(), "정상적으로 처리되었습니다!",Toast.LENGTH_LONG).show();
                    if(follow==true){ // 팔로우 -> 언팔로우 : delete_following
                        follow = false;
                        txt_follow.setText("팔로우");
                    }else{ // 팔로우x -> 팔로우 : following
                        follow = true;
                        txt_follow.setText("팔로우 취소");
                    }

                    // 리사이클러뷰 리스트들의 follow값도 변경
                    for(int i=0; i<arraylist.size(); i++){
                        arraylist.get(i).follow = follow;
                    }// end for
                    mainAdapter.notifyDataSetChanged();

                }else{
                    Toast.makeText(getApplicationContext(), "문제가 생겼습니다",Toast.LENGTH_LONG).show();

                }
            }
        }); // end fs.Management_Follow
    }

    // 데이터 가져오기
    private void Bring_Memo_Datas(){

        RetrofitConnection retrofitConnection
                = new RetrofitConnection();
        Call<ArrayList<Data_Book_Memo>> call
                = retrofitConnection.server.Get_Book_Memo_in_SNS(target_login_value,"","내메모",false);
        call.enqueue(new Callback<ArrayList<Data_Book_Memo>>() {
            @Override
            public void onResponse(Call<ArrayList<Data_Book_Memo>> call, Response<ArrayList<Data_Book_Memo>> response) {
                if(response.isSuccessful()){
                    arraylist = response.body();
                    mainAdapter = new Adapter_Book_Memo(arraylist,getApplicationContext(),Activity_Feed.this);
                    rv_memos.setAdapter(mainAdapter);
                    linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    rv_memos.setLayoutManager(linearLayoutManager);
                    for(int i=0; i<arraylist.size(); i++){
                        arraylist.get(i).follow = follow;
                    }// end for
                    mainAdapter.notifyDataSetChanged();
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

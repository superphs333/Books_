package com.remon.books;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.remon.books.Adapter.Adapter_Follow_People;
import com.remon.books.Adapter.Adapter_Join_People;
import com.remon.books.Data.Data_Join_People;
import com.remon.books.Function.Function_Set;
import com.remon.books.Function.Function_SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.lang.Thread.sleep;

public class Activity_Chatting_Room extends AppCompatActivity {

    // 뷰변수
    TextView txt_title, txt_explain,txt_count,txt_total;
    Button btn_join, btn_enter;
    RecyclerView rv_peoples;

    // 리사이클러뷰
    Adapter_Join_People mainAdapter;
    ArrayList<Data_Join_People> arrayList;
    LinearLayoutManager linearLayoutManager;

    // 함수
    Function_SharedPreference fshared;
    Function_Set fs;

    int idx; // room_idx
    String login_value;
    boolean state;
    int count = 0;
    int total = 0;
    String leader; // 리더


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__chatting__room);

        idx = Integer.parseInt(getIntent().getStringExtra("idx"));
        Log.d("실행", "(in Activity_Chatting_Room)idx="+idx);

        // 뷰연결
        txt_title = findViewById(R.id.txt_title);
        txt_explain = findViewById(R.id.txt_explain);
        txt_count = findViewById(R.id.txt_count);
        txt_total = findViewById(R.id.txt_total);
        rv_peoples = findViewById(R.id.rv_peoples);
        btn_join = findViewById(R.id.btn_join);
        btn_enter = findViewById(R.id.btn_enter);

        // 리사이클러뷰 관련
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_peoples.setLayoutManager(linearLayoutManager);

        // 함수연결
        fs = new Function_Set(getApplicationContext());
        fshared = new Function_SharedPreference(getApplicationContext());
        login_value = fshared.get_login_value();




        // 채팅룸 데이터 가져오기
        get_Chatting_Room_Data();

    }

    // 채팅룸 데이터 가져오기
    public void get_Chatting_Room_Data(){
        // 웹페이지 실행하기
        String url = getString(R.string.server_url)+"Data/Get_Chatting_Room_Data.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new com.android.volley.Response.Listener<String>() { // 정상 응답
                    @Override
                    public void onResponse(String response) {
                        Log.d("실행","response=>"+response);

                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            txt_title.setText(jsonArray.getJSONObject(0).getString("title"));
                            txt_explain.setText(jsonArray.getJSONObject(0).getString("room_explain"));
                            txt_count.setText(jsonArray.getJSONObject(0).getString("join_count"));
                            txt_total.setText(jsonArray.getJSONObject(0).getString("total_count"));
                            count = Integer.parseInt(jsonArray.getJSONObject(0).getString("join_count"));
                            total = Integer.parseInt(jsonArray.getJSONObject(0).getString("total_count"));
                            leader = jsonArray.getJSONObject(0).getString("leader");

                            // 참여자 불러오기
                            get_Join_Peoples();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() { // 에러 발생
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("실행","error=>"+error.getMessage());
                    }
                }

        ){ // Post 방식으로 body에 요청 파라미터를 넣어 전달하고 싶을 경우
            // 만약 헤더를 한 줄 추가하고 싶다면 getHeaders() override
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idx", String.valueOf(idx));
                return params;
            }
        };

        // 요청 객체를 만들었으니 이제 requestQueue 객체에 추가하면 됨.
        // Volley는 이전 결과를 캐싱하므로, 같은 결과가 있으면 그대로 보여줌
        // 하지만 아래 메소드를 false로 set하면 이전 결과가 있더라도 새로 요청해서 응답을 보여줌.
        request.setShouldCache(false);
        if(AppHelper.requestQueue==null){
            AppHelper.requestQueue = Volley.newRequestQueue(Activity_Chatting_Room.this);
        }
        AppHelper.requestQueue.add(request);

    }

    public void get_Join_Peoples(){
        Log.d("실행", "get_Join_Peoples");

        RetrofitConnection retrofitConnection
                = new RetrofitConnection();
        Call<ArrayList<Data_Join_People>> call
                = retrofitConnection.server.Get_Join_Peoples(idx,fshared.get_login_value());
        call.enqueue(new Callback<ArrayList<Data_Join_People>>() {
            @Override
            public void onResponse(Call<ArrayList<Data_Join_People>> call, Response<ArrayList<Data_Join_People>> response) {
                if(response.isSuccessful()){
                    Log.d("실행", "get_Join_Peoples SUCCESS!");
                    Log.d("실행", "leader="+leader);

                    arrayList = response.body();
                    mainAdapter = new Adapter_Join_People(arrayList,getApplicationContext(),Activity_Chatting_Room.this,leader);
                    rv_peoples.setAdapter(mainAdapter);

                    // 만약, 참여인원중에 내가 포함되어 있다면 -> btn_join.setText("나가기")
                    int check = 0;
                    for(int i=0; i<arrayList.size(); i++){
                        if(arrayList.get(i).getLogin_value().equals(login_value)){
                            check ++;
                            break;
                        }
                    }// end for
                    if(check>0){
                        btn_join.setText("나가기");
                        btn_enter.setVisibility(View.VISIBLE);
                        state = true;
                    }else{
                        btn_enter.setVisibility(View.GONE);

                        if(count==total){
                            btn_join.setText("대기하기");

                        }else{
                            btn_join.setText("참여하기");
                        }
                        state = false;
                    }

                    // join_count 업데이트
                    txt_count.setText(arrayList.size()+"");
                }else{
                    Log.d("실행", "서버에 연결되었으나 문제가 생김");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Data_Join_People>> call, Throwable t) {
                 Log.d("실행", "에러=>"+t.getMessage());

            }
        });


    } // end get_Join_Peoples()

    public void onClick(View view) {
        int id = view.getId();

        if(id==R.id.btn_join){ // 참여하기

            // 존재하는 방인지 확인
            fs.check_room(idx, new Function_Set.VolleyCallback() {
                @Override
                public void onSuccess(String result) {

                    Log.d("실행", "(후)result="+result);



                    // 방이 존재하지 않으면 -> 함수 빠져나감
                    if(result.trim().equals("false")){
                        Log.d("실행", "false");
                        return;
                    }
                    Log.d("실행", "true");

                    // 참가자 다시 불러오기
                    get_Join_Peoples();

                    try{
                        sleep(3000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }

                    Log.d("실행", "txt_count=>"+txt_count.getText().toString());



                    // 만약, 참여인원이 1명이라면(+현재 회원이 참여중인 상태) => 방삭제 알림
                    if(txt_count.getText().toString().equals("1") && state==true ){

                        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Chatting_Room.this);
                        builder.setTitle("알림"); //AlertDialog의 제목 부분
                        builder.setMessage("방의 마지막 인원이 나가게 되면 방이 삭제됩니다. 방을 삭제하시겠습니까?"); //AlertDialog의 내용 부분
                        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("실행","예 누름");

                                // 방 삭제하기
                                Delete_Chatting_Room();
                            }
                        });
                        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("실행","아니요 누름");
                            }
                        });
                        builder.setNeutralButton("취소", null);
                        builder.create().show(); //보이기
                    }else{
                        // 방장이 나가는 경우
                        if(login_value.equals(leader)){

                            Log.d("실행", "방장");

                            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Chatting_Room.this);
                            builder.setTitle("알림"); //AlertDialog의 제목 부분
                            builder.setMessage("방장이 나가게 되는 경우 다음 사람이 방장을 위임받게 됩니다. 나가시겠습니까?"); //AlertDialog의 내용 부분
                            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d("실행","예 누름");

                                    // 방장 변경
                                    leader = arrayList.get(1).getLogin_value();

                                    fs.Management_Join_Chatting_Room(idx, login_value, state,arrayList.get(1).getLogin_value() ,new Function_Set.VolleyCallback() {
                                        @Override
                                        public void onSuccess(String result) {

                                            String[] string_array= result.split("§");

                                            if(string_array[0].equals("success")){
                                                get_Join_Peoples();
                                                txt_count.setText(string_array[1]+"");
                                                count = Integer.parseInt(string_array[1]);
                                            }else if(string_array[0].equals("Duplicate")){
                                                Toast.makeText(getApplicationContext(), "이미 대기중인 상태입니다",Toast.LENGTH_LONG).show();
                                            }else{
                                                Toast.makeText(getApplicationContext(), "죄송합니다.문제가 발생하였습니다.",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            });
                            builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d("실행","아니요 누름");
                                }
                            });
                            builder.setNeutralButton("취소", null);
                            builder.create().show(); //보이기


                        }else{
                            fs.Management_Join_Chatting_Room(idx, login_value, state, new Function_Set.VolleyCallback() {
                                @Override
                                public void onSuccess(String result) {

                                    String[] string_array= result.split("§");

                                    if(string_array[0].equals("success")){
                                        get_Join_Peoples();
                                        txt_count.setText(string_array[1]+"");
                                        count = Integer.parseInt(string_array[1]);
                                    }else if(string_array[0].equals("Duplicate")){
                                        Toast.makeText(getApplicationContext(), "이미 대기중인 상태입니다",Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(getApplicationContext(), "죄송합니다.문제가 발생하였습니다.",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }


                    }
                }
            }); // end volley



        }else if(id==R.id.btn_enter){ // 채팅방 입장하기
            Intent intent = new Intent(getApplicationContext(),Activity_Chatting.class);
            intent.putExtra("room_idx",idx);
            intent.putExtra("title",txt_title.getText().toString());
            startActivity(intent);
        }
    } // end Onclick

    // 채팅방 삭제
    private void Delete_Chatting_Room() {
        // 웹페이지 실행하기
        String url = getString(R.string.server_url)+"Delete_Chatting_Room.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new com.android.volley.Response.Listener<String>() { // 정상 응답
                    @Override
                    public void onResponse(String response) {
                        Log.d("실행","response=>"+response);

                        String[] string_array= response.split("§");

                        if(string_array[0].equals("success") && string_array[1].equals("success")){
                            Toast.makeText(getApplicationContext(), "삭제되었습니다.",Toast.LENGTH_LONG).show();
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(), "죄송합니다.문제가 발생하였습니다.",Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() { // 에러 발생
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("실행","error=>"+error.getMessage());
                    }
                }

        ){ // Post 방식으로 body에 요청 파라미터를 넣어 전달하고 싶을 경우
            // 만약 헤더를 한 줄 추가하고 싶다면 getHeaders() override
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idx", String.valueOf(idx));
                return params;
            }
        };

        // 요청 객체를 만들었으니 이제 requestQueue 객체에 추가하면 됨.
        // Volley는 이전 결과를 캐싱하므로, 같은 결과가 있으면 그대로 보여줌
        // 하지만 아래 메소드를 false로 set하면 이전 결과가 있더라도 새로 요청해서 응답을 보여줌.
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);

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

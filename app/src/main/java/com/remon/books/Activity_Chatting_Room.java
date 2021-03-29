package com.remon.books;

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
import com.remon.books.Adapter.Adapter_Follow_People;
import com.remon.books.Adapter.Adapter_Join_People;
import com.remon.books.Data.Data_Join_People;
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

    // idx
    int idx;

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

        // 리사이클러뷰 관련
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_peoples.setLayoutManager(linearLayoutManager);

        // 함수연결
        fshared = new Function_SharedPreference(getApplicationContext());


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
                    arrayList = response.body();
                    mainAdapter = new Adapter_Join_People(arrayList,getApplicationContext(),Activity_Chatting_Room.this);
                    rv_peoples.setAdapter(mainAdapter);
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

        }else if(id==R.id.btn_enter){ // 채팅방 입장하기

        }
    } // end Onclick

    public class RetrofitConnection{
        String URL = getString(R.string.server_url);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JsonPlaceHolderApi server = retrofit.create(JsonPlaceHolderApi.class);
    }

}

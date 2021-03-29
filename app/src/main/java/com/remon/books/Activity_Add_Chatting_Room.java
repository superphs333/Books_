package com.remon.books;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.remon.books.Function.Function_SharedPreference;

import java.util.HashMap;
import java.util.Map;

public class Activity_Add_Chatting_Room extends AppCompatActivity {

    // 뷰변수
    EditText edit_title, edit_explain, edit_count;

    // 함수
    Function_SharedPreference fshared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__add__chatting__room);

        // 뷰연결
        edit_title = findViewById(R.id.edit_title);
        edit_explain = findViewById(R.id.edit_explain);
        edit_count = findViewById(R.id.edit_count);

        // 함수연결
        fshared = new Function_SharedPreference(getApplicationContext());




    }

    public void onClick(View view) {
        int id = view.getId();

        if(id==R.id.btn_add){ // 데이터 저장

            // 저장할 데이터
            final String title = edit_title.getText().toString();
            final String room_explain = edit_explain.getText().toString();
            final String total_count = edit_count.getText().toString();
            final String leader = fshared.get_login_value();

            // 웹페이지 실행하기
           String url = getString(R.string.server_url)+"Save_Chatting_Room.php";

            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    url,
                    new com.android.volley.Response.Listener<String>() { // 정상 응답
                        @Override
                        public void onResponse(String response) {
                            Log.d("실행","response=>"+response);

                            String[] string_array= response.split("§");
                            Log.d("실행", "결과="+string_array[0]);
                            Log.d("실행", "idx="+string_array[1]);

                            if(string_array[0].equals("success")){
                                Toast.makeText(getApplicationContext()
                                        , "채팅방이 생성되었습니다!",Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(getApplicationContext(),Activity_Chatting_Room.class);
                                intent.putExtra("idx",string_array[1]);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(), "문제가 발생하였습니다. 다시 시도해주세요",Toast.LENGTH_LONG).show();
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
                    params.put("title", title);
                    params.put("room_explain", room_explain);
                    params.put("total_count", total_count);
                    params.put("leader", leader);

                    return params;
                }
            };

            // 요청 객체를 만들었으니 이제 requestQueue 객체에 추가하면 됨.
            // Volley는 이전 결과를 캐싱하므로, 같은 결과가 있으면 그대로 보여줌
            // 하지만 아래 메소드를 false로 set하면 이전 결과가 있더라도 새로 요청해서 응답을 보여줌.
            request.setShouldCache(false);
            AppHelper.requestQueue.add(request);

        } // end id==R.id.btn_add
    } // end onClick
}

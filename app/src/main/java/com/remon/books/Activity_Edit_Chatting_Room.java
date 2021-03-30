package com.remon.books;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.remon.books.Function.Function_SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class Activity_Edit_Chatting_Room extends AppCompatActivity {

    // 뷰변수
    EditText edit_title, edit_explain, edit_count;

    // 함수
    Function_SharedPreference fshared;

    int idx; // room_idx
    int join_count; // 현재 참가중인 참여자수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__edit__chatting__room);

        // 뷰연결
        edit_title = findViewById(R.id.edit_title);
        edit_explain = findViewById(R.id.edit_explain);
        edit_count = findViewById(R.id.edit_count);

        // 함수연결
        fshared = new Function_SharedPreference(getApplicationContext());

        idx= getIntent().getIntExtra("idx",0);
        Log.d("실행", "(in Activity_Chatting_Room)idx="+idx);

        // 채팅룸 데이터 가져오기
        get_Chatting_Room_Data();

        // 값 변경시 -> join_count보다 작다면 => 다시 join_count값으로 변경후, 알림
        edit_count.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                int input;
                try{
                    input = Integer.parseInt(edit_count.getText().toString());
                    Log.d("실행", "input="+input);

                    if(input<join_count){
                        Toast.makeText(getApplicationContext(), "현재 참여중인 인원보다 더 적게 설정 할 수 없습니다",Toast.LENGTH_LONG).show();
                        edit_count.setText(join_count+"");
                    }
                }catch (NumberFormatException e){

                }


            }
        });


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

                            edit_title.setText(jsonArray.getJSONObject(0).getString("title"));
                            edit_explain.setText(jsonArray.getJSONObject(0).getString("room_explain"));
                            join_count = Integer.parseInt(jsonArray.getJSONObject(0).getString("join_count"));
                            edit_count.setText(jsonArray.getJSONObject(0).getString("total_count"));

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

    public void onClick(View view) {
        int id = view.getId();

        if(id==R.id.btn_add){ // 데이터 저장

            // 저장할 데이터
            final String title = edit_title.getText().toString();
            final String room_explain = edit_explain.getText().toString();
            final String total_count = edit_count.getText().toString();


            // 웹페이지 실행하기
           String url = getString(R.string.server_url)+"Edit_Chatting_Room.php";

            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    url,
                    new com.android.volley.Response.Listener<String>() { // 정상 응답
                        @Override
                        public void onResponse(String response) {
                            Log.d("실행","response=>"+response);

                            if(response.equals("success")){
                                Toast.makeText(getApplicationContext()
                                        , "채팅방이 수정되었습니다!",Toast.LENGTH_LONG).show();

                                // 페이지 이동
                                Intent intent = new Intent(Activity_Edit_Chatting_Room.this,Activity_Chatting_Room.class);
                                intent.putExtra("idx",idx+"");
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
                    params.put("idx", String.valueOf(idx));

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

package com.remon.books;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.remon.books.Function.Function_SharedPreference;

import java.util.HashMap;
import java.util.Map;

public class Activity_Add_Comment extends AppCompatActivity {

    // 메모 idx
    int idx_memo;

    // 함수
    Function_SharedPreference fshared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__add__comment);

        // 뷰연결

        // 함숫셋팅
        fshared = new Function_SharedPreference(getApplicationContext());

        // 값 전달받기
        idx_memo = getIntent().getIntExtra("idx_memo",0);
        Log.d("실행", "idx_memo="+idx_memo);

    }

    public void onClick(View view){
        int id = view.getId();

        // 뒤로가기
        if(id==R.id.img_back){
            finish();
        }

        // 댓글 보내기
        if(id==R.id.btn_comment){
            // 웹페이지 실행하기
            String url = getString(R.string.server_url)+"Management_Comment.php";

            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    url,
                    new com.android.volley.Response.Listener<String>() { // 정상 응답
                        @Override
                        public void onResponse(String response) {
                            Log.d("실행","response=>"+response);

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
                    params.put("login_value", fshared.get_login_value());
                    return params;
                }
            };

            // 요청 객체를 만들었으니 이제 requestQueue 객체에 추가하면 됨.
            // Volley는 이전 결과를 캐싱하므로, 같은 결과가 있으면 그대로 보여줌
            // 하지만 아래 메소드를 false로 set하면 이전 결과가 있더라도 새로 요청해서 응답을 보여줌.
            request.setShouldCache(false);
            AppHelper.requestQueue.add(request);

        }


    } // end onClick
}

package com.remon.books;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.remon.books.Function.Function_SharedPreference;

import java.util.HashMap;
import java.util.Map;

public class Activity_Review_Write extends AppCompatActivity {

    EditText edit_review;
    Button btn_review_write;

    Function_SharedPreference fshared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__review__write);

        /*
        뷰연결
         */
        edit_review = findViewById(R.id.edit_review);
        btn_review_write = findViewById(R.id.btn_review_write);

        // 리뷰셋팅
        edit_review.setText(getIntent().getStringExtra("review"));

        /*
        함수
         */
        fshared = new Function_SharedPreference(getApplicationContext());


    }

    public void Review_Send(View view) {

        // 내용이 공백인지 확인
        final String review = edit_review.getText().toString().trim();
        if(review.equals("")){
            Toast.makeText(getApplicationContext(), "리뷰를 입력해주세요",Toast.LENGTH_LONG).show();
            return;
        }

        // 웹페이지 실행하기
        String url = getString(R.string.server_url)+"Book_Review_Add.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new com.android.volley.Response.Listener<String>() { // 정상 응답
                    @Override
                    public void onResponse(String response) {
                        Log.d("실행","response=>"+response);

                        if(response.equals("success")){
                            Toast.makeText(getApplicationContext(), "리뷰가 등록되었습니다",Toast.LENGTH_LONG).show();
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(), "문제가 발생하였습니다. 다시 시도해 주세요",Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new com.android.volley.Response.ErrorListener() { // 에러 발생
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("실행","error=>"+error.getMessage());
                        Toast.makeText(getApplicationContext(), "문제가 발생하였습니다. 다시 시도해주세요",Toast.LENGTH_LONG).show();

                    }
                }

        ){ // Post 방식으로 body에 요청 파라미터를 넣어 전달하고 싶을 경우
            // 만약 헤더를 한 줄 추가하고 싶다면 getHeaders() override
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                // 보낼값 : login_value, unique_book_value, review
                params.put(getString(R.string.login_value), fshared.getPreferenceString(getString(R.string.member),getString(R.string.login_value)));
                params.put(getString(R.string.unique_book_value), getIntent().getStringExtra(getString(R.string.unique_book_value)));
                params.put("review",edit_review.getText().toString());


                return params;
            }
        };

        // 요청 객체를 만들었으니 이제 requestQueue 객체에 추가하면 됨.
        // Volley는 이전 결과를 캐싱하므로, 같은 결과가 있으면 그대로 보여줌
        // 하지만 아래 메소드를 false로 set하면 이전 결과가 있더라도 새로 요청해서 응답을 보여줌.
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);

    }
}

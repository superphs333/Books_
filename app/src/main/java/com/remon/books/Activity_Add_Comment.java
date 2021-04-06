package com.remon.books;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.remon.books.Adapter.Adapter_Comment_Memo;
import com.remon.books.Data.Data_Comment_Memo;
import com.remon.books.Function.Function_SharedPreference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_Add_Comment extends AppCompatActivity implements View.OnClickListener{

    // 메모 idx
    int idx_memo;

    // mode -> add, edit
    String mode = "add";

    // 선택한 댓글 position
    int temp_position;


    // 뷰
    EditText edit_comment;
    RecyclerView rv_comments;
    Button btn_comment;

    // 함수
    Function_SharedPreference fshared;

    // 리사이클러뷰
    ArrayList<Data_Comment_Memo> arrayList;
    Adapter_Comment_Memo mainAdapter;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__add__comment);

        // 뷰연결
        edit_comment = findViewById(R.id.edit_comment);
        rv_comments = findViewById(R.id.rv_comments);
        btn_comment = findViewById(R.id.btn_comment);



        // 함숫셋팅
        fshared = new Function_SharedPreference(getApplicationContext());

        // 값 전달받기
        idx_memo = getIntent().getIntExtra("idx_memo",0);
        Log.d("실행", "idx_memo="+idx_memo);

        // 리사이클러뷰
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_comments.setLayoutManager(linearLayoutManager);

        Get_Comment_Memos();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        // 뒤로가기
        if(id==R.id.img_back){
            finish();
        }

        // 댓글 보내기
        if(id==R.id.btn_comment){
            if(mode.equals("add")){
                Management_Comment("add",0,0);
            }else if(mode.equals("edit")){
                Management_Comment("edit",arrayList.get(temp_position).getIdx(),temp_position);
            }
        } // end id==R.id.btn_comment
    }



    // 댓글 데이터 불러오기
    private void Get_Comment_Memos(){
        RetrofitConnection retrofitConnection
                = new RetrofitConnection();
        Call<ArrayList<Data_Comment_Memo>> call
                = retrofitConnection.server.Get_Comment_Memos(idx_memo);
        call.enqueue(new Callback<ArrayList<Data_Comment_Memo>>() {
            @Override
            public void onResponse(Call<ArrayList<Data_Comment_Memo>> call, Response<ArrayList<Data_Comment_Memo>> response) {
                if(response.isSuccessful()){
                    Log.d("실행", "Get_Comment_Memos 성공");

                    arrayList = response.body();
                    mainAdapter = new Adapter_Comment_Memo(arrayList,getApplicationContext(),Activity_Add_Comment.this);
                    rv_comments.setAdapter(mainAdapter);

                    // txt_function 클릭
                    mainAdapter.setOnItemClickListener(new Adapter_Comment_Memo.OnItemClickListener() {
                        @Override
                        public void onItemClick(View v, final int position) {

                            Log.d("실행", "position="+position);

                            AlertDialog.Builder builder
                                    = new AlertDialog.Builder(Activity_Add_Comment.this);
                            final String str[] = {"수정","삭제"};
                            builder.setTitle("선택하세요")
                                    .setNegativeButton("취소",null)
                                    .setItems(str,// 리스트 목록에 사용할 배열
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Log.d("실행","선택된것="+str[which]);



                                                    if(str[which].equals("수정")){
                                                        // 해당 댓글 내용을 셋팅
                                                        edit_comment.setText(arrayList.get(position).getComment());
                                                        // 포커스
                                                        edit_comment.requestFocus();
                                                        // 키보드 올리기
                                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                                                        // mode 변경 -> edit + btn_comment 글자 변경
                                                        mode = "edit";
                                                        btn_comment.setText("수정");
                                                        temp_position = position;
                                                    }else if(str[which].equals("삭제")){
                                                        Management_Comment("delete",arrayList.get(position).getIdx(),position);
                                                    }
                                                }
                                            }
                                    );
                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }
                    });

                }else{
                    Log.d("실행", "서버에 연결되었지만 문제가 발생");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Data_Comment_Memo>> call, Throwable t) {
                Log.d("실행", "에러->"+t.getMessage());
            }
        });
    }

    public class RetrofitConnection{
        String URL = getString(R.string.server_url);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JsonPlaceHolderApi server = retrofit.create(JsonPlaceHolderApi.class);
    }

    // 댓글을 추가, 수정, 삭제하는 함수
    private void Management_Comment(final String sort,final int idx, final int position){
        // sort = add,add_comment,edit, delete

        Log.d("실행", "idx="+idx);


        // 채팅내용
        final String comment = edit_comment.getText().toString();

        // 날짜, 시간
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        final String date_time = simpleDate.format(mDate);
        Log.d("실행", "date_time="+date_time);


        // 웹페이지 실행하기
        String url = getString(R.string.server_url)+"Management_Comment.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new com.android.volley.Response.Listener<String>() { // 정상 응답
                    @Override
                    public void onResponse(String response) {
                        Log.d("실행","response=>"+response);

                        String[] string_array= response.trim().split("§");

                        if(string_array[0].equals("success")){


                            if(sort.equals("add")){

                                Data_Comment_Memo cdm
                                        = new Data_Comment_Memo(idx_memo
                                        ,Integer.parseInt(string_array[1]),fshared.get_login_value()
                                        ,fshared.get_nickname(),fshared.profile_url()
                                        ,comment,date_time);
                                arrayList.add(cdm);
                                mainAdapter.notifyDataSetChanged();
                            }else if(sort.equals("delete")){
                                arrayList.remove(position);
                                mainAdapter.notifyItemRemoved(position);
                            }else if(sort.equals("edit")){
                                arrayList.get(position).setComment(comment);
                                mainAdapter.notifyDataSetChanged();

                                // mode 변경 및 btn_comment 텍스트 셋팅
                                btn_comment.setText("전송");
                                mode = "add";
                            }

                            // edit_comment 빈값값
                            edit_comment.setText("");

                        } // end string_array[0].equals("success")

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
                // sort에 따라서 : add,add_comment,edit, delete 보내는 값 달라짐
                params.put("sort", sort);
                params.put("idx_memo", String.valueOf(idx_memo));
                if(sort.equals("add")){
                    params.put("login_value", fshared.get_login_value());
                    params.put("comment",comment);
                    params.put("date_time",date_time);
                }else if(sort.equals("add_comment")){

                }else if(sort.equals("edit")){
                    params.put("idx",idx+"");
                    params.put("comment",comment);
                }else if(sort.equals("delete")){
                    params.put("idx",idx+"");
                }
                return params;
            }
        };

        // 요청 객체를 만들었으니 이제 requestQueue 객체에 추가하면 됨.
        // Volley는 이전 결과를 캐싱하므로, 같은 결과가 있으면 그대로 보여줌
        // 하지만 아래 메소드를 false로 set하면 이전 결과가 있더라도 새로 요청해서 응답을 보여줌.
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    } // end Management_Comment()

}

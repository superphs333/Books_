package com.remon.books;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Movie;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.remon.books.Adapter.Adater_My_Book;
import com.remon.books.Adapter.Adater_Search_Book;
import com.remon.books.Data.Data_My_Book;
import com.remon.books.Data.Data_Search_Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Activity_Book_Search extends AppCompatActivity {

    ArrayList<Data_Search_Book> arrayList;

    /*
    뷰변수
     */
    Context context;
    Activity activity;
    EditText edit_search;
    RecyclerView rv_books;
    ImageView img_search;

    // 리싸이클러뷰용 변수
    private Adater_Search_Book mainAdapter;
    private LinearLayoutManager linearLayoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__book__search);


        // arrayList 초기화
        arrayList = new ArrayList<>();
        activity = Activity_Book_Search.this;
        context = getApplicationContext();
        edit_search = findViewById(R.id.edit_search);
        img_search = findViewById(R.id.img_search);
        rv_books = findViewById(R.id.rv_books);







    }


    // 책을 검색하여 보여줌
    public void search_book(View view) {

        // arraylist 초기화
        arrayList.clear();

        String url="https://dapi.kakao.com"+"/v3/search/book?query="+edit_search.getText().toString();



        StringRequest request = new StringRequest(
                Request.Method.GET, // GET 방식으로 요청
                url,
                new Response.Listener<String>() {
                    //응답이 제대로 되었을 때
                    @Override
                    public void onResponse(String response) {
                        Log.i("실행","응답 ->" + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            JSONArray TEMP = jsonObject.getJSONArray("documents");

                            for(int i=0; i<TEMP.length(); i++){
                                JSONObject temp_object = TEMP.getJSONObject(i);
                                Data_Search_Book dmy = new Data_Search_Book();

                                // 작가
                                String temp = temp_object.getString("authors").replaceAll("\\[","").replaceAll("\\]","").replaceAll("\"","");
                                dmy.setAuthors(temp);

                                // contents
//                                String contents = temp_object.getString("contents").
                                dmy.setContents(temp_object.getString("contents"));

                                // isbn -> 분기
                                String[] string_array= temp_object.getString("isbn").split(" ");
                                dmy.setIsbn(string_array[0]);

                                dmy.setPublisher(temp_object.getString("publisher"));
                                dmy.setThumbnail(temp_object.getString("thumbnail"));
                                dmy.setTitle(temp_object.getString("title"));
                                dmy.setUrl(temp_object.getString("url"));
                                arrayList.add(dmy);


                            }

                            // 리싸이클러뷰에 셋팅
                            mainAdapter = new Adater_Search_Book(arrayList,context,activity);
                            rv_books.setAdapter(mainAdapter);
                            linearLayoutManager = new LinearLayoutManager(context);
                            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            rv_books.setLayoutManager(linearLayoutManager);










                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO 에러응답을 받았을 때
                        Log.d("실행", "error=>"+error.getMessage());

                    }
                }
        ) {
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap();
                params.put("Authorization","KakaoAK e7175a312c372da8bf3b88df5fc3a144"); //카카오 개발자 홈페이지에서 본인의 앱의 Authorization key를 발급 받고 집어넣기
                return params;
            }
        };

        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }
}

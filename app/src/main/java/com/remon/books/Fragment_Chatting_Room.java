package com.remon.books;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.remon.books.Adapter.Adapter_Chatting_Room;
import com.remon.books.Adapter.Adater_My_Book;
import com.remon.books.Data.Data_Chatting_Room;
import com.remon.books.Data.Data_My_Book;
import com.remon.books.Function.Function_Set;
import com.remon.books.Function.Function_SharedPreference;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Fragment_Chatting_Room extends Fragment  {

    /*
    뷰변수
     */
    View v;
    Context context;
    Activity activity;
    Button btn_add_room;
    Spinner spin_sort;
    RecyclerView rv_chatting_rooms;


    // 데이터
    ArrayList<Data_Chatting_Room> arrayList;

    // 함수
    Function_SharedPreference fshared;

    // 리싸이클러뷰용 변수
    private Adapter_Chatting_Room mainAdapter;
    private LinearLayoutManager linearLayoutManager;

    // 필요변수
    private String login_value;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /*
        뷰연결
         */
        LayoutInflater inflater1 = inflater;
        v = inflater.inflate(R.layout.fragment__chattingroom,container,false);
        context = v.getContext();
        activity = (Main)getActivity();
        btn_add_room = v.findViewById(R.id.btn_add_room);
        spin_sort = v.findViewById(R.id.spin_sort);
        rv_chatting_rooms = v.findViewById(R.id.rv_chatting_rooms);

        // 함수연결
        fshared = new Function_SharedPreference(context);

        login_value = fshared.get_login_value();

        /*
        spinner셋팅
         */
        final String[] data = getResources().getStringArray(R.array.select_chatting_room_view);
        ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(context,android.R.layout.simple_dropdown_item_1line,data);
        spin_sort.setAdapter(adapter);

        // spinner 변경시 -> 보여지는 채팅방 다르게
        spin_sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){ // 전체
                    Get_Chatting_Room_Data_Whole();
                }else if(position==1){
                    Log.d("실행", "1");
                    Get_Chatting_Room_Data_Whole(1);
                }else if(position==2){
                    Log.d("실행", "2");
                    Get_Chatting_Room_Data_Whole(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        btn_add_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,Activity_Add_Chatting_Room.class);
                startActivity(intent);
            }
        });

        // 리싸이클러뷰
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_chatting_rooms.setLayoutManager(linearLayoutManager);




        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        // 데이터 불러오기 -> spinner에 따라
        int select = spin_sort.getSelectedItemPosition();
        if(select==0){
            Get_Chatting_Room_Data_Whole();
        }else if(select==1){ // 참여중
            Get_Chatting_Room_Data_Whole(1);
        }else{ // 대기중
            Get_Chatting_Room_Data_Whole(0);
        }

    }

    // 채팅방 데이터 불러오기
    private void Get_Chatting_Room_Data_Whole(){
        RetrofitConnection retrofitConnection
                = new RetrofitConnection();
        Call<ArrayList<Data_Chatting_Room>> call
                = retrofitConnection.server.Get_Chatting_Room_Data_Whole();
        call.enqueue(new Callback<ArrayList<Data_Chatting_Room>>() {
            @Override
            public void onResponse(Call<ArrayList<Data_Chatting_Room>> call, Response<ArrayList<Data_Chatting_Room>> response) {
                if(response.isSuccessful()){
                    Log.d("실행", "Get_Chatting_Room_Data_Whole 성공");

                    arrayList = response.body();
                    mainAdapter = new Adapter_Chatting_Room(arrayList,context,activity);
                    rv_chatting_rooms.setAdapter(mainAdapter);

                }else{
                    Log.d("실행", "서버에 연결돠었으나 문제가 발생");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Data_Chatting_Room>> call, Throwable t) {
                Log.d("실행", "오류=>"+t.getMessage());
            }
        });
    } // end Get_Chatting_Room_Data_Whole

    private void Get_Chatting_Room_Data_Whole(int state){
        Log.d("실행", "Get_Chatting_Room_Data_Whole-참여중OR대기중");
        Log.d("실행", "login_value="+login_value+", state="+state);


        RetrofitConnection retrofitConnection
                = new RetrofitConnection();
        Call<ArrayList<Data_Chatting_Room>> call
                = retrofitConnection.server.Get_Chatting_Room_Data_By_Sort(login_value, state);
        call.enqueue(new Callback<ArrayList<Data_Chatting_Room>>() {
            @Override
            public void onResponse(Call<ArrayList<Data_Chatting_Room>> call, Response<ArrayList<Data_Chatting_Room>> response) {
                arrayList = response.body();
                mainAdapter = new Adapter_Chatting_Room(arrayList,context,activity);
                rv_chatting_rooms.setAdapter(mainAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<Data_Chatting_Room>> call, Throwable t) {

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
}

package com.remon.books.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.remon.books.Data.Data_Chatting;

import java.util.ArrayList;

public class Adapter_Chatting extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    // 데이터 셋팅
    ArrayList<Data_Chatting> arrayList;

    Context context;

    // 생성자
    public Adapter_Chatting(ArrayList<Data_Chatting> arrayList, Context context){
        this.arrayList = arrayList;
        this.context = context;
    }

    /*
    뷰홀더 분기
     */
    // notice : 날짜/시간
    public class ViewHolder_notice extends RecyclerView.ViewHolder{

        public ViewHolder_notice(@NonNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}

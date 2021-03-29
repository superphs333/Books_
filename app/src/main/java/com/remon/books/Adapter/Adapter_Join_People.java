package com.remon.books.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.remon.books.Activity_Underline_Picture;
import com.remon.books.Data.Data_Img_Memo;
import com.remon.books.Data.Data_Join_People;
import com.remon.books.ItemTouchHelperListener;
import com.remon.books.R;

import java.util.ArrayList;

public class Adapter_Join_People
    extends RecyclerView.Adapter<Adapter_Join_People.CustomViewHolder>

{
    // Context
    Context context;
    Activity activity;

    // 데이터 셋팅
    private ArrayList<Data_Join_People> arrayList;


    // 생성자
    public Adapter_Join_People(ArrayList<Data_Join_People> arrayList, Context context, Activity activity){
        this.arrayList = arrayList;
        this.context = context;
        this.activity = activity;
    }



    // 뷰홀더
    public class CustomViewHolder extends RecyclerView.ViewHolder{

        // 위젯정의
        protected TextView txt_nickname, txt_follow;
        protected ImageView img_profile;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.img_profile = itemView.findViewById(R.id.img_profile);
            this.txt_nickname = itemView.findViewById(R.id.txt_nickname);
            this.txt_follow = itemView.findViewById(R.id.txt_follow);
        }
    }


    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view
                = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_join_people,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, final int position) {

        // 값셋팅
        Glide.with(holder.itemView.getContext()).load(arrayList.get(holder.getAdapterPosition()).getProfile_url()).into(holder.img_profile);
        holder.txt_nickname.setText(arrayList.get(holder.getAdapterPosition()).getNickname());
        if(arrayList.get(holder.getAdapterPosition()).isFollow()){ // follow = true
            //=> txt_follow가 Gone
            holder.txt_follow.setVisibility(View.GONE);
        }else{ // follow = false => txt_follow가 보임
            holder.txt_follow.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return (null !=  arrayList ?  arrayList.size() : 0);
    }




}

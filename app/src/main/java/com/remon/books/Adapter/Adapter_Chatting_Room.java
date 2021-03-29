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
import com.remon.books.Activity_Chatting_Room;
import com.remon.books.Activity_Underline_Picture;
import com.remon.books.Data.Data_Chatting_Room;
import com.remon.books.Data.Data_Img_Memo;
import com.remon.books.ItemTouchHelperListener;
import com.remon.books.R;

import java.util.ArrayList;

public class Adapter_Chatting_Room
    extends RecyclerView.Adapter<Adapter_Chatting_Room.CustomViewHolder>


{
    // Context
    Context context;
    Activity activity;

    // 데이터 셋팅
    private ArrayList<Data_Chatting_Room> arrayList;


    // 생성자
    public Adapter_Chatting_Room(ArrayList<Data_Chatting_Room> arrayList, Context context){
        this.arrayList = arrayList;
        this.context = context;
    }



    // 뷰홀더
    public class CustomViewHolder extends RecyclerView.ViewHolder{

        // 위젯정의
        protected TextView txt_title, txt_explain,txt_count, txt_total;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.txt_title = itemView.findViewById(R.id.txt_title);
            this.txt_explain = itemView.findViewById(R.id.txt_explain);
            this.txt_count = itemView.findViewById(R.id.txt_count);
            this.txt_total = itemView.findViewById(R.id.txt_total);

        }
    }


    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view
                = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_chatting_room,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, final int position) {

        // 데이터셋팅
        holder.txt_title.setText(arrayList.get(holder.getAdapterPosition()).getTitle());
        holder.txt_explain.setText(arrayList.get(holder.getAdapterPosition()).getRoom_explain());
        holder.txt_count.setText(arrayList.get(holder.getAdapterPosition()).getJoin_count()+"");
        holder.txt_total.setText(arrayList.get(holder.getAdapterPosition()).getTotal_count()+"");

        // 클릭 -> 해당 채팅방 들어가기(자세히보기 : Activity_Chatting_Room)
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Activity_Chatting_Room.class);
                intent.putExtra("idx",arrayList.get(holder.getAdapterPosition()).getIdx()+"");
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null !=  arrayList ?  arrayList.size() : 0);
    }


}

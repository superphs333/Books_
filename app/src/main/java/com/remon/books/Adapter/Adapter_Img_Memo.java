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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.remon.books.Activity_Underline_Picture;
import com.remon.books.Data.Data_Img_Memo;
import com.remon.books.ItemTouchHelperListener;
import com.remon.books.R;

import java.util.ArrayList;

public class Adapter_Img_Memo
    extends RecyclerView.Adapter<Adapter_Img_Memo.CustomViewHolder>
        implements ItemTouchHelperListener

{
    // Context
    Context context;

    // 데이터 셋팅
    private ArrayList<Data_Img_Memo> arrayList;


    // 생성자
    public Adapter_Img_Memo(ArrayList<Data_Img_Memo> arrayList, Context context){
        this.arrayList = arrayList;
        this.context = context;
    }



    // 뷰홀더
    public class CustomViewHolder extends RecyclerView.ViewHolder{

        // 위젯정의
        protected ImageView img_memo;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.img_memo = (ImageView)itemView.findViewById(R.id.img_memo);
        }
    }


    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view
                = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_img_memo,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, final int position) {
        /*
        이미지 넣기
        - 이미지를 앨범에서 가져오는 경우
        - 이미지를 서버에서 가져오는 경우 => 앞에 http로 시작작
         */
        if(arrayList.get(position).getImg().substring(0,4).equals("http")){
            Glide.with(holder.itemView.getContext())
                    .load(arrayList.get(position).getImg())
                    .into(holder.img_memo);
        }else{
            holder.img_memo.setImageURI(Uri.parse(arrayList.get(position).getImg()));
        }

        // 이미지 클릭 -> 밑줄을 그을 수 있는 액티비티로 이동
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Activity_Underline_Picture.class);
                intent.putExtra("img_url",arrayList.get(position).getImg());
                ((Activity) context).startActivityForResult(intent,999);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null !=  arrayList ?  arrayList.size() : 0);
    }


    @Override
    public boolean onItemMove(int from_position, int to_position) {
        //이동할 객체 저장
        Data_Img_Memo person = arrayList.get(from_position);
        //이동할 객체 삭제
        arrayList.remove(from_position);
        // 이동하고 싶은 position에 추가
        arrayList.add(to_position,person);
        //Adapter에 데이터 이동알림
        notifyItemMoved(from_position,to_position);
        return true;
    }

    @Override
    public void onItemSwipe(int position) {
        arrayList.remove(position);
        notifyItemRemoved(position);
    }

}

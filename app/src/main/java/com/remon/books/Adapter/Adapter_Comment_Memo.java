package com.remon.books.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.remon.books.Activity_Underline_Picture;
import com.remon.books.Data.Data_Comment_Memo;
import com.remon.books.Data.Data_Img_Memo;
import com.remon.books.Function.Function_Set;
import com.remon.books.Function.Function_SharedPreference;
import com.remon.books.ItemTouchHelperListener;
import com.remon.books.R;

import java.util.ArrayList;

public class Adapter_Comment_Memo
    extends RecyclerView.Adapter<Adapter_Comment_Memo.CustomViewHolder>
{
    // Context
    Context context;
    Activity activity;

    // 데이터 셋팅
    private ArrayList<Data_Comment_Memo> arrayList;

    // 함수
    Function_SharedPreference fshared;
    Function_Set fs;


    // 생성자
    public Adapter_Comment_Memo(ArrayList<Data_Comment_Memo> arrayList, Context context, Activity activity){
        this.arrayList = arrayList;
        this.context = context;
        this.activity = activity;
        fshared = new Function_SharedPreference(context);
        fs = new Function_Set(context);
    }

    /*
    커스텀 리스너
     */
    // 1. txt_function 용
    // 어댑터 내에서 커스텀 리스너 인터페이스 정의
    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }
    // 리스너 객체를 전달하는 메서드와 전달된 객체를 저장할 변수 추가
    private OnItemClickListener mListener = null ;
        // 리스너 객체 참조를 저장하는 변수
    // OnItemClickListener  리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    // 2. txt_reply용
    public interface OnItemClickListener2{
        void onItemClick(View v, int position);
    }
    private OnItemClickListener2 mListener2 = null ;
    public void setOnItemClickListener2(OnItemClickListener2 listener){
        this.mListener2 = listener;
    }







    // 뷰홀더
    public class CustomViewHolder extends RecyclerView.ViewHolder{

        // 위젯정의
        protected ImageView img_profile;
        TextView txt_nickname, txt_date_time, txt_function, txt_comment, txt_reply;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.img_profile = itemView.findViewById(R.id.img_profile);
            this.txt_nickname = itemView.findViewById(R.id.txt_nickname);
            this.txt_date_time = itemView.findViewById(R.id.txt_date_time);
            this.txt_function = itemView.findViewById(R.id.txt_function);
            this.txt_comment = itemView.findViewById(R.id.txt_comment);
            this.txt_reply = itemView.findViewById(R.id.txt_reply);

            this.txt_function.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                       mListener.onItemClick(v,pos);
                    }
                }
            });

            this.txt_reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        mListener2.onItemClick(v,pos);
                    }
                }
            });
        }
    }


    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view
                = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_comment,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, final int position) {

        // 프로필 이미지
        Glide.with(holder.itemView.getContext()).load(arrayList.get(holder.getAdapterPosition()).getProfile_url()).into(holder.img_profile);
        // 닉네임
        holder.txt_nickname.setText(arrayList.get(holder.getAdapterPosition()).getNickname());
        // 날짜
        holder.txt_date_time.setText(arrayList.get(holder.getAdapterPosition()).getDate_time());
        // 댓글 내용
        holder.txt_comment.setText(arrayList.get(holder.getAdapterPosition()).getComment());
        // 본인인 경우에만 -> txt_function 보임
        if(arrayList.get(holder.getAdapterPosition()).getLogin_value().equals(fshared.get_login_value())){
            // 본인인 경우
            holder.txt_function.setVisibility(View.VISIBLE);
        }else{
            // 본인이 아닌 경우
            holder.txt_function.setVisibility(View.GONE);
        }

        // depth = 1인경우 -> 왼쪽으로부터 간격 띄우기
        if(arrayList.get(holder.getAdapterPosition()).getDepth()==1){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(80,0,0,0);  // 왼쪽, 위, 오른쪽, 아래 순서입니다.
            holder.itemView.setLayoutParams(params);
        }else{
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,0,0,0);  // 왼쪽, 위, 오른쪽, 아래 순서입니다.
            holder.itemView.setLayoutParams(params);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("실행", "depth="+arrayList.get(holder.getAdapterPosition()).getDepth()+", group_idx="+arrayList.get(holder.getAdapterPosition()).getGroup_idx());

            }
        });


    }

    @Override
    public int getItemCount() {
        return (null !=  arrayList ?  arrayList.size() : 0);
    }



}

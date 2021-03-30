package com.remon.books.Adapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.remon.books.Activity_Underline_Picture;
import com.remon.books.AppHelper;
import com.remon.books.Data.Data_Img_Memo;
import com.remon.books.Data.Data_Join_People;
import com.remon.books.Function.Function_Set;
import com.remon.books.Function.Function_SharedPreference;
import com.remon.books.ItemTouchHelperListener;
import com.remon.books.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Adapter_Join_People
    extends RecyclerView.Adapter<Adapter_Join_People.CustomViewHolder>

{
    // Context
    Context context;
    Activity activity;

    // 데이터 셋팅
    private ArrayList<Data_Join_People> arrayList;

    // 함수
    Function_Set fs;
    Function_SharedPreference fshared;

    // 리더
    String leader;

    // 생성자
    public Adapter_Join_People(ArrayList<Data_Join_People> arrayList, Context context, Activity activity, String leader){
        this.arrayList = arrayList;
        this.context = context;
        this.activity = activity;
        fs = new Function_Set(context);
        fshared = new Function_SharedPreference(context);
        this.leader = leader;
    }



    // 뷰홀더
    public class CustomViewHolder extends RecyclerView.ViewHolder{

        // 위젯정의
        protected TextView txt_nickname, txt_follow,txt_role;
        protected ImageView img_profile;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.img_profile = itemView.findViewById(R.id.img_profile);
            this.txt_nickname = itemView.findViewById(R.id.txt_nickname);
            this.txt_follow = itemView.findViewById(R.id.txt_follow);
            this.txt_role = itemView.findViewById(R.id.txt_role);
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
        // leader라면 -> txt_role Visibility
        if(arrayList.get(holder.getAdapterPosition()).getLogin_value().equals(leader)){
            holder.txt_role.setVisibility(View.VISIBLE);
        }

        // txt_follow(팔로우)클릭 => 해당 회원 팔로우
        holder.txt_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("실행", fshared.get_login_value()+", "+arrayList.get(holder.getAdapterPosition()).getLogin_value());


                fs.Management_Follow(
                        fshared.get_login_value(),
                        arrayList.get(holder.getAdapterPosition()).getLogin_value()
                        , "following"
                        , new Function_Set.VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {

                                Log.d("실행", "result="+result);

                                if(result.equals("success")){
                                    holder.txt_follow.setVisibility(View.GONE);
                                    arrayList.get(holder.getAdapterPosition()).setFollow(true);
                                    notifyDataSetChanged();

                                    Toast.makeText(context, "팔로우 하였습니다!",Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(context, "죄송합니다. 문제가 발생하였습니다.",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        }); // end onClick

        // 프로필 사진 클릭 -> 해당 회원의 피드 가기
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

    }

    @Override
    public int getItemCount() {
        return (null !=  arrayList ?  arrayList.size() : 0);
    }




}

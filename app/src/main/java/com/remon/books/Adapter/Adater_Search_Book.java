package com.remon.books.Adapter;
import android.widget.Toast;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.remon.books.Activity_Book_URL;
import com.remon.books.AppHelper;
import com.remon.books.Data.Data_My_Book;
import com.remon.books.Data.Data_Search_Book;
import com.remon.books.Function.Function_Set;
import com.remon.books.Function.Function_SharedPreference;
import com.remon.books.PopUp_in_Search_Book;
import com.remon.books.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Adater_Search_Book
    extends RecyclerView.Adapter<Adater_Search_Book.CustomViewHolder>

{
    // Context
    Context context;
    Activity activity;

    // 데이터 셋팅
    private ArrayList<Data_Search_Book> arrayList;

    // 함수
    Function_Set fs;


    // 생성자
    public Adater_Search_Book(ArrayList<Data_Search_Book> arrayList, Context context,Activity activity){
        this.arrayList = arrayList;
        this.context = context;
        this.activity = activity;
        fs = new Function_Set(context);
        fs.context = context;
    }

    // 뷰홀더
    public class CustomViewHolder extends RecyclerView.ViewHolder{

        // 위젯정의
        protected ImageView img_thumbnail;
        protected TextView txt_title, txt_authors, txt_contents,txt_rating,txt_status;


        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.img_thumbnail = (ImageView)itemView.findViewById(R.id.img_thumbnail);
            this.txt_title = (TextView) itemView.findViewById(R.id.txt_title);
            this.txt_authors = (TextView)itemView.findViewById(R.id.txt_authors);
            this.txt_contents = (TextView)itemView.findViewById(R.id.txt_contents);
//            this.txt_rating = (TextView)itemView.findViewById(R.id.txt_rating);
            this.txt_status = (TextView)itemView.findViewById(R.id.txt_status);

        }
    }


    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view
                = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_search_book_list,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, int position) {
        // 이미지 셋팅
        Glide.with(holder.itemView.getContext()).load(arrayList.get(holder.getAdapterPosition()).getThumbnail()).into(holder.img_thumbnail);
        // 타이틀
        holder.txt_title.setText(arrayList.get(holder.getAdapterPosition()).getTitle());
        // 작가
        holder.txt_authors.setText(arrayList.get(holder.getAdapterPosition()).getAuthors());
        // 내용
        holder.txt_contents.setText(arrayList.get(holder.getAdapterPosition()).getContents());


        // 클릭 -> alertdialog
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder
                        = new AlertDialog.Builder(activity);
                final String str[] = {"자세히 보기","책 저장하기"};
                builder.setTitle("선택하세요")
                        .setNegativeButton("취소",null)
                        .setItems(str,// 리스트 목록에 사용할 배열
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d("실행","선택된것="+str[which]);

                                        if(str[which].equals("자세히 보기")){
                                            // 자세히 보기 클릭

                                            // 해당 url페이지를 웹뷰로 보여주는 액티비티로 이동
                                            Intent intent = new Intent(activity, Activity_Book_URL.class);
                                            intent.putExtra("url",arrayList.get(holder.getAdapterPosition()).getUrl());
                                            activity.startActivity(intent);
                                        }else{

                                            // 책 중복 확인 -> 중복되지 않았다면, 책 저장하기
                                            fs.Check_in_mybook(arrayList.get(holder.getAdapterPosition()).getIsbn(),new Function_Set.VolleyCallback() {
                                                @Override
                                                public void onSuccess(String result) {

                                                    // result = duplicate_no인 경우에만 책 추가
                                                    if(result.trim().equals("duplicate_no")){
                                                        Intent intent = new Intent(activity, PopUp_in_Search_Book.class);
                                                        intent.putExtra("title",arrayList.get(holder.getAdapterPosition()).getTitle());
                                                        intent.putExtra("book", (Serializable) arrayList.get(holder.getAdapterPosition()));
                                                        activity.startActivity(intent);
                                                    }else if(result.equals("duplicate")){ // 이미 존재하는 경우
                                                        Toast.makeText(context, "이미 등록되어 있는 도서입니다.",Toast.LENGTH_LONG).show();
                                                    }else{ // 에러
                                                        Toast.makeText(context, "문제가 생겼습니다. 다시 시도해주세요",Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });


                                        }
                                    }
                                }
                        );
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });



    }

    @Override
    public int getItemCount() {
        return (null !=  arrayList ?  arrayList.size() : 0);
    }



}

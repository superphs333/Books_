package com.remon.books.Adapter;

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

import com.bumptech.glide.Glide;
import com.remon.books.Activity_Detail_My_Book;
import com.remon.books.Data.Data_My_Book;
import com.remon.books.R;

import java.util.ArrayList;

public class Adater_My_Book
    extends RecyclerView.Adapter<Adater_My_Book.CustomViewHolder>

{
    // Context
    Context context;

    // 데이터 셋팅
    private ArrayList<Data_My_Book> arrayList;

    // 생성자
    public Adater_My_Book(ArrayList<Data_My_Book> arrayList, Context context){
        this.arrayList = arrayList;
        this.context = context;
    }

    // 뷰홀더
    public class CustomViewHolder extends RecyclerView.ViewHolder{

        // 위젯정의
        protected ImageView img_thumbnail;
        protected TextView txt_title, txt_authors, txt_contents,txt_rating,txt_status,txt_function;


        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.img_thumbnail = (ImageView)itemView.findViewById(R.id.img_thumbnail);
            this.txt_title = (TextView) itemView.findViewById(R.id.txt_title);
            this.txt_authors = (TextView)itemView.findViewById(R.id.txt_authors);
            this.txt_contents = (TextView)itemView.findViewById(R.id.txt_contents);
            this.txt_rating = (TextView)itemView.findViewById(R.id.txt_rating);
            this.txt_status = (TextView)itemView.findViewById(R.id.txt_status);
            this.txt_function = (TextView)itemView.findViewById(R.id.txt_function);

        }
    }


    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view
                = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_book_list,parent,false);
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
        if(arrayList.get(holder.getAdapterPosition()).getContents().equals("")){
            holder.txt_contents.setVisibility(View.GONE);
        }
        // 별점
        holder.txt_rating.setText(arrayList.get(holder.getAdapterPosition()).getRating()+"");
        // 읽음상태
        if(arrayList.get(holder.getAdapterPosition()).getStatus()==0){ // 읽고싶은
            holder.txt_status.setText(context.getString(R.string.read_bucket));
        }else if(arrayList.get(holder.getAdapterPosition()).getStatus()==1){ // 읽는중
            holder.txt_status.setText(context.getString(R.string.read_reading));
        }else{ // 읽음
            holder.txt_status.setText(context.getString(R.string.read_end));
        }

        // 클릭시 -> Activity_Detail_My_Book 이동
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 페이지 이동
                Intent intent = new Intent(context, Activity_Detail_My_Book.class);
                intent.putExtra("unique_book_value",arrayList.get(holder.getAdapterPosition()).getUnique_book_value());
                context.startActivity(intent);
            }
        });

        // txt_function(점세개) 클릭 -> 수정 or 삭제
        holder.txt_function.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder
                        = new AlertDialog.Builder(context);

                // 만약 from_=search라면 => 수정이 보이지 않아야 함
                if(arrayList.get(holder.getAdapterPosition()).getFrom_().equals(""))
                final String str[] = {"A","B"};
                builder.setTitle("선택하세요")
                        .setNegativeButton("취소",null)
                        .setItems(str,// 리스트 목록에 사용할 배열
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d("실행","선택된것="+str[which]);

                                    }
                                }
                        );
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        }); // end holder.txt_function.setOnClickListener

    }

    @Override
    public int getItemCount() {
        return (null !=  arrayList ?  arrayList.size() : 0);
    }


}

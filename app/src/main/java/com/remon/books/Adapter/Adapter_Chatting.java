package com.remon.books.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.remon.books.Data.Data_Chatting;
import com.remon.books.R;

import java.util.ArrayList;

public class Adapter_Chatting extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    // 데이터 셋팅
    ArrayList<Data_Chatting> arrayList;

    Context context;

    // 내 이메일
    String my_email;

    // 생성자
    public Adapter_Chatting(ArrayList<Data_Chatting> arrayList, Context context,String my_email){
        this.arrayList = arrayList;
        this.context = context;
        this.my_email = my_email;
    }

    /*
    뷰홀더 분기
     */
    // notice : 날짜/시간
    public class ViewHolder_notice extends RecyclerView.ViewHolder{

        TextView txt;

        public ViewHolder_notice(@NonNull View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.txt);
        }
    }
    // 다른사람 - 문자
    public class ViewHolder_others_chat extends RecyclerView.ViewHolder{
        ImageView chat_thumbnail;
        TextView txt_nickname,txt_chat, txt_time;

        public ViewHolder_others_chat(@NonNull View itemView) {
            super(itemView);
            chat_thumbnail = itemView.findViewById(R.id.chat_thumbnail);
            txt_nickname = itemView.findViewById(R.id.txt_nickname);
            txt_chat = itemView.findViewById(R.id.txt_chat);
            txt_time= itemView.findViewById(R.id.txt_time);
        }
    }

    // 나 - 문자
    public class ViewHolder_my_chat extends RecyclerView.ViewHolder{
        TextView txt_chat, txt_time;

        public ViewHolder_my_chat(@NonNull View itemView) {
            super(itemView);
            txt_chat = itemView.findViewById(R.id.txt_chat);
            txt_time= itemView.findViewById(R.id.txt_time);
        }
    }

    // 다른사람 - 파일
    public class ViewHolder_others_file extends RecyclerView.ViewHolder{
        ImageView chat_thumbnail,img_chat;
        TextView txt_nickname,txt_time,txt_read_chk;

        public ViewHolder_others_file(@NonNull View itemView) {
            super(itemView);
            chat_thumbnail = itemView.findViewById(R.id.chat_thumbnail);
            txt_nickname = itemView.findViewById(R.id.txt_nickname);
            img_chat = itemView.findViewById(R.id.img_chat);
            txt_time= itemView.findViewById(R.id.txt_time);
            txt_read_chk= itemView.findViewById(R.id.txt_read_chk);
        }
    }

    // 나 - 파일
    public class ViewHolder_my_file extends RecyclerView.ViewHolder{
        ImageView img_chat;
        TextView txt_time,txt_read_chk;

        public ViewHolder_my_file(@NonNull View itemView)
        {
            super(itemView);
            img_chat = itemView.findViewById(R.id.img_chat);
            txt_time= itemView.findViewById(R.id.txt_time);
            txt_read_chk= itemView.findViewById(R.id.txt_read_chk);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        /*
        getItemViewType에서 나눴던것 처럼 뷰타입 번호에 리턴값에 따라 각각에
        알맞는 뷰에 inflate해준다
         */
        View holderView;

        if(viewType==0){ // 알림
            holderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting_notice,parent,false);
            return new ViewHolder_notice(holderView);
        }else if(viewType==1){ // 다른사람 - 문자
            holderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting_others_chat,parent,false);
            return new ViewHolder_others_chat(holderView);
        }else if(viewType==2){ // 다른사람 - 파일
            holderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting_others_file,parent,false);
            return new ViewHolder_others_file(holderView);
        }else if(viewType==3){ // 나 - 문자
            holderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chtting_my_chat,parent,false);
            return new ViewHolder_my_chat(holderView);
        }else if(viewType==4){ // 나 - 파일
            holderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting_my_file,parent,false);
            return new ViewHolder_my_file(holderView);
        }
        else{
            return null;
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();

        if(viewType==0){ // 알림
            ViewHolder_notice viewholder = (ViewHolder_notice)holder;
            viewholder.txt.setText(arrayList.get(holder.getAdapterPosition()).getContent());
        }else if(viewType==1){ // 다른사람 - 문자
            ViewHolder_others_chat viewholder  = (ViewHolder_others_chat)holder;

            // 썸네일
            Glide.with(holder.itemView.getContext()).load(arrayList.get(holder.getAdapterPosition()).getProfile_url()).into(viewholder.chat_thumbnail);
            // 닉네임
            viewholder.txt_nickname.setText(arrayList.get(holder.getAdapterPosition()).getNickname());
            // 채팅내용
            viewholder.txt_chat.setText(arrayList.get(holder.getAdapterPosition()).getContent());
            // 시간
            viewholder.txt_time.setText(arrayList.get(holder.getAdapterPosition()).getDate());
        }else if(viewType==2){ // 다른사람 - 파일
            ViewHolder_others_file viewholder = (ViewHolder_others_file)holder;

            // 채팅내용
            Glide.with(holder.itemView.getContext()).load(arrayList.get(holder.getAdapterPosition()).getContent()).into(viewholder.img_chat);

            String order_tag = arrayList.get(holder.getAdapterPosition()).getOrder_tag();
            String sort = arrayList.get(holder.getAdapterPosition()).getSort();

            if(sort.equals("file")){
                viewholder.txt_time.setVisibility(View.VISIBLE); // 시간
                viewholder.chat_thumbnail.setVisibility(View.VISIBLE); // 썸네일
                viewholder.txt_nickname.setVisibility(View.VISIBLE); // 닉네임

                // 값셋팅
                viewholder.txt_time.setText(arrayList.get(holder.getAdapterPosition()).getDate()); // 시간
                Glide.with(holder.itemView.getContext()).load(arrayList.get(holder.getAdapterPosition()).getProfile_url()).into(viewholder.chat_thumbnail); // 썸네일
                viewholder.txt_nickname.setText(arrayList.get(holder.getAdapterPosition()).getNickname()); // 닉네임
            }else{

            }

        }else if(viewType==3){ // 나 - 문자
            ViewHolder_my_chat viewholder = (ViewHolder_my_chat) holder;

            // 채팅내용
            viewholder.txt_chat.setText(arrayList.get(holder.getAdapterPosition()).getContent());
            // 시간
            viewholder.txt_time.setText(arrayList.get(holder.getAdapterPosition()).getDate());
        }else if(viewType==4){ // 나 - 파일
            ViewHolder_my_file viewholder = (ViewHolder_my_file)holder;

            // 채팅내용
            Glide.with(holder.itemView.getContext()).load(arrayList.get(holder.getAdapterPosition()).getContent()).into(viewholder.img_chat);

            String order_tag = arrayList.get(holder.getAdapterPosition()).getOrder_tag();
            String sort = arrayList.get(holder.getAdapterPosition()).getSort();

            if(sort.equals("file")){
                viewholder.txt_time.setVisibility(View.VISIBLE);
                viewholder.txt_time.setText(arrayList.get(holder.getAdapterPosition()).getDate());
            }else{

            }
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    /*
    여러 뷰타입을 사용하기 위해서
     */
    @Override
    public int getItemViewType(int position) {

        int returnVal = 0;

        String sort = arrayList.get(position).getSort();

        if(arrayList.get(position).getSort().equals("notice")){
            returnVal = 0;
        }else if(arrayList.get(position).getSort().equals("message")){ // 메세지
            if(arrayList.get(position).getLogin_value().equals(my_email)){ // 나
                returnVal = 3;
            }else{ // 다른사람
                returnVal = 1;
            }
        }else if(sort.equals("file") || sort.equals("files")){ // 파일
            // 다른사람 or 나 분기
            if(arrayList.get(position).getLogin_value().equals(my_email)){ // 나
                returnVal = 4;
            }else{ // 다른사람
                returnVal = 2;
            }
        }

        return returnVal;
    }
}

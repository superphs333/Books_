package com.remon.books.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.remon.books.Data.Data_Follow_People;
import com.remon.books.Data.Data_Img_Memo;
import com.remon.books.Function.Function_Set;
import com.remon.books.Function.Function_SharedPreference;
import com.remon.books.ItemTouchHelperListener;
import com.remon.books.R;

import java.util.ArrayList;

public class Adapter_Follow_People
    extends RecyclerView.Adapter<Adapter_Follow_People.CustomViewHolder>


{
    // Context
    Context context;
    Activity activity;

    // 함수
    Function_Set fs;
    Function_SharedPreference fshared;

    //  이 모드에 따라(follower, following) 점세개 누를 때 보이는 메뉴가 다르다
    public String mode_follow = "follower";

    // 데이터 셋팅
    private ArrayList<Data_Follow_People> arrayList;


    // 생성자
    public Adapter_Follow_People(ArrayList<Data_Follow_People> arrayList, Context context, Activity activity){
        this.arrayList = arrayList;
        this.context = context;
        this.activity = activity;
        fs = new Function_Set(context);
        fshared = new Function_SharedPreference(context);
    }



    // 뷰홀더
    public class CustomViewHolder extends RecyclerView.ViewHolder{

        // 위젯정의
        protected ImageView img_profile;
        protected TextView txt_nickname,txt_function;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.img_profile = itemView.findViewById(R.id.img_profile);
            this.txt_nickname = itemView.findViewById(R.id.txt_nickname);
            this.txt_function = itemView.findViewById(R.id.txt_function);
        }
    }


    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view
                = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_follow_people,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, final int position) {



        // 프로필 사진
        Glide.with(context).load(arrayList.get(holder.getAdapterPosition()).getProfile_url()).into(holder.img_profile);
        // 닉네임
        holder.txt_nickname.setText(arrayList.get(holder.getAdapterPosition()).getNickname());
        // 점세개(txt_function)클릭
            // 팔로워 뷰 => 삭제, 팔로잉
            // 팔로잉 뷰 => 팔로잉 취소
        holder.txt_function.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                mode_follow
                - = 팔로잉
                    From_login_value = 나
                    To_login_value = 클릭한 대상
                - = 팔로워
                    From_login_value = 클릭한 대상
                    To_login_value = 나
                 */
                String From_login_value = "";
                String To_login_value = "";
                AlertDialog.Builder builder
                        = new AlertDialog.Builder(activity);
                String[] str = {"A","B"};
                if(mode_follow.equals("follower")){
                    str = new String[]{"삭제", "팔로잉"};
                    From_login_value = arrayList.get(holder.getAdapterPosition()).getLogin_value();
                    To_login_value = fshared.get_login_value();

                }else if(mode_follow.equals("following")){
                    str = new String[]{"팔로잉 취소"};
                    From_login_value = fshared.get_login_value();
                    To_login_value = arrayList.get(holder.getAdapterPosition()).getLogin_value();
                }
                final String[] finalStr = str;
                final String finalFrom_login_value = From_login_value;
                final String finalTo_login_value = To_login_value;
                builder.setTitle("선택하세요")
                        .setNegativeButton("취소",null)
                        .setItems(str,// 리스트 목록에 사용할 배열
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d("실행","선택된것="+ finalStr[which]);

                                        String management = "";
                                        if(finalStr[which].equals("삭제")){
                                            management = "invisible";
                                        }else if(finalStr[which].equals("팔로잉")){
                                            management = "following";
                                        }else if(finalStr[which].equals("팔로잉 취소")){
                                            management = "delete_following";
                                        }

                                        Log.d("실행", "From_login_value="+ finalFrom_login_value);
                                        Log.d("실행", "To_login_value="+ finalTo_login_value);
                                        Log.d("실행", "management="+management);


                                        final String finalManagement = management;
                                        fs.Management_Follow(finalFrom_login_value, finalTo_login_value, management, new Function_Set.VolleyCallback() {
                                            @Override
                                            public void onSuccess(String result) {
                                                Log.d("실행", "(in Adapter)result="+result);

                                                if(result.equals("success")){
                                                    // 해당 position에 있는 아이템 삭제
                                                        // 팔로잉 삭제, 삭제인 경우
                                                    if(!finalManagement.equals("following")){
                                                        arrayList.remove(holder.getAdapterPosition());
                                                        notifyItemRemoved(holder.getAdapterPosition());
                                                    }
                                                }//end if
                                            }
                                        });
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

package com.remon.books.Adapter;
import android.widget.Toast;

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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.remon.books.Activity_Chatting_Room;
import com.remon.books.Activity_Edit_Chatting_Room;
import com.remon.books.Activity_Underline_Picture;
import com.remon.books.AppHelper;
import com.remon.books.Data.Data_Chatting_Room;
import com.remon.books.Data.Data_Img_Memo;
import com.remon.books.Function.Function_Set;
import com.remon.books.Function.Function_SharedPreference;
import com.remon.books.ItemTouchHelperListener;
import com.remon.books.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Adapter_Chatting_Room
    extends RecyclerView.Adapter<Adapter_Chatting_Room.CustomViewHolder>


{
    // Context
    Context context;
    Activity activity;

    // 데이터 셋팅
    private ArrayList<Data_Chatting_Room> arrayList;

    // 함수
    Function_SharedPreference fshared;


    // 생성자
    public Adapter_Chatting_Room(ArrayList<Data_Chatting_Room> arrayList, Context context,Activity activity){
        this.arrayList = arrayList;
        this.context = context;
        this.activity = activity;
        fshared = new Function_SharedPreference(context);
    }



    // 뷰홀더
    public class CustomViewHolder extends RecyclerView.ViewHolder{

        // 위젯정의
        protected TextView txt_title, txt_explain,txt_count, txt_total, txt_function;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.txt_title = itemView.findViewById(R.id.txt_title);
            this.txt_explain = itemView.findViewById(R.id.txt_explain);
            this.txt_count = itemView.findViewById(R.id.txt_count);
            this.txt_total = itemView.findViewById(R.id.txt_total);
            this.txt_function = itemView.findViewById(R.id.txt_function);
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

        // leader = login_value인 경우에만 txt_function보이도록
        if(fshared.get_login_value().equals(arrayList.get(holder.getAdapterPosition()).getLeader())){
            holder.txt_function.setVisibility(View.VISIBLE);
        }else{
            holder.txt_function.setVisibility(View.GONE);
        }

        // txt_function => 수정, 삭제
        holder.txt_function.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder
                        = new AlertDialog.Builder(activity);
                final String str[] = {"수정","삭제"};
                builder.setTitle("선택하세요")
                        .setNegativeButton("취소",null)
                        .setItems(str,// 리스트 목록에 사용할 배열
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d("실행","선택된것="+str[which]);

                                        if(str[which].equals("수정")){
                                            Intent intent = new Intent(context,Activity_Edit_Chatting_Room.class);
                                            intent.putExtra("idx",arrayList.get(holder.getAdapterPosition()).getIdx());
                                            activity.startActivity(intent);
                                        }else if(str[which].equals("삭제")){
                                            // 웹페이지 실행하기
                                            String url = context.getString(R.string.server_url)+"Delete_Room.php";

                                            StringRequest request = new StringRequest(
                                                    Request.Method.POST,
                                                    url,
                                                    new com.android.volley.Response.Listener<String>() { // 정상 응답
                                                        @Override
                                                        public void onResponse(String response) {
                                                            Log.d("실행","response=>"+response);

                                                            if(response.trim().equals("success")){
                                                                Toast.makeText(context, "성공적으로 삭제되었습니다",Toast.LENGTH_LONG).show();
                                                                arrayList.remove(holder.getAdapterPosition());
                                                                notifyItemRemoved(holder.getAdapterPosition());
                                                            }else{
                                                                Toast.makeText(context, "죄송합니다.문제가 생겼습니다.",Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    },
                                                    new com.android.volley.Response.ErrorListener() { // 에러 발생
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            Log.d("실행","error=>"+error.getMessage());
                                                        }
                                                    }

                                            ){ // Post 방식으로 body에 요청 파라미터를 넣어 전달하고 싶을 경우
                                                // 만약 헤더를 한 줄 추가하고 싶다면 getHeaders() override
                                                @Override
                                                protected Map<String, String> getParams() throws AuthFailureError {
                                                    Map<String, String> params = new HashMap<String, String>();
                                                    params.put("room_idx", arrayList.get(holder.getAdapterPosition()).getIdx()+"");
                                                    return params;
                                                }
                                            };

                                            // 요청 객체를 만들었으니 이제 requestQueue 객체에 추가하면 됨.
                                            // Volley는 이전 결과를 캐싱하므로, 같은 결과가 있으면 그대로 보여줌
                                            // 하지만 아래 메소드를 false로 set하면 이전 결과가 있더라도 새로 요청해서 응답을 보여줌.
                                            request.setShouldCache(false);
                                            AppHelper.requestQueue.add(request);

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

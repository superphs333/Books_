package com.remon.books.Adapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.remon.books.Activity_Edit_Memo;
import com.remon.books.Activity_Feed;
import com.remon.books.AppHelper;
import com.remon.books.Data.Data_Book_Memo;
import com.remon.books.Function.Function_Set;
import com.remon.books.Function.Function_SharedPreference;
import com.remon.books.R;
import com.remon.books.SliderAdapterExample;
import com.remon.books.SliderItem;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Adapter_Book_Memo
    extends RecyclerView.Adapter<Adapter_Book_Memo.CustomViewHolder>
{
    // Context
    Context context;
    Activity activity;

    // 데이터 셋팅
    private ArrayList<Data_Book_Memo> arrayList;

    // 함수
    Function_Set fs;
    Function_SharedPreference fshared;


    // 생성자
    public Adapter_Book_Memo(ArrayList<Data_Book_Memo> arrayList, Context context, Activity activity){
        this.arrayList = arrayList;
        this.context = context;
        this.activity = activity;
        fs = new Function_Set(context);
        fshared = new Function_SharedPreference(context);
    }



    // 뷰홀더
    public class CustomViewHolder extends RecyclerView.ViewHolder{

        // 위젯정의
        protected ImageView img_profile, img_heart, img_comment;
        protected TextView txt_nickname, txt_memo, txt_page, txt_book, txt_heart_count
                , txt_comment_count, txt_date_time, txt_function,txt_open,txt_follow;
        Spinner spinner_select_open;
        protected SliderView sliderView;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.img_profile = (ImageView)itemView.findViewById(R.id.img_profile);
            this.img_heart = (ImageView)itemView.findViewById(R.id.img_heart);
            this.img_comment = (ImageView)itemView.findViewById(R.id.img_comment);
            this.txt_nickname = (TextView)itemView.findViewById(R.id.txt_nickname);
            this.txt_memo = (TextView)itemView.findViewById(R.id.txt_memo);
            this.txt_page = (TextView)itemView.findViewById(R.id.txt_page);
            this.txt_book = (TextView)itemView.findViewById(R.id.txt_book);
            this.txt_heart_count = (TextView)itemView.findViewById(R.id.txt_heart_count);
            this.txt_comment_count = (TextView)itemView.findViewById(R.id.txt_comment_count);
            this.txt_date_time = (TextView)itemView.findViewById(R.id.txt_date_time);
            this.txt_function = (TextView)itemView.findViewById(R.id.txt_function);
            this.spinner_select_open = (Spinner)itemView.findViewById(R.id.spinner_select_open);
            this.sliderView =itemView.findViewById(R.id.sliderView);
            this.txt_open = itemView.findViewById(R.id.txt_open);
            this.txt_follow = itemView.findViewById(R.id.txt_follow);
        }
    }


    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view
                = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_book_memo,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, final int position) {



        // 프로필 이미지 셋팅
        Glide.with(holder.itemView.getContext())
                .load(arrayList.get(position).getProfile_url())
                .into(holder.img_profile);
        // 닉네임
        holder.txt_nickname.setText(arrayList.get(holder.getAdapterPosition()).getNickname());
        // 팔로잉 체크 : false인 경우에만 팔로우 표시
        if(!arrayList.get(holder.getAdapterPosition()).isFollow()){
            holder.txt_follow.setVisibility(View.VISIBLE);
        }else{
            holder.txt_follow.setVisibility(View.GONE);
        }
        // txt_follow => 팔로우
        holder.txt_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String From = fshared.get_login_value();
                final String To = arrayList.get(holder.getAdapterPosition()).getLogin_value();
                fs.Management_Follow(From, To, "following", new Function_Set.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d("실행", "result="+result);

                        String[] string_array= result.split("§");
                        if(string_array[0].trim().equals("success")){

                            int index = 0;
                            for(Data_Book_Memo i : arrayList){
                                if(i.getLogin_value().equals(To)){
                                    arrayList.get(index).setFollow(true);
                                    holder.txt_follow.setVisibility(View.GONE);
                                }
                                index++;
                            }

                            notifyDataSetChanged();

                            Toast.makeText(context, "팔로우 하였습니다!",Toast.LENGTH_LONG).show();

                        }else{
                            Toast.makeText(context, "죄송합니다. 문제가 발생하였습니다.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        // 페이지
        holder.txt_page.setText(arrayList.get(holder.getAdapterPosition()).getPage()+"p");
        // count_heart
        holder.txt_heart_count.setText(arrayList.get(holder.getAdapterPosition()).getCount_heart()+"");
        // count_comment
        holder.txt_comment_count.setText(arrayList.get(holder.getAdapterPosition()).getCount_comment()+"");
        // 책이름
        holder.txt_book.setText(arrayList.get(holder.getAdapterPosition()).getTitle());
//        holder.txt_book.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context,Activity_)
//            }
//        });
        // 내 메모만 보기 / 다른 사람 메모 보기 / 전체보 기
        // 날짜, 시간
        holder.txt_date_time.setText(arrayList.get(holder.getAdapterPosition()).getDate_time());
        // 메모
        holder.txt_memo.setText(arrayList.get(holder.getAdapterPosition()).getMemo());
        // OPEN : spinner 셋팅 -> 값에 따라 spinner 셋팅
        holder.spinner_select_open.setVisibility(View.GONE);
//        final String[] data = context.getResources().getStringArray(R.array.select_open);
//        ArrayAdapter<String> adapter
//                = new ArrayAdapter<String>(context,android.R.layout.simple_dropdown_item_1line,data);
//        holder.spinner_select_open.setAdapter(adapter);
//        Log.d("실행", "");
        if(arrayList.get(holder.getAdapterPosition()).getOpen().equals("all")){ // 전체공개
            //holder.spinner_select_open.setSelection(0);
            holder.txt_open.setText("전체");
        }else if(arrayList.get(holder.getAdapterPosition()).getOpen().equals("follow")){
            //holder.spinner_select_open.setSelection(1);
            holder.txt_open.setText("팔로우");
        }else{ // 비공개
            //holder.spinner_select_open.setSelection(2);
            holder.txt_open.setText("비공개");
        }

        // 프로필 이미지 클릭 -> 해당 피드
        holder.img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("실행", "arrayList.get(holder.getAdapterPosition()).isFollow()="+arrayList.get(holder.getAdapterPosition()).isFollow());

                Intent intent = new Intent(context, Activity_Feed.class);
                intent.putExtra("login_value",arrayList.get(holder.getAdapterPosition()).getLogin_value());
                intent.putExtra("nickname",arrayList.get(holder.getAdapterPosition()).getNickname());
                intent.putExtra("profile_url",arrayList.get(holder.getAdapterPosition()).getProfile_url());
                intent.putExtra("follow",arrayList.get(holder.getAdapterPosition()).isFollow());
                activity.startActivity(intent);
            }
        });

        /*
        이미지 가져오기
         */
        List<String> img_url_list = new ArrayList<>();
        String[] temp = arrayList.get(position).getImg_urls().split("§");
        for(int i=0; i<temp.length; i++){
            img_url_list.add(temp[i]);
        } // end for

        /*
        이미지 슬라이더
         */
        SliderAdapterExample adapter2;
        adapter2 = new SliderAdapterExample(context); // 어댑터 생성
        holder.sliderView.setSliderAdapter(adapter2); // 어댑터 셋팅
        // 슬라이드 설정
        holder.sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        holder.sliderView.setIndicatorSelectedColor(Color.WHITE);
        holder.sliderView.setIndicatorUnselectedColor(Color.GRAY);
        holder.sliderView.setScrollTimeInSec(3);
            // 스크롤 지연(초) 설정
        holder.sliderView.setAutoCycle(true);
            // 자동으로 뒤집기를 시작
        holder.sliderView.startAutoCycle();
        holder.sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        holder.sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        // 데이터 넣기
        List<SliderItem> sliderItemList = new ArrayList<>();
        for(int i=0; i<temp.length; i++){
            SliderItem sliderItem = new SliderItem();
            int j = i+1;
            sliderItem.setDescription(j+"/"+temp.length); // 설명셋팅
            sliderItem.setImageUrl(temp[i]); // 이미지 셋팅
            sliderItemList.add(sliderItem); // 리스트에 넣기
        }// end for
        // 슬라이더에 리스틑 반영
        adapter2.renewItems(sliderItemList);


        /*
        하트표시
        - 이미 별 표시를 했을 경우(check_heart=true) = 채운하트 ===> 빈별, 데이터베이스(DELETE)
        - 별표시를 하지 않았을 경우(check_heart=false) = 빈별 ===> 채운별, 데이터베이스(INSERT)

        전달값) idx_memo, login_value, check_heart
         */
        final boolean check_heart = arrayList.get(holder.getAdapterPosition()).getCheck_heart();
        Drawable drawable;
        if(check_heart==true){ // 체크 된 상태
            drawable = context.getResources().getDrawable(R.drawable.fill_heart);
        }else{  // 체크가 되지 않은 상태
            drawable = context.getResources().getDrawable(R.drawable.empty_heart);
        }
        holder.img_heart.setImageDrawable(drawable); // 하트모양 셋팅
        holder.img_heart.setOnClickListener(new View.OnClickListener() { // 클릭 -> 상태반대로 변경
            @Override
            public void onClick(View v) {
                Log.d("실행", "  check_heart="+check_heart);
                //Log.d("실행", "idx="+arrayList.get(holder.getAdapterPosition()).getIdx());
                final int idx = arrayList.get(holder.getAdapterPosition()).getIdx();

                // 별모양 상태 변경
                Drawable drawable = null;
                if(check_heart==true){ // 체크 된 상태
                    drawable = context.getResources().getDrawable(R.drawable.empty_heart); // 빈하트
                    arrayList.get(holder.getAdapterPosition()).setCheck_heart(false);
                }else{ // 체크가 되지 않은 상태
                    drawable = context.getResources().getDrawable(R.drawable.fill_heart); // 채운하트
                    arrayList.get(holder.getAdapterPosition()).setCheck_heart(true);
                }
                notifyDataSetChanged();

                // login_value
                Function_SharedPreference fshared = new Function_SharedPreference(context);
                final String login_value = fshared.get_login_value();
                //Log.d("실행", "login_value="+login_value);


                // 데이터베이스 상태반영
                // 웹페이지 실행하기
                String url = context.getString(R.string.server_url)+"Update_heart_check.php";

                StringRequest request = new StringRequest(
                        Request.Method.POST,
                        url,
                        new com.android.volley.Response.Listener<String>() { // 정상 응답
                            @Override
                            public void onResponse(String response) {
                                Log.d("실행","response=>"+response);

                                String[] temp = response.trim().split(context.getString(R.string.seperator));
                                if(temp[0].equals("success") && temp[1].equals("success")){
                                    Log.d("실행", "Count_heart="+temp[2]);

                                    // txt_heart_count 셋팅
                                    holder.txt_heart_count.setText(temp[2]);
                                    // 값셋팅
                                    arrayList.get(holder.getAdapterPosition()).setCount_heart(Integer.parseInt(temp[2]));
                                }else{
                                    Toast.makeText(context, "",Toast.LENGTH_LONG).show();
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
                        params.put("idx_memo",idx+"");
                        params.put("login_value", login_value);
                        params.put("check_heart", String.valueOf(check_heart));
                        return params;
                    }
                };

                // 요청 객체를 만들었으니 이제 requestQueue 객체에 추가하면 됨.
                // Volley는 이전 결과를 캐싱하므로, 같은 결과가 있으면 그대로 보여줌
                // 하지만 아래 메소드를 false로 set하면 이전 결과가 있더라도 새로 요청해서 응답을 보여줌.
                request.setShouldCache(false);
                AppHelper.requestQueue.add(request);

                notifyDataSetChanged();
            }
        });

        // txt_function => 삭제 or 수정
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

                                        Log.d("실행",
                                                "idx="+arrayList.get(holder.getAdapterPosition()).getIdx());

                                        if(str[which].equals("삭제")){
                                            Delete_Memo(arrayList.get(holder.getAdapterPosition()).getIdx(),holder.getAdapterPosition());
                                        }else{ // 수정
                                            Intent intent = new Intent(context, Activity_Edit_Memo.class);
                                            intent.putExtra("idx",arrayList.get(holder.getAdapterPosition()).getIdx());
                                            intent.putExtra("title",arrayList.get(holder.getAdapterPosition()).getTitle());
                                            activity.startActivity(intent);
                                        }
                                    }
                                }
                        );
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        // 메모 자세히 보기
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

    // 메모 데이터를 삭제
    public void Delete_Memo(final int idx, final int position){
        // 웹페이지 실행하기
        String url = context.getString(R.string.server_url)+"Delete_Book_Memo.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new com.android.volley.Response.Listener<String>() { // 정상 응답
                    @Override
                    public void onResponse(String response) {
                        Log.d("실행","response=>"+response);

                        if(response.equals("success")){
                            arrayList.remove(position);
                            notifyItemRemoved(position);
                        }else{
                            Toast.makeText(context, "문제가 발생하였습니다. 다시 시도해주세요",Toast.LENGTH_LONG).show();
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
                params.put("idx", String.valueOf(idx));
                return params;
            }
        };

        // 요청 객체를 만들었으니 이제 requestQueue 객체에 추가하면 됨.
        // Volley는 이전 결과를 캐싱하므로, 같은 결과가 있으면 그대로 보여줌
        // 하지만 아래 메소드를 false로 set하면 이전 결과가 있더라도 새로 요청해서 응답을 보여줌.
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);

    } // end Delete_Memo



}

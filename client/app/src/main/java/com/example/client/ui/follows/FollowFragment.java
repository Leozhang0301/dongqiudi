package com.example.client.ui.follows;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.client.FollowPickActivity;
import com.example.client.LoginActivity;
import com.example.client.MainActivity;
import com.example.client.R;
import com.example.client.TeamNewsActivity;
import com.example.client.UserManagerActivity;
import com.example.client.ui.news.NewsActivity;
import com.example.client.ui.news.NewsItem;
import com.example.client.ui.news.NewsItemAdapter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FollowFragment extends Fragment {
    private TextView textView;
    private Button loginBtn;
    private SharedPreferences sharedPreferences;
    private TextView userNickName;
    private Button followBtn;
    private List<NewsItem> newsItems;
    private NewsItemAdapter itemAdapter;
    private ListView newslist;
    private Button followBtn2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root=null;
        sharedPreferences=getActivity().getSharedPreferences("user-info", Context.MODE_PRIVATE);
        String isLogin=sharedPreferences.getString("user_name","none");
        //没有登陆
        if(isLogin.equals("none")){
            root=inflater.inflate(R.layout.not_logined,container,false);
            textView=(TextView)root.findViewById(R.id.text_follow);
            loginBtn=(Button)root.findViewById(R.id.login_btn);
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent();
                    intent.setClass(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            });
            textView.setText("还没有登录哦~");

        }else {
            root=inflater.inflate(R.layout.fragment_follow,container,false);
            String getnewsURL="http://8.129.27.254:8000/getnewsbyusername?username="+sharedPreferences.getString("user_name","none");
            userNickName=(TextView)root.findViewById(R.id.user_nick_name);
            userNickName.setText(sharedPreferences.getString("user_name","none"));
            TextView tv=(TextView)root.findViewById(R.id.emptylist);
            newsItems=new LinkedList<>();
            newslist=(ListView)root.findViewById(R.id.followed_news);
            followBtn=(Button)root.findViewById(R.id.follow_btn);
            followBtn2=(Button)root.findViewById(R.id.follow_btn2);
            userNickName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent();
                    intent.setClass(getActivity(), UserManagerActivity.class);
                    startActivity(intent);
                }
            });
            OkHttpClient okHttpClient= MainActivity.okHttpClient;
            Request request=new Request.Builder()
                    .get()
                    .url(getnewsURL)
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.d("kwwl","onFailure"+e.toString());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Log.d("kwwl","onResponse"+response.code());
                    String string=response.body().string();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray jsonArray=new JSONArray(string);
                                //关注的球队没有新闻
                                if (jsonArray.length()==0){

                                }else {
                                    followBtn.setVisibility(View.INVISIBLE);
                                    tv.setVisibility(View.INVISIBLE);
                                    followBtn2.setVisibility(View.VISIBLE);
                                    followBtn2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent=new Intent();
                                            intent.setClass(getActivity(), FollowPickActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                                    for (int i=0;i<jsonArray.length();i++){
                                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                                        String title=jsonObject.getString("标题");
                                        String publishTime=jsonObject.getString("时间");
                                        String contentURL="http://8.129.27.254/news/"+title+".html" ;
                                        String coverURL=jsonObject.getString("封面");
                                        newsItems.add(new NewsItem(title,publishTime,coverURL,contentURL));
                                    }
                                    itemAdapter=new NewsItemAdapter((LinkedList<NewsItem>)newsItems,getContext());
                                    newslist.setAdapter(itemAdapter);
                                    //绑定列表点击事件
                                    newslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Intent intent=new Intent();
                                            Bundle bundle=new Bundle();
                                            bundle.putInt("position",position);
                                            //新建一个newitem对象来获取点击的对象
                                            NewsItem item=(NewsItem) newsItems.get(position);
                                            bundle.putString("url",item.getUrl());
                                            bundle.putString("title",item.getTitle());
                                            bundle.putString("time",item.getPublishTime());
                                            intent.putExtras(bundle);
                                            intent.setClass(getActivity(), NewsActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            });
            followBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent();
                    intent.setClass(getActivity(), FollowPickActivity.class);
                    startActivity(intent);

                }
            });
        }

        return root;
    }
}

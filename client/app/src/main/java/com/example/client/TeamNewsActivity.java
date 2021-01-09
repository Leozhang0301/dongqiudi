package com.example.client;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.client.ui.news.NewsActivity;
import com.example.client.ui.news.NewsItem;
import com.example.client.ui.news.NewsItemAdapter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TeamNewsActivity extends AppCompatActivity {
    private Bundle bundle;
    private Intent intent;
    private ListView listView;
    private String URL="http://8.129.27.254:8000/getnewsbytag?tag=";
    private List<NewsItem> newsItems;
    private NewsItemAdapter itemAdapter;
    private JSONArray jsonArray;
    private TextView empty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_team_news);
        listView=(ListView)findViewById(R.id.team_news_list);
        empty=(TextView)findViewById(R.id.empty_news_list);
        intent=getIntent();
        bundle=intent.getExtras();

        URL+=bundle.getString("team");
        newsItems=new LinkedList<>();
        setdata();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                intent.setClass(TeamNewsActivity.this, NewsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setdata() {
        OkHttpClient client= MainActivity.okHttpClient;
        Request request=new Request.Builder()
                .get()
                .url(URL)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("kwwl","onFailure"+e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("kwwl","onResponse"+response.code());
                String string=response.body().string();
                Log.d("TeamNewsList", string);
                if (string.equals("[]")){
                    //没有数据
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            empty.setVisibility(View.VISIBLE);
                        }
                    });

                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                jsonArray=new JSONArray(string);
                                for (int i=0;i<jsonArray.length();i++){
                                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                                    String title=jsonObject.getString("标题");
                                    String publishTime=jsonObject.getString("时间");
                                    String contentURL="http://8.129.27.254/news/"+title+".html" ;
                                    String coverURL=jsonObject.getString("封面");
                                    newsItems.add(new NewsItem(title,publishTime,coverURL,contentURL));
                                }
                                itemAdapter=new NewsItemAdapter((LinkedList<NewsItem>)newsItems,getApplicationContext());
                                listView.setAdapter(itemAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }


            }
        });
    }
}

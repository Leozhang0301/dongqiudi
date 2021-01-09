package com.example.client.ui.news;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.client.MainActivity;
import com.example.client.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsActivity extends AppCompatActivity {
    private Bundle extras;
    private Intent intent;
    private WebView webView;
    private TextView empty;
    private TextView titleText;
    private TextView timeText;
    private ListView commentList;
    private String URL="http://8.129.27.254:8000/getcomments?news=";
    private JSONArray jsonArray;
    private LinkedList<Comment> commentLinkedList;
    private CommentAdapter commentAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_news);
        commentLinkedList=new LinkedList<>();
        empty=(TextView)findViewById(R.id.empty_comment_list);
        webView=(WebView)findViewById(R.id.news_webview);
        titleText=(TextView)findViewById(R.id.news_content_title);
        timeText=(TextView)findViewById(R.id.news_content_time);
        commentList=(ListView)findViewById(R.id.comments_list);
        intent=getIntent();
        extras= intent.getExtras();
        int position=(int)extras.get("position");
        String url=extras.getString("url");
        String title=extras.getString("title");
        String time=extras.getString("time");
        String newsID=extras.getString("ID");
        URL+=newsID;
        titleText.setText(title);
        timeText.setText(time);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBlockNetworkLoads(false);
        setComments();
    }

    private void setComments() {
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
                Log.d("NewsFragment", string);
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
                                    String userName=jsonObject.getString("姓名");
                                    String content=jsonObject.getString("内容");
                                    String time=jsonObject.getString("时间");
                                    commentLinkedList.add(new Comment(userName,content,time));
                                }
                                commentAdapter=new CommentAdapter((LinkedList<Comment>)commentLinkedList,getApplicationContext());
                                commentList.setAdapter(commentAdapter);
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

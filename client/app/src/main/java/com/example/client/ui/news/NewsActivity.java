package com.example.client.ui.news;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.client.LoginActivity;
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
    private String getCommentURL="http://8.129.27.254:8000/getcomments?news=";
    private JSONArray jsonArray;
    private LinkedList<Comment> commentLinkedList;
    private CommentAdapter commentAdapter;
    private Button publishBtn;
    private EditText commentText;
    private String newsID;
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
        commentText=(EditText)findViewById(R.id.new_content_comment);
        publishBtn=(Button)findViewById(R.id.comment_publish_btn);
        publishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitComment();
            }
        });
        intent=getIntent();
        extras= intent.getExtras();
        int position=(int)extras.get("position");
        String url=extras.getString("url");
        String title=extras.getString("title");
        String time=extras.getString("time");
        newsID=extras.getString("ID");
        getCommentURL+=newsID;
        titleText.setText(title);
        timeText.setText(time);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBlockNetworkLoads(false);
        setComments();
    }

    private void submitComment() {
        AlertDialog.Builder dialog= new AlertDialog.Builder(this);
        SharedPreferences sp=getSharedPreferences("user-info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        //获取登录状态
        String userName=sp.getString("user_name","none");
        //如果没有登录
        if (userName.equals("none")){
            dialog.setTitle("错误");
            dialog.setMessage("还没有登录哦~");
            dialog.setPositiveButton("去登陆", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    intent.setClass(NewsActivity.this, LoginActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.show();
                }
            });
        }else {
            String content=commentText.getText().toString();
            String commentURL="http://8.129.27.254:8000/submitcomment?newsid="+newsID+"&username="+userName+"&content="+content;
            OkHttpClient client=MainActivity.okHttpClient;
            Request request=new Request.Builder()
                    .get()
                    .url(commentURL)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.d("kwwl","onFailure"+e.toString());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Log.d("kwwl","onResponse"+response.code());
                    dialog.setTitle("成功");
                    dialog.setMessage("评论成功");
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            commentText.setText("");
                            //刷新评论列表
                            setComments();
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.show();
                        }
                    });
                }
            });
        }
    }

    private void setComments() {
        OkHttpClient client= MainActivity.okHttpClient;
        Request request=new Request.Builder()
                .get()
                .url(getCommentURL)
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
                                getListViewSelfHeight(commentList);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }

            }
        });
    }

    private void getListViewSelfHeight(ListView commentList) {
        ListAdapter listAdapter=commentList.getAdapter();
        int totalHeight=0;
        for(int i=0;i<listAdapter.getCount();i++){
            View listItem=listAdapter.getView(i,null,commentList);
            listItem.measure(0,0);
            totalHeight+=listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params=commentList.getLayoutParams();
        params.height=totalHeight+(commentList.getDividerHeight()*(listAdapter.getCount()-1));
        commentList.setLayoutParams(params);
    }
}

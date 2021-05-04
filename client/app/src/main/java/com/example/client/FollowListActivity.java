package com.example.client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.client.ui.follows.FollowListAdapter;
import com.example.client.ui.follows.TeamFollowItem;

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

public class FollowListActivity extends AppCompatActivity {
    private Button goFollow;
    private ListView listView;
    private SharedPreferences sharedPreferences;
    private String URL="http://8.129.27.254:8000/getfollowed?username=";
    private JSONArray jsonArray;
    private FollowListAdapter followListAdapter;
    private LinkedList<TeamFollowItem> mData=null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.followed_list_layout);
        goFollow=findViewById(R.id.gofollow);
        listView=findViewById(R.id.follow_team_list);
        sharedPreferences=getSharedPreferences("user-info", Context.MODE_PRIVATE);
        mData=new LinkedList<TeamFollowItem>();
        URL+=sharedPreferences.getString("user_name","none");
        goFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(FollowListActivity.this, FollowPickActivity.class);
                startActivity(intent);
            }
        });
        GetData();
        SetClick();
    }

    private void SetClick() {
    }

    private void GetData() {
        OkHttpClient okHttpClient= MainActivity.okHttpClient;
        Request request=new Request.Builder()
                .get()
                .url(URL)
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            jsonArray=new JSONArray(string);
                            for (int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject=jsonArray.getJSONObject(i);
                                String name=jsonObject.getString("队名");
                                //获得队标
                                String icon_url ="http://8.129.27.254/image/team_icon/"+name+".png";
                                mData.add(new TeamFollowItem(name,icon_url));
                            }
                            followListAdapter=new FollowListAdapter(mData,FollowListActivity.this);
                            listView.setAdapter(followListAdapter);
                            getListViewSelfHeight(listView);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    //重写onMeasured方法
    //解决scrollView嵌套listView不能显示完全的错误
    public void getListViewSelfHeight(ListView listView){
        ListAdapter listAdapter=listView.getAdapter();
        int totalHeight=0;
        for(int i=0;i<listAdapter.getCount();i++){
            View listItem=listAdapter.getView(i,null,listView);
            listItem.measure(0,0);
            totalHeight+=listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params=listView.getLayoutParams();
        params.height=totalHeight+(listView.getDividerHeight()*(listAdapter.getCount()-1));
        listView.setLayoutParams(params);
    }
}

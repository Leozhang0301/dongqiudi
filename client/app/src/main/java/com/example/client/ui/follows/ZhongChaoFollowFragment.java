package com.example.client.ui.follows;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.client.FollowPickActivity;
import com.example.client.MainActivity;
import com.example.client.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ZhongChaoFollowFragment extends Fragment {
    private GridView gridView;
    private Class<FollowPickActivity> context;
    private FollowTeamAdapter followTeamAdapter=null;
    private LinkedList<TeamFollowItem> mData=null;
    private String URL="http://8.129.27.254:8000/queryteamname?tablename=zhongchao";
    private JSONArray jsonArray;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.teams_grid,container,false);
        context= FollowPickActivity.class;
        gridView=root.findViewById(R.id.team_follow_grid);
        mData=new LinkedList<TeamFollowItem>();
        GetData();
        setClickListener();
        return root;
    }

    private void setClickListener() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getActivity(),"click"+mData.get(position).getTeamName(),Toast.LENGTH_SHORT).show();

                SharedPreferences sp=getActivity().getSharedPreferences("user-info", Context.MODE_PRIVATE);
                String username=sp.getString("user_name","none");

                OkHttpClient okHttpClient=MainActivity.okHttpClient;
                String addFollowURL="http://8.129.27.254:8000/addfollow?username="+username+"&teamname="+mData.get(position).getTeamName();
                Request request=new Request.Builder()
                        .get()
                        .url(addFollowURL)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.d("kwwl","onFailure"+e.toString());
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        Log.d("kwwl","onResponse"+response.code());
                        String respon=response.body().string();
                        if (respon.equals("已关注")){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
                                    dialog.setMessage("您已经关注了该球队，是否需要取消关注");
                                    dialog.setTitle("警告");
                                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String disfollowURL="http://8.129.27.254:8000/disfollow?username="+username+"&teamname="+mData.get(position).getTeamName();
                                            Request request1=new Request.Builder()
                                                    .get()
                                                    .url(disfollowURL)
                                                    .build();
                                            okHttpClient.newCall(request1).enqueue(new Callback() {
                                                @Override
                                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                                    Log.d("kwwl","onFailure"+e.toString());
                                                }

                                                @Override
                                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(getContext(),"取消成功",Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.show();
                                }
                            });

                        }else if (respon.equals("成功")){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(),"关注成功",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            jsonArray=new JSONArray(string);
                            for (int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject=jsonArray.getJSONObject(i);
                                String qiudui=jsonObject.getString("队名");
                                //获得队标
                                String icon_url ="http://8.129.27.254/image/team_icon/"+qiudui+".png";
                                mData.add(new TeamFollowItem(qiudui,icon_url));
                                Log.d("kwwl",jsonObject.toString());
                            }
                            followTeamAdapter=new FollowTeamAdapter((LinkedList<TeamFollowItem>)mData,getActivity());
                            gridView.setAdapter(followTeamAdapter);
                            //getGridViewSelfHeight(gridView);
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
    public void getGridViewSelfHeight(GridView gridView){
        ListAdapter listAdapter=gridView.getAdapter();
        if (listAdapter==null){
            return;
        }
        int rows;
        int columns=3;
        int horizontalBorderHeight=0;
        Class<?> clazz=gridView.getClass();
        try {
            Field column=clazz.getDeclaredField("mRequestedNumColumns");
            column.setAccessible(true);
            columns=(Integer)column.get(gridView);

            Field horizontalSpacing=clazz.getDeclaredField("mRequestedHorizontalSpacing");
            horizontalSpacing.setAccessible(true);
            horizontalBorderHeight=(Integer)horizontalSpacing.get(gridView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(listAdapter.getCount()%columns>0){
            rows=listAdapter.getCount()/columns+1;
        }else {
            rows=listAdapter.getCount()/columns;
        }
        int totalHeight=0;
        for (int i=0; i<rows;i++){
            View listItem=listAdapter.getView(i,null,gridView);
            listItem.measure(0,0);
            totalHeight+=listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params=gridView.getLayoutParams();
        params.height=totalHeight+horizontalBorderHeight*(rows-1);
        gridView.setLayoutParams(params);
    }
}

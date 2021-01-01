package com.example.client.ui.ranking;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.example.client.MainActivity;
import com.example.client.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ZhongChaoRankFragment extends Fragment {
    private ImageView team_icon;
    private String URL="http://8.129.27.254:8000/queryrank?tablename=zhongchao";
    private OkHttpClient client=new OkHttpClient();

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //处理数据
        }
    };
    private JSONArray jsonArray;

    private List<RankItem> rankItemList;
    private RankItemAdapter rankItemAdapter;
    private ListView listView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.rank_list,container,false);
        listView=root.findViewById(R.id.rank_team);
        team_icon=root.findViewById(R.id.team_icon);
        rankItemList=new LinkedList<>();
        GetData();

        //加入数据
        //rankItemList.add(new RankItem("1","河南建业","0","0","0", "0","0","0","0"));


        return root;
    }

    private void GetData(){
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
                //Log.d("kwwl","onFailure"+response.body().string());
                String string=response.body().string();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            jsonArray=new JSONArray(string);
                            for (int i =0;i<jsonArray.length();i++){
                                JSONObject jsonObject=jsonArray.getJSONObject(i);
                                String paiming=jsonObject.getString("排名");
                                String qiudui=jsonObject.getString("球队名");
                                String changci=jsonObject.getString("场次");
                                String sheng=jsonObject.getString("胜");
                                String ping=jsonObject.getString("平");
                                String fu=jsonObject.getString("负");
                                String jinqiu=jsonObject.getString("进球");
                                String shiqiu=jsonObject.getString("失球");
                                String jifen=jsonObject.getString("积分");
                                //获得队标
                                String icon_url ="http://8.129.27.254/image/team_icon/"+qiudui+".png";
                                rankItemList.add(new RankItem(paiming,icon_url,qiudui,changci,sheng,ping,fu,jinqiu,shiqiu,jifen));
                                Log.d("kwwl",jsonObject.toString());

                            }
                            rankItemAdapter=new RankItemAdapter((LinkedList<RankItem>)rankItemList,getActivity());
                            listView.setAdapter(rankItemAdapter);
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

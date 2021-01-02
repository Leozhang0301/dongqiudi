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
import android.widget.TextView;

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

public class YingChaoRankFragment extends Fragment {
    private TextView leagueRule;
    private ImageView team_icon;
    private String URL="http://8.129.27.254:8000/queryrank?tablename=yingchao";

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
        leagueRule=root.findViewById(R.id.league_rules);
        rankItemList=new LinkedList<>();
        GetData();
        setRule();
        //加入数据
        //rankItemList.add(new RankItem("1","河南建业","0","0","0", "0","0","0","0"));


        return root;
    }

    //设置联赛规则
    private void setRule() {
        String rule="2020-2021英超联赛名次决定方法：\n"+
                "英格兰超级联赛共由20支参赛球队组成，主客场双循环赛制制作赛，每场赛事"+
                "胜者得3分，平局两队各得1分，负者不得分，38轮过后以积分多少决定最终排名。"+
                "若同分则依次以下列方式决定排名:\n"+
                "a) 净胜球多者，名次列前；\n"+
                "b) 进球多者，名次列前；\n"+
                "若涉及到冠军，欧战，降级名额的争夺，且以上选项都决定不出名次，则继续比较\n"+
                "c) 相互对阵积分多者，名次列前；\n"+
                "d）相互对阵客场进球多者，名次列前；\n"+
                "e）附加赛\n"+
                "\n"+
                "欧战&升降级名额：\n"+
                "（一）联赛前4名直接参加下赛季欧冠联赛;\n"+
                "（二）第5名参加下赛季欧联杯;\n"+
                "（三）英格兰足总杯冠军可参加欧联杯，如其已获得欧战资格，则名额让给联赛未获得欧战资格中排名靠前的球队\n"+
                "（四）若欧冠，欧联冠军都是英超球队，且两支球队都未通过联赛获得欧冠资格，则第4名参加欧联杯\n"+
                "（五）排名最后3名的球队降入英冠联赛";
        leagueRule.setText(rule);

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

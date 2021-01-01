package com.example.client.ui.matches;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.client.R;
import com.example.client.ui.follows.FollowViewModel;

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

public class MatchesFragment extends Fragment {
    private ListView matchesList;
    private String URL="http://8.129.27.254:8000/getmatches";
    private List<MatchItem> matchItems;
    private MatchItemAdapter matchItemAdapter;
    private JSONArray jsonArray;
    private ImageView homeTeamIcon;
    private ImageView awayTeamIcon;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_matches,container,false);
        matchesList=(ListView)root.findViewById(R.id.matches_list);
        matchItems=new LinkedList<>();
        homeTeamIcon=(ImageView)root.findViewById(R.id.home_team_icon);
        awayTeamIcon=(ImageView)root.findViewById(R.id.away_team_icon);
        GetData();
        return root;

    }

    private void GetData() {
        OkHttpClient okHttpClient=new OkHttpClient.Builder()
                .connectTimeout(8000, TimeUnit.MILLISECONDS)
                .build();
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
                String responseBody=response.body().string();
                Log.d("MatchesFragment", "response:" + response);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            jsonArray=new JSONArray(responseBody);
                            for (int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject=jsonArray.getJSONObject(i);
                                String date=jsonObject.getString("日期");
                                String time=jsonObject.getString("时间");
                                String league=jsonObject.getString("联赛");
                                String homeTeam=jsonObject.getString("主队");
                                String result=jsonObject.getString("比分");
                                String awayTeam=jsonObject.getString("客队");
                                //获得队标
                                String homeTeamIcon ="http://8.129.27.254/image/team_icon/"+homeTeam+".png";
                                //获得队标
                                String awayTeamIcon ="http://8.129.27.254/image/team_icon/"+awayTeam+".png";
                                matchItems.add(new MatchItem(date,time,homeTeam,awayTeam,result,league,homeTeamIcon,awayTeamIcon));
                                Log.d("kwwl",jsonObject.toString());
                            }
                            matchItemAdapter=new MatchItemAdapter((LinkedList<MatchItem>)matchItems,getActivity());
                            matchesList.setAdapter(matchItemAdapter);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }
}

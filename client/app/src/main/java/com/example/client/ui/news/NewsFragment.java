package com.example.client.ui.news;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.client.MainActivity;
import com.example.client.R;
import com.example.client.ui.matches.MatchesViewModel;
import com.google.gson.Gson;
import com.stx.xhb.androidx.XBanner;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsFragment extends Fragment {
    private XBanner xBanner;
    private String url="http://8.129.27.254:8000/getnews";
    private JSONArray jsonArray;

    private ListView newslist;
    private List<NewsItem> newsItems;
    private NewsItemAdapter itemAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root =inflater.inflate(R.layout.fragment_news,container,false);
        xBanner=(XBanner) root.findViewById(R.id.image_banner);
        newslist=(ListView)root.findViewById(R.id.news_list);

        newsItems=new LinkedList<>();
        initBanner();
        loadData();
        setData();
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
                bundle.putString("ID",item.getNewsID());
                intent.putExtras(bundle);
                intent.setClass(getActivity(),NewsActivity.class);
                startActivity(intent);
            }
        });
        //list.add()
        //xBanner.setBannerData();
        return root;
    }

    private void setData(){
        OkHttpClient client= MainActivity.okHttpClient;
        Request request=new Request.Builder()
                .get()
                .url(url)
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
                getActivity().runOnUiThread(new Runnable() {
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
                                String newsID=jsonObject.getString("ID");
                                newsItems.add(new NewsItem(title,publishTime,coverURL,contentURL,newsID));
                            }
                            itemAdapter=new NewsItemAdapter((LinkedList<NewsItem>)newsItems,getActivity());
                            newslist.setAdapter(itemAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
//        newsItems.add(new NewsItem("英超战报：利物浦1-1西布朗，马内破门，马蒂普伤退","12-28 02:20",R.drawable.first,"http://8.129.27.254/news/1234.html"));
//        newsItems.add(new NewsItem("梅西：冬窗不会与其他球队谈判；巴萨就是我的生命","12-28 05:11",R.drawable.second,"http://8.129.27.254/news/hello.html"));
//        newsItems.add(new NewsItem("C罗：希望有一天我进球时，全世界的孩子们都会欢呼","12-28 02:31",R.drawable.third,"https://www.dongqiudi.com/news/1758096.html"));
//        newsItems.add(new NewsItem("意媒：马尔库特不在那不勒斯的计划中，1月可能被清洗","12-28 10:29",R.drawable.forth,"https://www.dongqiudi.com/news/1758371.html"));
//        newsItems.add(new NewsItem("挂帅武汉卓尔！李霄鹏：这是一种缘分”","12-28 00:13",R.drawable.fifth,"https://www.dongqiudi.com/news/1758017.html"));
//        newsItems.add(new NewsItem("颜骏凌：我的眼伤已经基本上没问题，准备好回归球场","12-27 21:23",R.drawable.sixth,"https://www.dongqiudi.com/news/1757809.html"));
//        newsItems.add(new NewsItem("奥拉罗尤：不排除未来去欧洲执教，只要报价合适都会考虑","12-27 22:49",R.drawable.seventh,"https://www.dongqiudi.com/news/1757918.html"));
//        newsItems.add(new NewsItem("伊布：只有马拉多纳比我更强；也许在未来我会成为教练","12-27 19:27",R.drawable.eighth,"https://www.dongqiudi.com/news/1757679.html"));
//        newsItems.add(new NewsItem("蒿俊闵参加公益赛获队友盛赞：他很快就适应了我们的打法","12-27 23:28",R.drawable.nineth,"https://www.dongqiudi.com/news/1757967.html"));
//        newsItems.add(new NewsItem("韩媒：深足后卫宋株熏已同意加盟济州联","12-27 19:07",R.drawable.tenth,"https://www.dongqiudi.com/news/1757666.html"));
//        itemAdapter=new NewsItemAdapter((LinkedList<NewsItem>)newsItems,getActivity());
//        newslist.setAdapter(itemAdapter);
    }



    private void initBanner() {
        xBanner.setOnItemClickListener(new XBanner.OnItemClickListener() {
            @Override
            public void onItemClick(XBanner banner, Object model, View view, int position) {
                Toast.makeText(getContext(),"点击了第"+position+1+"图片",Toast.LENGTH_SHORT).show();
            }
        });
        xBanner.loadImage(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                TuchongEntity.FeedListBean.EntryBean listBean=((TuchongEntity.FeedListBean.EntryBean) model);
                String url = "https://photo.tuchong.com/" + listBean.getImages().get(0).getUser_id() + "/f/" + listBean.getImages().get(0).getImg_id() + ".jpg";
                Glide.with(getActivity()).load(url).into((ImageView)view);
            }
        });
        List<TuchongEntity.FeedListBean.EntryBean> data=new ArrayList<>();
        xBanner.setBannerData(data);
    }


    private void loadData() {
        //加载网络图片资源
        String url = "https://api.tuchong.com/2/wall-paper/app";
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(getContext(), "加载广告数据失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        TuchongEntity advertiseEntity = new Gson().fromJson(response, TuchongEntity.class);
                        List<TuchongEntity.FeedListBean> others = advertiseEntity.getFeedList();
                        List<TuchongEntity.FeedListBean.EntryBean> data = new ArrayList<>();
                        for (int i = 0; i <others.size(); i++) {
                            TuchongEntity.FeedListBean feedListBean = others.get(i);
                            if ("post".equals(feedListBean.getType())) {
                                data.add(feedListBean.getEntry());
                            }
                        }
                        //刷新数据之后，需要重新设置是否支持自动轮播
                        //xBanner.setAutoPlayAble(data.size() > 1);
                        xBanner.setBannerData(data);
                    }
                });

    }
}

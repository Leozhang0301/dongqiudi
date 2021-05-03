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
import com.example.client.UserManagerActivity;
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
//        initBanner();
//        loadData();
        bannerData();
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

    private void bannerData() {
        ArrayList<String> image=new ArrayList<>();
        image.add("http://yjq-fiies.oss-cn-beijing.aliyuncs.com/imgs/dongqiudi/hjyHKEnnNp.jpg");
        image.add("http://yjq-fiies.oss-cn-beijing.aliyuncs.com/imgs/dongqiudi/MQxbwtxYWW.jpg");

        xBanner.setOnItemClickListener(new XBanner.OnItemClickListener() {
            @Override
            public void onItemClick(XBanner banner, Object model, View view, int position) {
                Intent intent=new Intent();
                intent.setClass(getActivity(), UserManagerActivity.class);
                startActivity(intent);
            }
        });
        xBanner.setData(image,null);
        xBanner.loadImage(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                Glide.with(getActivity())
                        .load(image.get(position))
                        .into((ImageView)view);
            }
        });
    }

    //加载新闻列表
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
    }



//    private void initBanner() {
//        xBanner.setOnItemClickListener(new XBanner.OnItemClickListener() {
//            @Override
//            public void onItemClick(XBanner banner, Object model, View view, int position) {
//                Toast.makeText(getContext(),"点击了第"+position+1+"图片",Toast.LENGTH_SHORT).show();
//            }
//        });
//        xBanner.loadImage(new XBanner.XBannerAdapter() {
//            @Override
//            public void loadBanner(XBanner banner, Object model, View view, int position) {
//                TuchongEntity.FeedListBean.EntryBean listBean=((TuchongEntity.FeedListBean.EntryBean) model);
//                String url = "https://photo.tuchong.com/" + listBean.getImages().get(0).getUser_id() + "/f/" + listBean.getImages().get(0).getImg_id() + ".jpg";
//                Glide.with(getActivity()).load(url).into((ImageView)view);
//            }
//        });
//    }
//
//
//    private void loadData() {
//        //加载网络图片资源
//        String url = "https://api.tuchong.com/2/wall-paper/app";
//        OkHttpUtils
//                .get()
//                .url(url)
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        Toast.makeText(getContext(), "加载广告数据失败", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//                        TuchongEntity advertiseEntity = new Gson().fromJson(response, TuchongEntity.class);
//                        List<TuchongEntity.FeedListBean> others = advertiseEntity.getFeedList();
//                        List<TuchongEntity.FeedListBean.EntryBean> data = new ArrayList<>();
//                        for (int i = 0; i <others.size(); i++) {
//                            TuchongEntity.FeedListBean feedListBean = others.get(i);
//                            if ("post".equals(feedListBean.getType())) {
//                                data.add(feedListBean.getEntry());
//                            }
//                        }
//                        //刷新数据之后，需要重新设置是否支持自动轮播
//                        //xBanner.setAutoPlayAble(data.size() > 1);
//                        xBanner.setBannerData(data);
//                    }
//                });
//
//    }
}

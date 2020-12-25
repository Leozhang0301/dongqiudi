package com.example.client.ui.news;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.client.R;
import com.example.client.ui.matches.MatchesViewModel;
import com.google.gson.Gson;
import com.stx.xhb.androidx.XBanner;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;

public class NewsFragment extends Fragment {
    private XBanner xBanner;
    private String imgURL="http://8.129.27.254/image/team_icon/河南建业.png";

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
        //list.add()
        //xBanner.setBannerData();
        return root;
    }

    private void setData(){
        newsItems.add(new NewsItem("国内媒体:李晓鹏将执教武汉卓尔","这是新闻发布时间",R.mipmap.ic_launcher));
        newsItems.add(new NewsItem("这是新闻标题2","这是新闻发布时间",R.mipmap.ic_launcher));
        newsItems.add(new NewsItem("这是新闻标题3","这是新闻发布时间",R.mipmap.ic_launcher));
        newsItems.add(new NewsItem("这是新闻标题4","这是新闻发布时间",R.mipmap.ic_launcher));
        newsItems.add(new NewsItem("这是新闻标题4","这是新闻发布时间",R.mipmap.ic_launcher));
        newsItems.add(new NewsItem("这是新闻标题4","这是新闻发布时间",R.mipmap.ic_launcher));
        newsItems.add(new NewsItem("这是新闻标题4","这是新闻发布时间",R.mipmap.ic_launcher));
        newsItems.add(new NewsItem("这是新闻标题4","这是新闻发布时间",R.mipmap.ic_launcher));
        newsItems.add(new NewsItem("这是新闻标题4","这是新闻发布时间",R.mipmap.ic_launcher));
        newsItems.add(new NewsItem("这是新闻标题4","这是新闻发布时间",R.mipmap.ic_launcher));
        itemAdapter=new NewsItemAdapter((LinkedList<NewsItem>)newsItems,getActivity());
        newslist.setAdapter(itemAdapter);
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

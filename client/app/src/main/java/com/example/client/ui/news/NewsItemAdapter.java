package com.example.client.ui.news;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.client.MainActivity;
import com.example.client.R;
import com.example.client.ui.ranking.RankItemAdapter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

public class NewsItemAdapter extends BaseAdapter {
    private LinkedList<NewsItem> linkedList;
    private Context context;
    private LruCache<String, BitmapDrawable> mImageCache;
    private ListView listView;

    public NewsItemAdapter(LinkedList<NewsItem> linkedList, Context context) {
        this.linkedList = linkedList;
        this.context = context;
        mImageCache= MainActivity.mImageCache;
    }

    @Override
    public int getCount() {
        return linkedList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (listView==null){
            listView=(ListView)parent;
        }
        ViewHolder viewHolder=null;
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.news_list_item,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.newsTitle=(TextView)convertView.findViewById(R.id.news_title);
            viewHolder.newsPublishTime=(TextView)convertView.findViewById(R.id.news_publish_time);
            viewHolder.newsCover=(ImageView) convertView.findViewById(R.id.news_first_pic);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder=(ViewHolder) convertView.getTag();
        }
        viewHolder.newsTitle.setText(linkedList.get(position).getTitle());
        viewHolder.newsPublishTime.setText(linkedList.get(position).getPublishTime());
        //viewHolder.newsPic.setImageResource(linkedList.get(position).getCoverID());
        //新闻封面
        viewHolder.newsCover.setTag(linkedList.get(position).getCoverURL());
        //如果本地已有缓存，就从本地获取，如果本地没有则网络请求
        if (mImageCache.get(linkedList.get(position).getCoverURL())!=null){
            viewHolder.newsCover.setImageDrawable(mImageCache.get(linkedList.get(position).getCoverURL()));
        }else {
            ImageTask imageTask=new ImageTask();
            imageTask.execute(linkedList.get(position).getCoverURL());
        }
        return convertView;
    }

    public static class ViewHolder{
        TextView newsTitle;
        TextView newsPublishTime;
        //ImageView newsPic;
        ImageView newsCover;
    }

    class ImageTask extends AsyncTask<String,Void,BitmapDrawable> {
        private String iconURL;

        @Override
        protected BitmapDrawable doInBackground(String... params) {
            iconURL=params[0];
            Bitmap bitmap=downloadIcon();
            BitmapDrawable bitmapDrawable=new BitmapDrawable(listView.getResources(),bitmap);
            //如果本地缓存没有，就缓存
            if(mImageCache.get(iconURL)==null){
                mImageCache.put(iconURL,bitmapDrawable);
            }
            return bitmapDrawable;
        }

        @Override
        protected void onPostExecute(BitmapDrawable bitmapDrawable) {
            // 通过Tag找到ImageView，如果该ImageView所在的item已被移出页面，就会直接返回null
            ImageView iv=(ImageView)listView.findViewWithTag(iconURL);
            if(iv!=null &&bitmapDrawable!=null){
                iv.setImageDrawable(bitmapDrawable);
            }
        }
        //网络请求加载图片
        private Bitmap downloadIcon() {
            HttpURLConnection con = null;
            Bitmap bitmap = null;
            try {
                URL url = new URL(iconURL);
                con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5 * 1000);
                con.setReadTimeout(10 * 1000);
                bitmap = BitmapFactory.decodeStream(con.getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }

            return bitmap;
        }

    }
}

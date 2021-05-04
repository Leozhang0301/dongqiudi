package com.example.client.ui.follows;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.client.MainActivity;
import com.example.client.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

import okhttp3.OkHttpClient;

public class FollowListAdapter extends BaseAdapter {
    private LinkedList<TeamFollowItem> linkedList;
    private Context context;
    private LruCache<String, BitmapDrawable> mImageCache;
    private ListView listView;

    public FollowListAdapter(LinkedList<TeamFollowItem> linkedList, Context context) {
        this.linkedList = linkedList;
        this.context = context;
        this.mImageCache = MainActivity.mImageCache;
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
            listView=(ListView) parent;
        }
        ViewHolder viewHolder=null;
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.followed_list_item,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.teamIcon=(ImageView)convertView.findViewById(R.id.follow_list_icon);
            viewHolder.teamName=(TextView)convertView.findViewById(R.id.follow_list_name);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        viewHolder.teamName.setText(linkedList.get(position).getTeamName());
        viewHolder.teamIcon.setTag(linkedList.get(position).getIconURL());
        if (mImageCache.get(linkedList.get(position).getIconURL())!=null){
            viewHolder.teamIcon.setImageDrawable(mImageCache.get(linkedList.get(position).getIconURL()));
        }else {
            //图片加载
            ImageTask imageTask=new ImageTask();
            imageTask.execute(linkedList.get(position).getIconURL());
        }
        return convertView;
    }

    static class ViewHolder{
        TextView teamName;
        ImageView teamIcon;
    }

    class ImageTask extends AsyncTask<String,Void,BitmapDrawable> {
        private String iconURL;

        @Override
        protected BitmapDrawable doInBackground(String... strings) {
            iconURL=strings[0];
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

        private Bitmap downloadIcon() {
            Bitmap bitmap=null;
            HttpURLConnection con=null;
            try {
                URL url=new URL(iconURL);
                con=(HttpURLConnection)url.openConnection();
                con.setConnectTimeout(5 * 1000);
                con.setReadTimeout(10 * 1000);
                bitmap = BitmapFactory.decodeStream(con.getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (con != null) {
                    con.disconnect();
                }
            }

            return bitmap;
        }
    }
}

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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.client.MainActivity;
import com.example.client.R;
import com.example.client.ui.news.CommentAdapter;
import com.example.client.ui.ranking.RankItemAdapter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

public class FollowTeamAdapter extends BaseAdapter {
    private LinkedList<TeamFollowItem> linkedList;
    private Context context;
    private LruCache<String, BitmapDrawable> mImageCache;
    private GridView gridView;

    public FollowTeamAdapter(LinkedList<TeamFollowItem> linkedList, Context context) {
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
        if (gridView==null){
            gridView=(GridView)parent;
        }
        ViewHolder viewHolder=null;
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.teams_grid_item,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.teamIcon=(ImageView)convertView.findViewById(R.id.team_icon_follow);
            viewHolder.teamName=(TextView)convertView.findViewById(R.id.team_name_follow);
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

    class ImageTask extends AsyncTask<String,Void,BitmapDrawable>{
        private String iconURL;

        @Override
        protected BitmapDrawable doInBackground(String... strings) {
            iconURL=strings[0];
            Bitmap bitmap=downloadIcon();
            BitmapDrawable bitmapDrawable=new BitmapDrawable(gridView.getResources(),bitmap);
            //如果本地缓存没有，就缓存
            if(mImageCache.get(iconURL)==null){
                mImageCache.put(iconURL,bitmapDrawable);
            }
            return bitmapDrawable;
        }

        @Override
        protected void onPostExecute(BitmapDrawable bitmapDrawable) {
            // 通过Tag找到ImageView，如果该ImageView所在的item已被移出页面，就会直接返回null
            ImageView iv=(ImageView)gridView.findViewWithTag(iconURL);
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

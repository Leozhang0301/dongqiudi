package com.example.client.ui.matches;

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

public class MatchItemAdapter extends BaseAdapter {
    private LinkedList<MatchItem> linkedList;
    private Context context;
    private ListView listView;
    private LruCache<String, BitmapDrawable> mImageCache;

    public MatchItemAdapter(LinkedList<MatchItem> linkedList, Context context) {
        this.linkedList = linkedList;
        this.context = context;
        //设置缓存大小为最大缓存的1/8
        int maxCache=(int)Runtime.getRuntime().maxMemory();
        int cacheSize=maxCache/8;
//        mImageCache=new LruCache<String, BitmapDrawable>(cacheSize){
//            //重写缓存大小计算公式
//            //以图片大小计算缓存
//            @Override
//            protected int sizeOf(String key, BitmapDrawable value) {
//                return value.getBitmap().getByteCount();
//            }
//        };
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
        ViewHolder viewHolder=null;
        if (listView==null){
            listView=(ListView)parent;
        }
        if (viewHolder==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.match_list_item,parent,false);
            viewHolder=new ViewHolder();

            viewHolder.date=(TextView)convertView.findViewById(R.id.match_date);
            viewHolder.time=(TextView)convertView.findViewById(R.id.match_time);
            viewHolder.league=(TextView)convertView.findViewById(R.id.league);
            viewHolder.homeTeam=(TextView)convertView.findViewById(R.id.home_team);
            viewHolder.result=(TextView)convertView.findViewById(R.id.result);
            viewHolder.awayTeam=(TextView)convertView.findViewById(R.id.away_team);
            viewHolder.homeTeamIcon=(ImageView)convertView.findViewById(R.id.home_team_icon);
            viewHolder.awayTeamIcon=(ImageView)convertView.findViewById(R.id.away_team_icon);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        viewHolder.date.setText(linkedList.get(position).getDate());
        viewHolder.time.setText(linkedList.get(position).getTime());
        viewHolder.league.setText(linkedList.get(position).getLeague());
        viewHolder.homeTeam.setText(linkedList.get(position).getHomeTeam());
        viewHolder.result.setText(linkedList.get(position).getResult());
        viewHolder.awayTeam.setText(linkedList.get(position).getAwayTeam());
        viewHolder.homeTeamIcon.setTag(linkedList.get(position).getHomeTeamIconURL());
        viewHolder.awayTeamIcon.setTag(linkedList.get(position).getAwayTeamIconURL());
        //如果本地已有缓存，就从本地获取，没有就网络请求
        if(mImageCache.get(linkedList.get(position).getHomeTeamIconURL())!=null){
            viewHolder.homeTeamIcon.setImageDrawable(mImageCache.get(linkedList.get(position).getHomeTeamIconURL()));
        }
        if(mImageCache.get(linkedList.get(position).getAwayTeamIconURL())!=null){
            viewHolder.awayTeamIcon.setImageDrawable(mImageCache.get(linkedList.get(position).getAwayTeamIconURL()));
        }
        if(mImageCache.get(linkedList.get(position).getHomeTeamIconURL())!=null&&mImageCache.get(linkedList.get(position).getAwayTeamIconURL())!=null) {
            ImageTask imageTask=new ImageTask();
            String[] icons={linkedList.get(position).getHomeTeamIconURL(),linkedList.get(position).getAwayTeamIconURL()};
            imageTask.execute(icons);
        }
        return convertView;
    }

    static class ViewHolder{
        TextView date;
        TextView time;
        TextView league;
        TextView homeTeam;
        TextView result;
        TextView awayTeam;
        ImageView homeTeamIcon;
        ImageView awayTeamIcon;
    }

    class ImageTask extends AsyncTask<String,Void,BitmapDrawable> {
        private String iconURL;

        @Override
        protected BitmapDrawable doInBackground(String... strings) {

            return null;
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

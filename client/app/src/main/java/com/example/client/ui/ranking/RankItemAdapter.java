package com.example.client.ui.ranking;

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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

public class RankItemAdapter extends BaseAdapter {
    private LinkedList<RankItem> linkedList;
    private Context context;
    private LruCache<String, BitmapDrawable> mImageCache;
    private ListView listView;

    public RankItemAdapter(LinkedList<RankItem> linkedList, Context context) {
        this.linkedList = linkedList;
        this.context = context;
//        //设置缓存大小为最大缓存的1/8
//        int maxCache=(int)Runtime.getRuntime().maxMemory();
//        int cacheSize=maxCache/8;
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
        if (listView==null){
            listView=(ListView)parent;
        }
        ViewHolder viewHolder=null;
        if (convertView ==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.rank_list_item,parent,false);
            viewHolder=new ViewHolder();
            //绑定
            viewHolder.paiming=(TextView)convertView.findViewById(R.id.paiming);
            viewHolder.team_icon=(ImageView)convertView.findViewById(R.id.team_icon);
            viewHolder.qiudui=(TextView)convertView.findViewById(R.id.qiudui);
            viewHolder.changci=(TextView)convertView.findViewById(R.id.changci);
            viewHolder.sheng=(TextView)convertView.findViewById(R.id.sheng);
            viewHolder.ping=(TextView)convertView.findViewById(R.id.ping);
            viewHolder.fu=(TextView)convertView.findViewById(R.id.fu);
            viewHolder.jinqiu=(TextView)convertView.findViewById(R.id.jinqiu);
            viewHolder.shiqiu=(TextView)convertView.findViewById(R.id.shiqiu);
            viewHolder.jifen=(TextView)convertView.findViewById(R.id.jifen);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder) convertView.getTag();
        }
        viewHolder.paiming.setText(linkedList.get(position).getPaiming());
        viewHolder.qiudui.setText(linkedList.get(position).getQiudui());
        viewHolder.changci.setText(linkedList.get(position).getChangci());
        viewHolder.sheng.setText(linkedList.get(position).getSheng());
        viewHolder.ping.setText(linkedList.get(position).getPing());
        viewHolder.fu.setText(linkedList.get(position).getFu());
        viewHolder.jinqiu.setText(linkedList.get(position).getJinqiu());
        viewHolder.shiqiu.setText(linkedList.get(position).getShiqiu());
        viewHolder.jifen.setText(linkedList.get(position).getJifen());
        //队标
        viewHolder.team_icon.setTag(linkedList.get(position).getGetIconURL());
        //如果本地已有缓存，就从本地获取，没有就网络请求
        if(mImageCache.get(linkedList.get(position).getGetIconURL())!=null){
            viewHolder.team_icon.setImageDrawable(mImageCache.get(linkedList.get(position).getGetIconURL()));
        }else {
            ImageTask imageTask=new ImageTask();
            imageTask.execute(linkedList.get(position).getGetIconURL());
        }
        return convertView;
    }
    static class ViewHolder{
        TextView paiming;
        ImageView team_icon;
        TextView qiudui;
        TextView changci;
        TextView sheng;
        TextView ping;
        TextView fu;
        TextView jinqiu;
        TextView shiqiu;
        TextView jifen;
    }

     class ImageTask extends AsyncTask<String,Void,BitmapDrawable>{
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



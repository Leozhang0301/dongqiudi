package com.example.client.ui.news;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.client.R;

import java.util.LinkedList;

public class NewsItemAdapter extends BaseAdapter {
    private LinkedList<NewsItem> linkedList;
    private Context context;

    public NewsItemAdapter(LinkedList<NewsItem> linkedList, Context context) {
        this.linkedList = linkedList;
        this.context = context;
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
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.news_list_item,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.newsTitle=(TextView)convertView.findViewById(R.id.news_title);
            viewHolder.newsPublishTime=(TextView)convertView.findViewById(R.id.news_publish_time);
            viewHolder.newsPic=(ImageView) convertView.findViewById(R.id.news_first_pic);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder=(ViewHolder) convertView.getTag();
        }
        viewHolder.newsTitle.setText(linkedList.get(position).getTitle());
        viewHolder.newsPublishTime.setText(linkedList.get(position).getPublishTime());
        viewHolder.newsPic.setImageResource(linkedList.get(position).getCoverID());
        return convertView;
    }

    public static class ViewHolder{
        TextView newsTitle;
        TextView newsPublishTime;
        ImageView newsPic;
    }
}

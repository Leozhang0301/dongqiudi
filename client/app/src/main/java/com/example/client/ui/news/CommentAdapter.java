package com.example.client.ui.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.client.R;

import java.util.LinkedList;

public class CommentAdapter extends BaseAdapter {
    private LinkedList<Comment> linkedList;
    private Context context;

    public CommentAdapter(LinkedList<Comment> linkedList, Context context) {
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
            convertView= LayoutInflater.from(context).inflate(R.layout.comment_item,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.name=(TextView)convertView.findViewById(R.id.comment_name);
            viewHolder.time=(TextView)convertView.findViewById(R.id.comment_time);
            viewHolder.content=(TextView)convertView.findViewById(R.id.comment_content);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        viewHolder.name.setText(linkedList.get(position).getName());
        viewHolder.time.setText(linkedList.get(position).getTime());
        viewHolder.content.setText(linkedList.get(position).getContent());
        return convertView;
    }

    public static class ViewHolder{
        TextView name;
        TextView time;
        TextView content;
    }
}

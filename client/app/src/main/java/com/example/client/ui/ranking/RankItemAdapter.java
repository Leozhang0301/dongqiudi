package com.example.client.ui.ranking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.client.R;

import java.util.LinkedList;

public class RankItemAdapter extends BaseAdapter {
    private LinkedList<RankItem> linkedList;
    private Context context;

    public RankItemAdapter(LinkedList<RankItem> linkedList, Context context) {
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
        if (convertView ==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.rank_list_item,parent,false);
            viewHolder=new ViewHolder();
            //绑定
            viewHolder.paiming=(TextView)convertView.findViewById(R.id.paiming);
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
        return convertView;
    }
    static class ViewHolder{
        TextView paiming;
        TextView qiudui;
        TextView changci;
        TextView sheng;
        TextView ping;
        TextView fu;
        TextView jinqiu;
        TextView shiqiu;
        TextView jifen;
    }
}



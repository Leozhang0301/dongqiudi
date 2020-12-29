package com.example.client.ui.matches;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.client.R;

import java.util.LinkedList;

public class MatchItemAdapter extends BaseAdapter {
    private LinkedList<MatchItem> linkedList;
    private Context context;

    public MatchItemAdapter(LinkedList<MatchItem> linkedList, Context context) {
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
        if (viewHolder==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.match_list_item,parent,false);
            viewHolder=new ViewHolder();

            viewHolder.date=(TextView)convertView.findViewById(R.id.match_date);
            viewHolder.time=(TextView)convertView.findViewById(R.id.match_time);
            viewHolder.league=(TextView)convertView.findViewById(R.id.league);
            viewHolder.homeTeam=(TextView)convertView.findViewById(R.id.home_team);
            viewHolder.result=(TextView)convertView.findViewById(R.id.result);
            viewHolder.awayTeam=(TextView)convertView.findViewById(R.id.away_team);
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

        return convertView;
    }

    static class ViewHolder{
        TextView date;
        TextView time;
        TextView league;
        TextView homeTeam;
        TextView result;
        TextView awayTeam;
    }
}

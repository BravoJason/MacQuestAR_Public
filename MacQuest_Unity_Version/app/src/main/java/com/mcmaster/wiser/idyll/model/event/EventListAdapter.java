package com.mcmaster.wiser.idyll.model.event;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mcmaster.wiser.idyll.R;

import java.util.ArrayList;

public class EventListAdapter extends ArrayAdapter<EventItem> {

    private int resourceId;
    private ArrayList<EventItem> objects;
    private Context context;
    public EventListAdapter(@NonNull Context context, int resource, ArrayList<EventItem> events) {
        super(context, resource);

        this.objects = events;
        this.resourceId = resource;
        this.context = context;
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return objects.size();
    }

    @Override
    public EventItem getItem(int position) {
        // TODO Auto-generated method stub
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    private static class ViewHolder
    {
        TextView textContent;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        ViewHolder viewHolder = null;
        if(convertView == null)
        {
            viewHolder=new ViewHolder();
            LayoutInflater mInflater=LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.list_item_1, null);
            viewHolder.textContent = (TextView) convertView.findViewById(R.id.text1);

            convertView.setTag(viewHolder);
        }else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        EventItem event = objects.get(position);

        if(event != null){
            viewHolder.textContent.setText(event.getEventName());
            if(event.getIsVisit()){
                viewHolder.textContent.setTextColor(context.getResources().getColor(R.color.visitedEvent));
            }else{
                viewHolder.textContent.setTextColor(context.getResources().getColor(R.color.black));
            }

            if(!event.getIsClick()){
                //viewHolder.textContent.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                if(position % 2 == 0){
                    viewHolder.textContent.setBackgroundColor(context.getResources().getColor(R.color.eventItem_0));
                }else{
                    viewHolder.textContent.setBackgroundColor(context.getResources().getColor(R.color.eventItem_1));
                }
            }else
            {
                viewHolder.textContent.setBackgroundColor(context.getResources().getColor(R.color.colorGreen));
            }

        }

        return convertView;

    }
}

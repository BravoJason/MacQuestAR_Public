package com.mcmaster.wiser.idyll.presenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mcmaster.wiser.idyll.R;
import com.mcmaster.wiser.idyll.model.bus.BusStop;
import com.mcmaster.wiser.idyll.presenter.ItemClickListener;

import java.util.ArrayList;

/**
 * Custom adapter class for BusStop cards
 * Created by Eric on 6/26/17.
 */

public class BusStopAdapter extends RecyclerView.Adapter<BusStopAdapter.MyViewHolder>{

    private final static String TAG = BusStopAdapter.class.getSimpleName();

    Context c;

    private ArrayList<BusStop> dataSet;

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView routeShortName, routeLongName, stopName, nextBusArrival;

        ItemClickListener itemClickListener;

        public MyViewHolder(View itemView) {
            super(itemView);

            this.routeShortName = (TextView) itemView.findViewById(R.id.route_short_name);
            this.routeLongName = (TextView) itemView.findViewById(R.id.route_long_name);
            this.stopName = (TextView) itemView.findViewById(R.id.stop_name);
            this.nextBusArrival = (TextView) itemView.findViewById(R.id.next_bus_time);

            itemView.setOnClickListener(this);

        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClick(this.getLayoutPosition());
        }
    }

    public BusStopAdapter(Context c, ArrayList<BusStop> data) {
        this.c = c;
        this.dataSet = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bus_stop_item, parent, false);

//        final MyViewHolder myViewHolder = new MyViewHolder(view);

        //TODO: change the card view here with an onclick listener

        return new BusStopAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TextView stopName1 = holder.stopName;
        TextView routeShortName1 = holder.routeShortName;
        TextView routeLongName1 = holder.routeLongName;
        TextView nextBusArrival1 = holder.nextBusArrival;

        stopName1.setText(dataSet.get(position).getStopName());
        routeShortName1.setText(dataSet.get(position).getRouteShortName());
        routeLongName1.setText(dataSet.get(position).getRouteLongName());
        nextBusArrival1.setText(dataSet.get(position).getNextBusArrival());

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}

package com.mcmaster.wiser.idyll.presenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcmaster.wiser.idyll.R;
import com.mcmaster.wiser.idyll.model.event.EventUtils;
import com.mcmaster.wiser.idyll.model.event.Event;
import com.mcmaster.wiser.idyll.presenter.ItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Daniel
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {

    private final static String TAG = EventAdapter.class.getSimpleName();

    Context c;

    private ArrayList<Event> dataSet;

    private OnItemClickListener listener;

    private RecyclerView mSearchResultsList;

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name, description, status;
        ImageView imageView;

        Button viewEventButton;

        ItemClickListener itemClickListener;


        public MyViewHolder(final View itemView) {
            super(itemView);
//            this.expandable = (RelativeLayout) itemView.findViewById(R.id.expandableLayout);
            this.name = (TextView) itemView.findViewById(R.id.event_name);
            this.description = (TextView) itemView.findViewById(R.id.event_description);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
            this.viewEventButton = (Button) itemView.findViewById(R.id.view_event_button);
            this.status = (TextView) itemView.findViewById(R.id.event_status);
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener == null)
                return;
            this.itemClickListener.onItemClick(this.getLayoutPosition());
        }
    }

    public EventAdapter(Context c, ArrayList<Event> data, RecyclerView mSearchResultsList) {
        this.c = c;
        this.dataSet = data;
        this.mSearchResultsList = mSearchResultsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);


        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        TextView name1 = holder.name;
        TextView shortName1 = holder.description;
        TextView status1 = holder.status;

        ImageView testImage = holder.imageView;

        name1.setText(dataSet.get(position).getEventName());
        shortName1.setText(dataSet.get(position).getDescription());
        if (dataSet.get(position).getPublicAttribute().equals(EventUtils.EVENT_ATTR_PUBLIC)){
            status1.setText("Public");
        }
        else if (dataSet.get(position).getPublicAttribute().equals(EventUtils.EVENT_ATTR_PRIVATE)){
            status1.setText("Private: Password Required");
        }
        else{
            status1.setText("");
        }

        String rawId = dataSet.get(position).getId();

        try {
            String imageSource = "i" + rawId;
            int id = c.getResources().getIdentifier(imageSource, "drawable", c.getPackageName());
//            Picasso.with(c).load(R.mipmap.ic_launcher_round).placeholder(R.mipmap.ic_launcher_round).into(testImage);
            Picasso.with(c).load(R.mipmap.ic_launcher_round).placeholder(R.mipmap.ic_launcher_round).fit().into(testImage);
        } catch (Exception e) {
            Picasso.with(c).load(R.mipmap.ic_launcher_round).placeholder(R.mipmap.ic_launcher_round).into(testImage);
        }

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                final String id = dataSet.get(pos).getId();
                final String pAttribute = dataSet.get(pos).getPublicAttribute();
                final String password = dataSet.get(pos).getPassword();
                Button viewOnMapButton = (Button) holder.itemView.findViewById(R.id.view_event_button);
                //switchVisibility(viewOnMapButton);

                if (listener != null) {

                    listener.onItemClick(id, pAttribute, password);
                }
                /*viewOnMapButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                if(pos + 1 == dataSet.size()){

                    //mSearchResultsList.smoothScrollToPosition(pos);
                    //mSearchResultsList.scrollToPosition(pos-1);
                    mSearchResultsList.smoothScrollToPosition(pos);
                    Log.d("Can Scroll", String.valueOf(mSearchResultsList.computeHorizontalScrollExtent()));
                }*/
            }
        });
    }

    private void switchVisibility(View view) {
        if (view.getVisibility() == View.GONE) {
            view.setVisibility(View.VISIBLE);
        } else if (view.getVerticalFadingEdgeLength() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        }
    }


    public void swapData(ArrayList<Event> mNewDataSet) {
        dataSet = mNewDataSet;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        public void onItemClick(String id, String pAttribute, String password);
    }
}

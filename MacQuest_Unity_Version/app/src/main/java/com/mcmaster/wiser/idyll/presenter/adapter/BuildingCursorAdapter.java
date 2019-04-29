package com.mcmaster.wiser.idyll.presenter.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcmaster.wiser.idyll.R;
import com.mcmaster.wiser.idyll.model.Contracts;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wiserlab on 7/19/17.
 */

public class BuildingCursorAdapter extends RecyclerViewCursorAdapter<BuildingCursorAdapter.BuildingViewHolder>
implements View.OnClickListener{

    private final static String TAG = BuildingCursorAdapter.class.getSimpleName();

    private final LayoutInflater layoutInflater;

    private OnItemClickListener onItemClickListener;

    Context context;

    public BuildingCursorAdapter(final Context context){
        super();
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public BuildingCursorAdapter.BuildingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = this.layoutInflater.inflate(R.layout.building_item, parent, false);
        view.setOnClickListener(this);
        return new BuildingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BuildingCursorAdapter.BuildingViewHolder holder, final Cursor cursor) {
        holder.bindData(cursor, context);
    }

    @Override
    public void onClick(final View v) {
        if (this.onItemClickListener != null){
            final RecyclerView recyclerView = (RecyclerView) v.getParent();
            final int position =  recyclerView.getChildLayoutPosition(v);
            if (position != RecyclerView.NO_POSITION){
                final Cursor cursor = this.getItem(position);
                this.onItemClickListener.onItemClicked(cursor);
            }
        }

    }

    public static class BuildingViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.building_name)
        TextView buildingName;

        @BindView(R.id.building_shortname)
        TextView buildingShortName;

        @BindView(R.id.imageView)
        ImageView buildingImage;


        public BuildingViewHolder(final View itemView){
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bindData(final Cursor cursor, Context context){
            final String name = cursor.getString(cursor.getColumnIndex(Contracts.BuildingContractEntry.COLUMN_NAME));
            final String shortName = cursor.getString(cursor.getColumnIndex(Contracts.BuildingContractEntry.COLUMN_SHORTNAME));
            final String rawID = cursor.getString(cursor.getColumnIndex(Contracts.BuildingContractEntry.COLUMN_OUT_ID));

            this.buildingName.setText(name);
            this.buildingShortName.setText(shortName);

            try {
                String imageSource = "i" + rawID;
                int id = context.getResources().getIdentifier(imageSource, "drawable", context.getPackageName());
                Picasso.with(context).load(id).placeholder(R.drawable.logo).error(R.drawable.logo).into(buildingImage);
            } catch (Exception e ){
                Picasso.with(context).load(R.drawable.logo).placeholder(R.drawable.logo).into(buildingImage);
                return;
            }

        }
    }

    public interface OnItemClickListener{
        void onItemClicked(Cursor cursor);
    }
}

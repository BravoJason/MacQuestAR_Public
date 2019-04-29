package com.mcmaster.wiser.idyll.presenter.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mcmaster.wiser.idyll.R;
import com.mcmaster.wiser.idyll.connection.MySingleton;
import com.mcmaster.wiser.idyll.connection.ServerUtils;
import com.mcmaster.wiser.idyll.model.Contracts;
import com.mcmaster.wiser.idyll.model.building.Building;
import com.mcmaster.wiser.idyll.model.building.history.BuildingHistoryDbHelper;
import com.mcmaster.wiser.idyll.presenter.ItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to populate the RecyclerView with building information
 * Created by Eric on 6/13/17.
 */

public class BuildingAdapter extends RecyclerView.Adapter<BuildingAdapter.MyViewHolder> {

    private final static String TAG = BuildingAdapter.class.getSimpleName();

    Context c;

    private ArrayList<Building> dataSet;

    private OnItemClickListener listener;

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name, shortName;
        ImageView buildingImage;

        Button seeOnMapButton;

        ItemClickListener itemClickListener;


        public MyViewHolder(final View itemView) {
            super(itemView);
//            this.expandable = (RelativeLayout) itemView.findViewById(R.id.expandableLayout);
            this.name = (TextView) itemView.findViewById(R.id.building_name);
            this.shortName = (TextView) itemView.findViewById(R.id.building_shortname);
            this.buildingImage = (ImageView) itemView.findViewById(R.id.imageView);
            this.seeOnMapButton = (Button) itemView.findViewById(R.id.view_on_map_button);
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

    public BuildingAdapter(Context c, ArrayList<Building> data) {
        this.c = c;
        this.dataSet = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.building_item, parent, false);

//        final MyViewHolder myViewHolder = new MyViewHolder(view);

        //TODO: change the card view here with an onclick listener

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        TextView name1 = holder.name;
        TextView shortName1 = holder.shortName;

        ImageView testImage = holder.buildingImage;

        name1.setText(dataSet.get(position).getName());
        shortName1.setText(dataSet.get(position).getShortName());
        String rawId = dataSet.get(position).getIdNum();

        try {
            String imageSource = "i" + rawId;
            int id = c.getResources().getIdentifier(imageSource, "drawable", c.getPackageName());
//            Picasso.with(c).load(R.mipmap.ic_launcher_round).placeholder(R.mipmap.ic_launcher_round).into(testImage);
            Picasso.with(c).load(id).placeholder(R.mipmap.ic_launcher_round).fit().into(testImage);
        } catch (Exception e) {
            Picasso.with(c).load(R.mipmap.ic_launcher_round).placeholder(R.mipmap.ic_launcher_round).into(testImage);
            return;
        }

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                int xyz = 0;
                final String name = dataSet.get(pos).getName();
                final String shortName = dataSet.get(pos).getShortName();
                final String outid = dataSet.get(pos).getIdNum();
                final String rawLocation = dataSet.get(pos).getLocation();
                // missing .. inserted today 8/21/2017
                final String id = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID);
                // check for internet connection.
                ConnectivityManager manager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = manager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isAvailable()) {

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerUtils.API_HISTORY_DB, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            BuildingHistoryDbHelper mDbHelper = new BuildingHistoryDbHelper(c);
                            SQLiteDatabase db = mDbHelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put(Contracts.BuildingHistoryEntry.COLUMN_BUILDING_NAME, name);
                            values.put(Contracts.BuildingHistoryEntry.COLUMN_BUILDING_SHORTNAME, shortName);
                            values.put(Contracts.BuildingHistoryEntry.COLUMN_OUT_ID, outid);
                            values.put(Contracts.BuildingHistoryEntry.COLUMN_LOCATION, rawLocation);
                            values.put(Contracts.BuildingHistoryEntry.COLUMN_SYNC_STATUS, BuildingHistoryDbHelper.SYNC_STATUS_OK);
                            Log.v(TAG, "Internet-Connected");

                            long newRowId = db.insert(Contracts.BuildingHistoryEntry.TABLE_NAME, null, values);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            BuildingHistoryDbHelper mDbHelper = new BuildingHistoryDbHelper(c);
                            SQLiteDatabase db = mDbHelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put(Contracts.BuildingHistoryEntry.COLUMN_BUILDING_NAME, name);
                            values.put(Contracts.BuildingHistoryEntry.COLUMN_BUILDING_SHORTNAME, shortName);
                            values.put(Contracts.BuildingHistoryEntry.COLUMN_OUT_ID, outid);
                            values.put(Contracts.BuildingHistoryEntry.COLUMN_LOCATION, rawLocation);
                            values.put(Contracts.BuildingHistoryEntry.COLUMN_SYNC_STATUS, BuildingHistoryDbHelper.SYNC_STATUS_FAILED);
                            db.insert(Contracts.BuildingHistoryEntry.TABLE_NAME, null, values);
                            Log.v(TAG, "Error-Connecting-to-server");
                        }
                    }) {
                        protected Map<String, String> getParams() throws AuthFailureError {

                            Map<String, String> params = new HashMap<>();
                            params.put("name", name);
                            params.put("shortname", shortName);
                            params.put("location", rawLocation);
                            params.put("outid", outid);
                            params.put("uuid", id);
                            return params;

                        }

                    };
                    MySingleton.getInstance(c).addToRequestQue(stringRequest);

                } else {
                    BuildingHistoryDbHelper mDbHelper = new BuildingHistoryDbHelper(c);
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(Contracts.BuildingHistoryEntry.COLUMN_BUILDING_NAME, name);
                    values.put(Contracts.BuildingHistoryEntry.COLUMN_BUILDING_SHORTNAME, shortName);
                    values.put(Contracts.BuildingHistoryEntry.COLUMN_OUT_ID, outid);
                    values.put(Contracts.BuildingHistoryEntry.COLUMN_LOCATION, rawLocation);
                    values.put(Contracts.BuildingHistoryEntry.COLUMN_SYNC_STATUS, BuildingHistoryDbHelper.SYNC_STATUS_FAILED);
                    Log.v(TAG, "Internet-not-Connected");
                    //  newRowId
                    db.insert(Contracts.BuildingHistoryEntry.TABLE_NAME, null, values);
                    xyz = 1;

                }
                // Arooj code ends here.

                if (xyz == -1) { // If the row ID is -1, then there was an error with insertion.
                    Toast.makeText(c, "Error with saving", Toast.LENGTH_SHORT).show();
                } else { // Otherwise, the insertion was successful
//                    Toast.makeText(c, "History saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
                }

                Button viewOnMapButton = (Button) holder.itemView.findViewById(R.id.view_on_map_button);
                switchVisibility(viewOnMapButton);
                viewOnMapButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onItemClick(name, rawLocation);
                        }
                    }
                });
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


    public void swapData(ArrayList<Building> mNewDataSet) {
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
        public void onItemClick(String textName, String textViewBrief);
    }
}

package com.mcmaster.wiser.idyll.view;

import com.mcmaster.wiser.idyll.model.BusDbHelper;
import com.mcmaster.wiser.idyll.view.BaseFragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.mcmaster.wiser.idyll.R;
import com.mcmaster.wiser.idyll.model.BusDbHelper;
import com.mcmaster.wiser.idyll.model.bus.BusStop;
import com.mcmaster.wiser.idyll.presenter.adapter.BusStopAdapter;
import com.mcmaster.wiser.idyll.presenter.util.BusStopDataUtils;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mcmaster.wiser.idyll.view.MainActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BusFragment extends BaseFragment {

    private final String TAG = BusFragment.class.getSimpleName();

    BusDbHelper db;

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    Context context;
    @BindView(R.id.bus_show_on_map_button)
    Button busShowOnMapButton;

    @OnClick(R.id.bus_show_on_map_button)
    public void busShowMap() {
        String selectedStop = (String) spinner.getSelectedItem();
        LatLng stopLocation = BusStopDataUtils.getLocationOfStop(selectedStop, getContext());

        ((MainActivity) getActivity()).navigationView.getMenu().getItem(0).setChecked(true);

        ((MainActivity) getActivity()).setBusStopLatLng(stopLocation);
        getFragmentManager()
                .popBackStack(MapFragment.class.getSimpleName(), 0);
    }

    @BindView(R.id.recycler_cardview)
    CardView recyclerCardview;

    @BindView(R.id.bus_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.bus_spinner)
    Spinner spinner;

    @BindView(R.id.bus_button)
    Button button;

    @OnClick(R.id.bus_button)
    void onBusButtonClick() {
        String selectedStop = (String) spinner.getSelectedItem();

        new BusAsyncTask().execute(selectedStop);

        showRecyclerCardView();

    }

    private void showRecyclerCardView() {
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.enter_from_bottom);
        anim.setDuration(250);
        recyclerCardview.startAnimation(anim);
        recyclerCardview.setVisibility(View.VISIBLE);
    }

    public BusFragment() {
    } // Required empty public constructor

    //When the cardview closes, reset the sidebar to highlight the right option.
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) getActivity()).resetSidebarHighlight();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bus, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context=getContext();

        new StopNamesAsyncTask().execute("STRING");
        SetupRecyclerView();
//        SetupDataBase();
        recyclerCardview.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onActivityBackPress() {
        return false;
    }



    private void SetupDataBase() {
        db = new BusDbHelper(getContext());
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                R.layout.support_simple_spinner_dropdown_item,
                BusStopDataUtils.CreateSpinnerSuggestionsList(getContext()));

        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    private void SetupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private class BusAsyncTask extends AsyncTask<String, Void, ArrayList<BusStop>> {

        @Override
        protected ArrayList<BusStop> doInBackground(String... params) {
            String selectedStop = params[0];
            ArrayList<BusStop> busStopArrayList = BusStopDataUtils.fetchData(selectedStop, getContext());
            return busStopArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<BusStop> busStops) {
            super.onPostExecute(busStops);
            RecyclerView.Adapter myAdapter = new BusStopAdapter(getContext(), busStops);
            recyclerView.setAdapter(myAdapter);
        }
    }

    private class StopNamesAsyncTask extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            return BusStopDataUtils.CreateSpinnerSuggestionsList(getContext());

        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            if(context==null){
                context=getContext();
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context,
                    R.layout.support_simple_spinner_dropdown_item, strings);

            dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

            spinner.setAdapter(dataAdapter);
        }
    }
}

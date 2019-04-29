package com.mcmaster.wiser.idyll.view;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.mcmaster.wiser.idyll.R;
import com.mcmaster.wiser.idyll.model.Contracts;
import com.mcmaster.wiser.idyll.model.building.Building;
import com.mcmaster.wiser.idyll.presenter.util.BuildingDataUtils;
import com.mcmaster.wiser.idyll.presenter.adapter.BuildingAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragment for showing building directory and lookup
 * Created by Eric
 */

public class BuildingDirectoryFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = BuildingDirectoryFragment.class.getSimpleName();
    private static final int LOADER_SEARCH_RESULTS = 1;

    @BindView(R.id.search_results_list)
    RecyclerView mSearchResultsList;

    @BindView(R.id.floating_search_view)
    FloatingSearchView mSearchView;

    private BuildingAdapter adapter;


    ArrayList<Building> tempList;
    ArrayList<Building> buildingData;

    public BuildingDirectoryFragment() {
    } // Required empty public constructor


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_building_directory, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tempList = new ArrayList<>(); // temporary, blank array list to display while the database is loading
        setupFloatingSearch();
        setupResultsList();
        setupDrawer();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public boolean onActivityBackPress() {
        //if mSearchView.setSearchFocused(false) causes the focused search
        //to close, then we don't want to close the activity. if mSearchView.setSearchFocused(false)
        //returns false, we know that the search was already closed so the call didn't change the focus
        //state and it makes sense to call supper onBackPressed() and close the activity
        if (!mSearchView.setSearchFocused(false)) {
            return false;
        }
        return true;
    }

    //When the cardview closes, reset the sidebar to highlight the right option.
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) getActivity()).resetSidebarHighlight();
    }

    private void setupDrawer() {
        attachSearchViewActivityDrawer(mSearchView);
    }

    private void setupResultsList() {

//        adapter = new BuildingAdapter(getContext(), BuildingDataUtils.fetchData(getContext())); //must bring in a context and an arraylist of buildings
        adapter = new BuildingAdapter(getContext(), tempList);
//        mCursorAdapter = new BuildingCursorAdapter(getContext());
        this.getLoaderManager().restartLoader(LOADER_SEARCH_RESULTS,null,this);
        mSearchResultsList.setAdapter(adapter);
        mSearchResultsList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setOnItemClickListener(new BuildingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String textName, String textViewBrief) {

//                getActivity().getSupportFragmentManager().beginTransaction().remove(getActivity().getSupportFragmentManager().getFragments().get(
//                        getActivity().getSupportFragmentManager().getFragments().size()-1
//                )).commit();

                ((MainActivity) getActivity()).navigationView.getMenu().getItem(0).setChecked(true);
                ((MainActivity) getActivity()).setRawLocation(textViewBrief);
                onDestroy();

                getFragmentManager()
                        .popBackStack(MapFragment.class.getSimpleName(), 0);

            }
        });
    }

    private void setupFloatingSearch() {
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                } else {
                    mSearchView.showProgress();
                    BuildingDataUtils.findBuildingSuggestions( buildingData, getContext(), newQuery, 5, new BuildingDataUtils.OnFindBuildingSuggestionsListener() {
                        @Override
                        public void onResults(ArrayList<Building> results) {
                            if (results.size() == 0){
                                Building emptyEvent = new Building("No Building Found");
                                results.add(emptyEvent);
                            }

                            mSearchView.swapSuggestions(results);
                            mSearchView.hideProgress();
                            adapter.swapData(results);


                        }
                    });


                }


            }


        });





        mSearchView.setOnSuggestionsListHeightChanged(new FloatingSearchView.OnSuggestionsListHeightChanged() {
            @Override
            public void onSuggestionsListHeightChanged(float newHeight) {
                mSearchResultsList.setTranslationY(newHeight);
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                ArrayList<Building> selectedBuilding = new ArrayList<>();
                Building b = (Building) searchSuggestion;
                selectedBuilding.add(b);
                adapter.swapData(selectedBuilding);
            }

            @Override
            public void onSearchAction(String currentQuery) {

            }
        });

        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                mSearchView.swapSuggestions(BuildingDataUtils.fetchHistory(getContext(), 3));
            }

            @Override
            public void onFocusCleared() {
                mSearchView.clearSuggestions();
            }
        });

        mSearchView.setOnClearSearchActionListener(new FloatingSearchView.OnClearSearchActionListener() {
            @Override
            public void onClearSearchClicked() {
                adapter.swapData(buildingData);
            }
        });

        mSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon, TextView textView, SearchSuggestion item, int itemPosition) {
                Building building = (Building) item;

                if (building.getIsHistory()) {
                    leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.ic_history_black_24dp, null));
                    leftIcon.setAlpha(.36f);
                } else {
                    String temp = building.getBody();
                    if (temp.toLowerCase().contains("parking")) {
                        leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.ic_directions_car_black_24dp, null));
                    } else if (temp.toLowerCase().contains("library")) {
                        leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.ic_local_library_black_24dp, null));
                    } else {
                        leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.ic_business_black_24dp, null));
                    }
                    leftIcon.setAlpha(.36f);
                }
                String text = building.getBody();
                textView.setText(text);
            }
        });

        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.delete_history_item) {
                    BuildingDataUtils.deleteHistory(getContext());
                }
            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case LOADER_SEARCH_RESULTS:
                String[] projection = {
                        Contracts.BuildingContractEntry.COLUMN_OUT_ID,
                        Contracts.BuildingContractEntry.COLUMN_NAME,
                        Contracts.BuildingContractEntry.COLUMN_SHORTNAME,
                        Contracts.BuildingContractEntry.COLUMN_LOCATION
                };
                return new CursorLoader(getContext(), Contracts.BuildingContractEntry.CONTENT_URI , projection, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()){
            case LOADER_SEARCH_RESULTS:
                buildingData = BuildingDataUtils.getBuildingsFromCursor(data);
                adapter.swapData(buildingData);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()){
            case LOADER_SEARCH_RESULTS:
                break;
        }
    }
}

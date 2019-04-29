package com.mcmaster.wiser.idyll.view;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.mcmaster.wiser.idyll.R;
import com.mcmaster.wiser.idyll.model.event.EventUtils;
import com.mcmaster.wiser.idyll.model.event.Event;
import com.mcmaster.wiser.idyll.presenter.ParentEventIDReceiver;
import com.mcmaster.wiser.idyll.presenter.adapter.EventAdapter;
import com.mcmaster.wiser.idyll.presenter.util.MapUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragment for showing events. What pops up when you click View event points on the sidebar
 * Created by Daniel
 */


public class EventListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = EventListFragment.class.getSimpleName();
    private static final int LOADER_SEARCH_RESULTS = 1;


    @BindView(R.id.search_results_list)
    RecyclerView mSearchResultsList;

    @BindView(R.id.floating_search_view)
    FloatingSearchView mSearchView;

    private EventAdapter adapter;


    ArrayList<Event> tempList;
    ArrayList<Event> eData;
    public ArrayList<ArrayList<String>> eventData = new ArrayList<>();
    private boolean showCurrentandFuture = true;

    ParentEventIDReceiver receiver;

    public EventListFragment() {
    } // Required empty public constructor

    public void setReceiver(ParentEventIDReceiver receiver) {
        this.receiver = receiver;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);
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
        ((MainActivity) getActivity()).drawARPoints();
        ((MainActivity) getActivity()).resetSidebarHighlight();
        //TODO: ADD SHOWING LOCATION TOGGLE / FLOOR SELECT

        ((MainActivity) getActivity()).resetFabMenu();
    }

    private void setupDrawer() {
        attachSearchViewActivityDrawer(mSearchView);
    }

    private void setupResultsList() {

        //What happens when you click the actual show on map button.

        eventData = MapUtils.arEvents;
        eData = new ArrayList<>();

        for (ArrayList<String> event : eventData) {
            eData.add(new Event(event));
        }

        adapter = new EventAdapter(getContext(), eData, mSearchResultsList);
        this.getLoaderManager().restartLoader(LOADER_SEARCH_RESULTS, null, this);
        mSearchResultsList.setAdapter(adapter);
        mSearchResultsList.setLayoutManager(new LinearLayoutManager(getContext()));


        adapter.setOnItemClickListener(new EventAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final String id, final String publicAttribute, final String password) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder = builder.setTitle("Join Event").setMessage("Do you want to join this event?");
                AlertDialog dialog = builder.setPositiveButton("Join",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showEvent(id, publicAttribute, password, true);
                            }
                        }).setNegativeButton("View Points",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showEvent(id, publicAttribute, password, false);
                            }
                        }).setNeutralButton("Go back",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.colorPrimary));





                /*

                 */
            }
        });

    }


    //Function to show sub event.
    private void showEvent(final String id, String publicAttribute, final String password, final boolean isJoin) {


        if (publicAttribute.equals(EventUtils.EVENT_ATTR_PRIVATE)) {
            //Dialog to ask whether you want to join this event.


            //TODO: Prompt user to enter password.
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Input the Event Password: ");

            // Set up the input
            final EditText input = new EditText(getContext());
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(input);

            input.setGravity(Gravity.CENTER);


            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String m_Text = input.getText().toString();
                    //Verify the password.
                    if (m_Text.equals(password)) {
                        //TODO: Prompt events shown
                        MapUtils.showEventsWithID(id);

                        showEventToast(isJoin);

                        onDestroy();


                        getFragmentManager()
                                .popBackStack(MapFragment.class.getSimpleName(), 0);
                        if (isJoin && receiver != null) {

                            receiver.parentIDChanged(Integer.parseInt(id), isJoin);
                        }
                    } else {
                        Toast.makeText(getContext(), "Incorrect Password. Try again please.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });


            AlertDialog dialog = builder.create();

            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

            dialog.show();

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));

        } else {
            //TODO: Show correct AR Events. Probably make a call in MapUtils for this.
            MapUtils.showEventsWithID(id);
            showEventToast(isJoin);
            onDestroy();

            getFragmentManager()
                    .popBackStack(MapFragment.class.getSimpleName(), 0);
            if (isJoin && receiver != null) {

                receiver.parentIDChanged(Integer.parseInt(id), isJoin);
            }
        }
    }


    //Function to show the toast text in the bottom of the screen when check the event points on the map.
    private void showEventToast(boolean isJoin) {
        if (isJoin) {
            Toast.makeText(getContext(), "Enjoy the event.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Check tie event.", Toast.LENGTH_SHORT).show();
        }
    }


    private void setupFloatingSearch() {
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();

                    ArrayList<Event> results = new ArrayList<>();

                    for (ArrayList<String> event : eventData) {
                        results.add(new Event(event));
                    }


                    adapter.swapData(results);
                } else {
                    mSearchView.showProgress();

                    eventData = MapUtils.arEvents;

                    ArrayList<Event> results = new ArrayList<>();

                    for (ArrayList<String> event : eventData) {
                        if (event.get(2).toLowerCase().contains(newQuery.toLowerCase())) {
                            results.add(new Event(event));
                        }
                    }
                    //mSearchView.swapSuggestions(results);
                    if (results.size() == 0) {
                        Event emptyEvent = new Event("No Events Found");
                        results.add(emptyEvent);
                    }
                    adapter.swapData(results);
                    mSearchView.hideProgress();

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
                //Do something when suggestion clicked
                ArrayList<Event> selectedEvent = new ArrayList<>();
                Event e = (Event) searchSuggestion;
                selectedEvent.add(e);
                adapter.swapData(selectedEvent);
            }

            @Override
            public void onSearchAction(String currentQuery) {

            }
        });

        mSearchView.setOnClearSearchActionListener(new FloatingSearchView.OnClearSearchActionListener() {
            @Override
            public void onClearSearchClicked() {
                adapter.swapData(eData);
            }
        });

        mSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon, TextView textView, SearchSuggestion item, int itemPosition) {
                Event event = (Event) item;

                textView.setText(event.getEventName());
            }
        });

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }




}

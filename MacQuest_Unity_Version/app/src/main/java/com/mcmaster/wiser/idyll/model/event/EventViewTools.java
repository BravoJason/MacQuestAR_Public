package com.mcmaster.wiser.idyll.model.event;

import android.widget.ListView;

import com.mcmaster.wiser.idyll.presenter.adapter.EventAdapter;

public class EventViewTools {

    public static void changeClickViewColor(ListView eventlist, int index){
        EventListAdapter eventListAdapter = (EventListAdapter) eventlist.getAdapter();
        EventItem eventItem = eventListAdapter.getItem(index);
        eventItem.setIsClick();
        eventListAdapter.notifyDataSetChanged();
    }
}

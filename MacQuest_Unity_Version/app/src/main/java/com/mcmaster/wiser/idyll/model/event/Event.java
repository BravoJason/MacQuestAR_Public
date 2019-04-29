package com.mcmaster.wiser.idyll.model.event;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcel;
import android.support.v7.app.AlertDialog;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.mcmaster.wiser.idyll.R;
import com.mcmaster.wiser.idyll.view.EventWebPageActivity;

import java.util.ArrayList;

/**
 * Building object for display info from the database
 * Created by Eric on 6/13/17.
 */

public class Event implements SearchSuggestion {
    private String id;
    private String url;
    private String eventName;
    private String eventHolder;
    private String publicAttribute;
    private String password;
    private String description;


    public String getId() {
        return id;
    }

    public void setId(String outId) {
        this.id = outId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventHolder() {
        return eventHolder;
    }

    public void setEventHolder(String eventHolder) {
        this.eventHolder = eventHolder;
    }

    public String getPublicAttribute() {
        return publicAttribute;
    }

    public void setPublicAttribute(String publicAttribute) {
        this.publicAttribute = publicAttribute;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Event(String suggestion) {
        this.eventName = suggestion;
        this.publicAttribute = "";
    }


    public Event(ArrayList<String> event) {
        this.id = event.get(0);
        this.url = event.get(1);
        this.eventName = event.get(2);
        this.eventHolder = event.get(3);
        this.publicAttribute = event.get(4);
        this.password = event.get(5);
        this.description = event.get(6);
    }

    public Event(Parcel source) {
        this.eventName = source.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public String getBody() {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }


    //Function to pop up the dialog when the user visit one sub event point and there is a action-requirement url.
    public static void pointsActionDialog(final Context context, ArrayList<String> arPoint) {

        //Verify whether the event has a action URL.
        if (!arPoint.get(EventUtils.EVENT_OBJECT_INDEX_VISIT_URL).equals(EventUtils.EVENT_WEB_URL_NONE)) {
            //Has Action URL.

            //Verify whether the event action URL is already shown.
            if (arPoint.get(EventUtils.EVENT_OBJECT_INDEX_VISIT_URL_SHOW).equals(EventUtils.EVENT_URL_NOT_SHOW)) {
                //Not show.

                //Show URL.
                //Create new web intent.
                final Intent webIntent = new Intent(context, EventWebPageActivity.class);
                webIntent.putExtra("URL", arPoint.get(EventUtils.EVENT_OBJECT_INDEX_VISIT_URL));
                webIntent.putExtra("Name", arPoint.get(EventUtils.EVENT_OBJECT_INDEX_SUB_TITLE));
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder = builder.setTitle(arPoint.get(EventUtils.EVENT_OBJECT_INDEX_VISIT_TITLE)).setMessage(arPoint.get(EventUtils.EVENT_OBJECT_INDEX_ACTION_DESC));
                AlertDialog dialog = builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Open WebView
                                context.startActivity(webIntent);

                            }
                        }).setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Cancel Dialog.
                                dialog.cancel();

                            }
                        }).create();


                if (!((Activity) context).isFinishing()) {
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));
                    //Set the flag as shown.
                    arPoint.set(EventUtils.EVENT_OBJECT_INDEX_VISIT_URL_SHOW, EventUtils.EVENT_URL_SHOWED);
                }


            }
        } else {
            if (arPoint.get(EventUtils.EVENT_OBJECT_INDEX_SUBEVENT_ACTION_FLAG).equals(EventUtils.EVENT_ACTION_TRUE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder = builder.setTitle(arPoint.get(EventUtils.EVENT_OBJECT_INDEX_VISIT_TITLE)).setMessage(arPoint.get(EventUtils.EVENT_OBJECT_INDEX_ACTION_DESC));
                AlertDialog dialog = builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Open WebView
                                dialog.cancel();

                            }
                        }).create();
                if (!((Activity) context).isFinishing()) {
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));
                    //Set the flag as shown.
                    arPoint.set(EventUtils.EVENT_OBJECT_INDEX_SUBEVENT_ACTION_FLAG, EventUtils.EVENT_ACTION_FALSE);
                }
            }
        }

    }


    //Function to pop up the event description url window.
    public static void pointsDescriptionURLDialog(final Context context, ArrayList<String> arPoint) {


        if (!arPoint.get(EventUtils.EVENT_OBJECT_INDEX_DESCRIPTION_URL).equals(EventUtils.EVENT_WEB_URL_NONE)
                &&
                arPoint.get(EventUtils.EVENT_OBJECT_INDEX_DESCRIPTION_URL_SHOW).equals(EventUtils.EVENT_URL_NOT_SHOW)) {

            arPoint.set(EventUtils.EVENT_OBJECT_INDEX_DESCRIPTION_URL_SHOW, EventUtils.EVENT_URL_SHOWED);
            //Create new web intent.
            final Intent webIntent = new Intent(context, EventWebPageActivity.class);
            webIntent.putExtra("URL", arPoint.get(EventUtils.EVENT_OBJECT_INDEX_DESCRIPTION_URL));
            webIntent.putExtra("Name", arPoint.get(EventUtils.EVENT_OBJECT_INDEX_SUB_TITLE));
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder = builder.setTitle("Check more event information").setMessage("Do you want to open webpage to check more event information?");
            AlertDialog dialog = builder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Open WebView
                            context.startActivity(webIntent);

                        }
                    }).setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Cancel Dialog.
                            dialog.cancel();

                        }
                    }).create();
            if (!((Activity) context).isFinishing()) {
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }


        }


    }


    //Function to phrase subevent id from URL.
    public static String phraseSubeventIDFromURL(String URL) {
        String subEventID = null;
        String[] temp_;
        if (URL != null) {
            temp_ = URL.split(EventUtils.EVENT_SUBEVENT_URL_TAG);

            if (temp_.length == 2) {
                //The ID is founded.
                subEventID = temp_[1].charAt(temp_[1].length() - 1) == '/' ? temp_[1].substring(0, temp_[1].length() - 1) : temp_[1];

            }
        }
        return subEventID;

    }

    //Function to find event point in the event point list by ID.
    public static ArrayList<String> findEventPointByID(ArrayList<ArrayList<String>> eventLists, String ID) {
        ArrayList<String> eventPoint = null;
        if (eventLists != null && eventLists.size() != 0) {
            for (ArrayList<String> point : eventLists) {
                if (point.get(EventUtils.EVENT_OBJECT_INDEX_SUB_ID).equals(ID)) {
                    eventPoint = point;
                }
            }
        }

        return eventPoint;
    }


    //Function to change click event view color


}





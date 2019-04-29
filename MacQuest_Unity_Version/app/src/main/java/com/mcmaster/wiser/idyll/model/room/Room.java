package com.mcmaster.wiser.idyll.model.room;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * Room object to display information from the database
 * Created by Eric on 7/05/17.
 */

public class Room implements SearchSuggestion {
    private int rid;
    private int outid;
    private String name;
    private int floor;
    private String rawLocation;
    private String nearestStaircase;
    private String routingLocation;
    private String buildingName;

    public String getBuildingName() {
        return buildingName;
    }

    public String getRawLocation() {
        return rawLocation;
    }

    public String getNearestStaircase() {
        return nearestStaircase;
    }

    public String getroutingLocation() {
        return routingLocation;
    }
    public Room(int rid , int outid, String name , int floor, String rawLocation, String buildingName, String routingLocation, String nearestStaircase){
        this.rid = rid;
        this.outid = outid;
        this.name = name;
        this.floor = floor;
        this.rawLocation = rawLocation;
        this.buildingName = buildingName;
        this.routingLocation=routingLocation;
        this.nearestStaircase=nearestStaircase;
    }

    public int getRid() {
        return rid;
    }

    public int getOutid() {
        return outid;
    }

    public String getRoomNumber() {
        return name;
    }

    public int getFloor() {
        return floor;
    }

    @Override
    public String getBody() {
        String buildingAndNumber = buildingName + " " + name;
        //String buildingAndNumber = buildingName;

        //Log.d("search",buildingAndNumber);
        return buildingAndNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

    public Room(Parcel source){
        this.name = source.readString();
    }

    public static final Creator<Room> CREATOR = new Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel source) {
            return new Room(source);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };
}

package com.mcmaster.wiser.idyll.model.building;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * Building object for display info from the database
 * Created by Eric on 6/13/17.
 */

public class Building implements SearchSuggestion{
    private String name;
    private String shortName;
    private String idNum;
    private String room;
    private String outId;
    private String utility;
    private String uuid;
    private int sync_status;

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getOutId() {
        return outId;
    }

    public void setOutId(String outId) {
        this.outId = outId;
    }

    public String getUtility() {
        return utility;
    }

    public void setUtility(String utility) {
        this.utility = utility;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getSync_status() {
        return sync_status;
    }

    public void setSync_status(int sync_status) {
        this.sync_status = sync_status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private String location;

    private boolean mIsHistory = false;

    public Building(String suggestion){
        this.name = suggestion;
    }

    public Building(Parcel source){
        this.name = source.readString();
        this.mIsHistory = source.readInt() != 0;
    }

    public Building(String sname, String sShortName, String sRoom, String sOutid, String sLocation, String sUtility, int IntSync){
        this.name = sname == null ? "":sname;
        this.shortName = sShortName == null ? "":sShortName;
        this.room = sRoom == null ? "":sRoom;
        this.outId = sOutid == null ? "":sOutid;
        this.location = sLocation == null ? "":sLocation;
        this.utility = sUtility == null ? "":sUtility;
        this.sync_status = IntSync;
    }

    public Building(String name, String shortName, String idNum){
        this.name = name;
        this.shortName = shortName;
        this.idNum = idNum;
    }

    public Building(String name, String shortName, String idNum, String location){
        this.name = name;
        this.shortName = shortName;
        this.idNum = idNum;
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setIsHistory (boolean isHistory){
        this.mIsHistory = isHistory;
    }

    public boolean getIsHistory(){
        return this.mIsHistory;
    }

    public static final Creator<Building> CREATOR = new Creator<Building>() {
        @Override
        public Building createFromParcel(Parcel source) {
            return new Building(source);
        }

        @Override
        public Building[] newArray(int size) {
            return new Building[size];
        }
    };


    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getIdNum(){
        return idNum;
    }

    @Override
    public String getBody() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(mIsHistory ? 1 : 0);
    }
}

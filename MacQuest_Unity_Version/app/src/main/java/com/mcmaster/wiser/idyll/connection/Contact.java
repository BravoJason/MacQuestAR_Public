package com.mcmaster.wiser.idyll.connection;

/**
 * Created by Ahmed on 8/5/2017.
 */

public class Contact {
    private String bName;
    private String bShortName;
    private String bLocation;
    private String bUtility;
    private String bRoom;
    private String bOutid;
    private String bUuid;
    private int bSyncStatus;

    public Contact(String Name, String ShortName, String Location, String Utility, String Room, String Outid, String Uuid, int Sync_status)
    {
        this.bName=Name;
        this.bShortName=ShortName;
        this.bLocation=Location;
        this.bUtility=Utility;
        this.bRoom=Room;
        this.bOutid=Outid;
        this.bUuid=Uuid;
        this.bSyncStatus=Sync_status;

    }

    public String getbName() {
        return bName;
    }

    public void setbName(String bName) {
        this.bName = bName;
    }

    public String getbShortName() {
        return bShortName;
    }

    public void setbShortName(String bShortName) {
        this.bShortName = bShortName;
    }

    public String getbLocation() {
        return bLocation;
    }

    public void setbLocation(String bLocation) {
        this.bLocation = bLocation;
    }

    public String getbUtility() {
        return bUtility;
    }

    public void setbUtility(String bUtility) {
        this.bUtility = bUtility;
    }

    public String getbRoom() {
        return bRoom;
    }

    public void setbRoom(String bRoom) {
        this.bRoom = bRoom;
    }

    public String getbOutid() {
        return bOutid;
    }

    public void setbOutid(String bOutid) {
        this.bOutid = bOutid;
    }

    public String getbUuid() {
        return bUuid;
    }

    public void setbUuid(String bUuid) {
        this.bUuid = bUuid;
    }

    public int getSyncStatus() {
        return bSyncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        bSyncStatus = syncStatus;
    }
}

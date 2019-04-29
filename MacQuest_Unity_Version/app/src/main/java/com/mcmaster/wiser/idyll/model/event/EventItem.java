package com.mcmaster.wiser.idyll.model.event;

public class EventItem {
    private String eventName;
    private boolean isVisit = false;
    private boolean isClick = false;

    public EventItem(String eventName, boolean isVisit, boolean isClick){
        this.eventName = eventName;
        this.isVisit = isVisit;
        this.isClick = isClick;
    }


    public String getEventName(){
        return eventName;
    }

    public void setEventName(String name){
        eventName = name;
    }

    public void setVisit(boolean isVisit){
        this.isVisit = isVisit;
    }

    public boolean getIsVisit(){
        return isVisit;
    }

    public void setIsClick(){
        isClick = !isClick;
    }

    public boolean getIsClick(){
        return isClick;
    }




}

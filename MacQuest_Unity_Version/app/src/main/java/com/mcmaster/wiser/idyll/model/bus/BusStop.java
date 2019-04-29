package com.mcmaster.wiser.idyll.model.bus;

/**
 * Object class for a BusStop
 * Created by Eric on 6/19/17.
 */

public class BusStop {
    private String stopName;
    private String routeShortName;
    private String routeLongName;
    private String nextBusArrival;

    public BusStop(String stopName, String routeShortName, String routeLongName, String nextBusArrival){
        this.stopName = stopName;
        this.routeShortName = routeShortName;
        this.routeLongName = routeLongName;
        this.nextBusArrival = nextBusArrival;
    }
    public String getStopName() {
        return stopName;
    }

    public String getRouteShortName() {
        return routeShortName;
    }

    public String getRouteLongName() {
        return routeLongName;
    }

    public String getNextBusArrival() {
        return nextBusArrival;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public void setRouteShortName(String routeShortName) {
        this.routeShortName = routeShortName;
    }

    public void setRouteLongName(String routeLongName) {
        this.routeLongName = routeLongName;
    }

    public void setNextBusArrival(String nextBusArrival) {
        this.nextBusArrival = nextBusArrival;
    }

}

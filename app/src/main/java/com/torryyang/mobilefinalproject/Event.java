package com.torryyang.mobilefinalproject;

/**
 * Created by Torry on 20/11/2016.
 */

public class Event {
    private String eventName, eventTime, eventDesc, eventLoc, eventImg;

    public Event(String n, String d, String t, String l, String i) {
        eventName = n;
        eventDesc = d;
        eventTime = t;
        eventLoc = l;
        eventImg = i;
    }

    public String getName() {
        return eventName;
    }

    public String getDesc() {
        return eventDesc;
    }

    public String getTime() {
        return eventTime;
    }

    public String getLoc() {
        return eventLoc;
    }

    public String getImg() {
        return eventImg;
    }
}

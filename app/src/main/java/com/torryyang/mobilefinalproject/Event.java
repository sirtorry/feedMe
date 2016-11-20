package com.torryyang.mobilefinalproject;

/**
 * Created by Torry on 20/11/2016.
 */

public class Event {
    private String eventName, eventTime, eventDesc, eventLoc;

    public Event(String n, String d, String t, String l) {
        eventName = n;
        eventDesc = d;
        eventTime = t;
        eventLoc = l;
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
}

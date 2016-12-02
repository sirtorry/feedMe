package com.torryyang.mobilefinalproject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Created by Torry on 21/11/2016.
 */

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView eventName;

        public ViewHolder(View itemView) {
            super(itemView);

            eventName = (TextView) itemView.findViewById(R.id.event_name);
        }
    }

    private List<Event> mEvents;
    private Context mContext;

    public EventsAdapter(Context context,List<Event> events) {
        Collections.reverse(events);
        mEvents = events;
        mContext = context;
    }

    private Context getmContext() {
        return mContext;
    }

    public EventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View eventView = inflater.inflate(R.layout.item_event,parent,false);
        ViewHolder viewHolder = new ViewHolder(eventView);
        return viewHolder;
    }

    public void onBindViewHolder(EventsAdapter.ViewHolder viewHolder, int position) {
        Event event = mEvents.get(position);

        TextView textView = viewHolder.eventName;
        textView.setText(event.getName() + " | " + event.getDesc() + " | " + event.getTime() +  " | "  + event.getLoc() + " | "  + event.getImg());
    }

    public int getItemCount() {
        return mEvents.size();
    }
}

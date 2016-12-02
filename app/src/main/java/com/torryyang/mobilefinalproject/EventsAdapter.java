package com.torryyang.mobilefinalproject;

import android.content.Context;
import android.content.Intent;
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView eventName;
        private Context context;

        public ViewHolder(Context context, View itemView) {
            super(itemView);

            eventName = (TextView) itemView.findViewById(R.id.event_name);
            this.context = context;
            itemView.setOnClickListener(this);
        }
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                Event event = mEvents.get(position);
                // We can access the data within the views
                Intent intent = new Intent(context,EventInfoActivity.class);
                intent.putExtra("name", event.getName());
                intent.putExtra("desc", event.getDesc());
                intent.putExtra("time", event.getTime());
                intent.putExtra("loc", event.getLoc());
                intent.putExtra("img",event.getImg());
                context.startActivity(intent);
            }
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
        ViewHolder viewHolder = new ViewHolder(context,eventView);
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

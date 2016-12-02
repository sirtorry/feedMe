package com.torryyang.mobilefinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class EventInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        TextView infoName = (TextView) findViewById(R.id.info_name);
        TextView infoDesc = (TextView) findViewById(R.id.info_desc);
        TextView infoTime = (TextView) findViewById(R.id.info_time);
        TextView infoLoc = (TextView) findViewById(R.id.info_loc);
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null) {
            infoName.setText((String) bd.get("name"));
            infoDesc.setText((String) bd.get("desc"));
            infoTime.setText((String) bd.get("time"));
            infoLoc.setText((String) bd.get("loc"));
        }
    }
}

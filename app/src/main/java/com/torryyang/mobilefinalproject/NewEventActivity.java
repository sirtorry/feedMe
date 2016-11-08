package com.torryyang.mobilefinalproject;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewEventActivity extends AppCompatActivity {

    Button makeButton;
    EditText eventName,eventDesc,eventLoc,eventTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        Button makeButton = (Button)findViewById(R.id.add_event_button);
        eventName = (EditText)findViewById(R.id.event_name);
        eventDesc = (EditText)findViewById(R.id.event_descr);
        eventLoc = (EditText)findViewById(R.id.event_loc);
        eventTime = (EditText)findViewById(R.id.event_time);

        makeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = eventName.getText().toString();
                String desc = eventDesc.getText().toString();
                String loc = eventLoc.getText().toString();
                String time = eventTime.getText().toString();
                SQLiteDatabase locDb = getBaseContext().openOrCreateDatabase("local-data.db",MODE_PRIVATE,null);
                locDb.execSQL("CREATE TABLE IF NOT EXISTS events(name TEXT, desc TEXT, eventTime TEXT, location TEXT);");
                locDb.execSQL("INSERT INTO events VALUES('" + name + "','" + desc + "','" + loc + "','" + time + "');");
                locDb.close();
                finish();
            }
        });
    }
}

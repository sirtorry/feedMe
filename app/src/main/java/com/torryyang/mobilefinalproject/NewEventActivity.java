package com.torryyang.mobilefinalproject;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

public class NewEventActivity extends AppCompatActivity {

    Button makeButton;
    EditText eventName, eventDesc, eventLoc, eventTime;
    TextView getPlace;
    int PLACE_PICKER_REQUEST = 1;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        Button makeButton = (Button) findViewById(R.id.add_event_button);
        eventName = (EditText) findViewById(R.id.event_name);
        eventDesc = (EditText) findViewById(R.id.event_descr);
        eventLoc = (EditText) findViewById(R.id.event_loc);
        eventTime = (EditText) findViewById(R.id.event_time);
        getPlace = (TextView) findViewById(R.id.add_loc);
        getPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(NewEventActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });

        makeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = eventName.getText().toString();
                String desc = eventDesc.getText().toString();
                String loc = eventLoc.getText().toString();
                String time = eventTime.getText().toString();
                SQLiteDatabase locDb = getBaseContext().openOrCreateDatabase("local-data.db", MODE_PRIVATE, null);
                locDb.execSQL("CREATE TABLE IF NOT EXISTS events(name TEXT, desc TEXT, eventTime TEXT, location TEXT);");
                locDb.execSQL("INSERT INTO events VALUES('" + name + "','" + desc + "','" + loc + "','" + time + "');");
                locDb.close();
                finish();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("NewEvent Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PLACE_PICKER_REQUEST) {
            if(resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data,this);
                String address = place.getAddress().toString();
                getPlace.setText(address);
                eventLoc.setText(address);
//                SharedPreferences sharedPreferences =
//                        PreferenceManager.getDefaultSharedPreferences(this);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString(getString(R.string.pref_location_key),address);
//                editor.commit();
            }
        } else {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}

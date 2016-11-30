package com.torryyang.mobilefinalproject;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewEventActivity extends AppCompatActivity {

    Button makeButton, addImageButton;
    EditText eventName, eventDesc, eventLoc, eventTime;
    static final int DIALOG_ID = 0;
    int eventHour, eventMin;
    Uri file;
    Bitmap tempImage;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Uri.fromFile(getOutputMediaFile());
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, file);
        startActivityForResult(takePictureIntent, 0);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                addImageButton.setEnabled(true);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        Button makeButton = (Button) findViewById(R.id.add_event_button);
        eventName = (EditText) findViewById(R.id.new_event_name);
        eventDesc = (EditText) findViewById(R.id.new_event_descr);
        eventLoc = (EditText) findViewById(R.id.new_event_loc);
        eventTime = (EditText) findViewById(R.id.new_event_time);
        addImageButton = (Button) findViewById(R.id.add_pic_but);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            addImageButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }

        eventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DIALOG_ID);
            }
        });

        eventLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(NewEventActivity.this), 1);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });

        addImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispatchTakePictureIntent();
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

    protected TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minOfHour) {
            eventHour = hourOfDay;
            eventMin = minOfHour;
            eventTime.setText(eventHour + ":" + eventMin);
        }
    };

    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ID)
            return new TimePickerDialog(NewEventActivity.this,timePickerListener, eventHour, eventMin,false);
        return null;
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
        switch(requestCode){
            case 0:
                if (resultCode == RESULT_OK) {
                    try {
                        tempImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), file);
                        addImageButton.setText("Photo attached. Click to change.");
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            case 1:
                if(resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(data,this);
                    String address = place.getAddress().toString();
                    eventLoc.setText(address);
                    //                SharedPreferences sharedPreferences =
                    //                        PreferenceManager.getDefaultSharedPreferences(this);
                    //                SharedPreferences.Editor editor = sharedPreferences.edit();
                    //                editor.putString(getString(R.string.pref_location_key),address);
                    //                editor.commit();
                }
                break;
        }
    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MobileFinal");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
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
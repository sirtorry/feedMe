package com.torryyang.mobilefinalproject;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class NewEventActivity extends AppCompatActivity {

    Button makeButton, addImageButton;
    EditText eventName, eventDesc, eventLoc, eventTime;
    static final int DIALOG_ID = 0;
    int eventHour, eventMin;
    Uri file;
    String name,desc,loc,time,imgUrl;
    Bitmap tempImage;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    Boolean imageAdded = false;
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

//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        // Save the user's current game state
//        savedInstanceState.putInt(PLAYER_SCORE, mCurrentScore);
//        savedInstanceState.putInt(PLAYER_LEVEL, mCurrentLevel);
//
//        // Always call the superclass so it can save the view hierarchy state
//        super.onSaveInstanceState(savedInstanceState);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        makeButton = (Button) findViewById(R.id.add_event_button);
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

                name = eventName.getText().toString();
                desc = eventDesc.getText().toString();
                loc = eventLoc.getText().toString();
                time = eventTime.getText().toString();

//                newEventObj params = new newEventObj(name,desc,time,loc,tempImage,imageAdded);
//                postEvent myTask = new postEvent();
//                myTask.execute(params);

                if(imageAdded) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    tempImage.compress(Bitmap.CompressFormat.PNG,100,baos);
                    byte[] imgData = baos.toByteArray();
                    String path = "eventImages/" + UUID.randomUUID() + ".png";
                    StorageReference eventImageRef = storage.getReference(path);
                    StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("eventName",name).build();
                    UploadTask uploadTask =eventImageRef.putBytes(imgData,metadata);
                    uploadTask.addOnSuccessListener(NewEventActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imgUrl = taskSnapshot.getDownloadUrl().toString();
//                            JSONObject obj = new JSONObject();
//                            try {
//                                obj.put("event_title", name);
//                                obj.put("event_description",desc);
//                                obj.put("event_location",loc);
//                                obj.put("event_start_time",time);
//                                obj.put("event_image_url",imgUrl);
//                                obj.put("user_id",1);
//                                excutePost("http://plato.cs.virginia.edu/~psa5dg/create",obj);
//                            } catch(Exception e) {
//                                e.printStackTrace();
//                            }
                        }
                    });
                }
//                else{
//                    JSONObject obj = new JSONObject();
//                    try {
//                        obj.put("event_title", name);
//                        obj.put("event_description",desc);
//                        obj.put("event_location",loc);
//                        obj.put("event_start_time",time);
//                        obj.put("event_image_url",imgUrl);
//                        obj.put("user_id",1);
//                        excutePost("http://plato.cs.virginia.edu/~psa5dg/create",obj);
//                    } catch(Exception e) {
//                        e.printStackTrace();
//                    }
//                }
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
            String hh = Integer.toString(hourOfDay);
            String mm = Integer.toString(minOfHour);
            if(hh.length() < 2) {
                hh = "0" + hh;
            }
            if(mm.length() < 2) {
                mm = "0" + mm;
            }
            eventTime.setText(hh + ":" + mm);
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
                        imageAdded = true;
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

    private static class newEventObj {
        String name,desc,time,loc,imgUrl;
        Bitmap img;
        Boolean imgAdded;

        newEventObj(String n, String d, String t, String l, Bitmap i, Boolean a) {
            name = n;
            desc = d;
            time = t;
            loc = l;
            img = i;
            imgAdded = a;
        }
    }

//    private class postEvent extends AsyncTask<newEventObj, Void, Void> {
//        @Override
//        protected Void doInBackground(newEventObj... params) {
//            if(params[0].imgAdded) {
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                params[0].img.compress(Bitmap.CompressFormat.PNG,100,baos);
//                byte[] imgData = baos.toByteArray();
//                String path = "eventImages/" + UUID.randomUUID() + ".png";
//                StorageReference eventImageRef = storage.getReference(path);
//                StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("eventName",name).build();
//                UploadTask uploadTask =eventImageRef.putBytes(imgData,metadata);
//                uploadTask.addOnSuccessListener(NewEventActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        imgUrl = taskSnapshot.getDownloadUrl().toString();
//                    }
//                });
//
//            }
//            return null;
//        }
//    }

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

    public static Boolean excutePost(String targetURL, JSONObject jsonParam)
    {
        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST"); // hear you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
            connection.setRequestProperty("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
            connection.connect();

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream ());
            wr.writeBytes(jsonParam.toString());
            wr.flush();
            wr.close ();

            InputStream is;
            int response = connection.getResponseCode();
            if (response >= 200 && response <=399){
                //return is = connection.getInputStream();
                return true;
            } else {
                //return is = connection.getErrorStream();
                return false;
            }


        } catch (Exception e) {

            e.printStackTrace();
            return false;

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }
    }
}
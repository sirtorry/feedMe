package com.torryyang.mobilefinalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

public class EventInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        TextView infoDesc = (TextView) findViewById(R.id.info_desc);
        TextView infoTime = (TextView) findViewById(R.id.info_time);
        TextView infoLoc = (TextView) findViewById(R.id.info_loc);
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null) {
            new DownloadImageTask((ImageView) findViewById(R.id.info_image))
                    .execute((String) bd.get("img"));
            setTitle((String) bd.get("name"));
            infoDesc.setText(Html.fromHtml("<b><big>" + "What? "  + "</big></b>" + (String) bd.get("desc")));
            infoTime.setText(Html.fromHtml("<b><big>" + "When? "  + "</big></b>" + (String) bd.get("time")));
            infoLoc.setText(Html.fromHtml("<b><big>" + "Where? "  + "</big></b>" + (String) bd.get("loc")));
        }
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}

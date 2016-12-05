package com.torryyang.mobilefinalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.InputStream;

public class EventInfoActivity extends AppCompatActivity {

    String loc;

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
            loc = (String) bd.get("loc");
            String imgUrl = (String) bd.get("img");
            if (imgUrl != "NULL") {
                new DownloadImageTask((ImageView) findViewById(R.id.info_image),(ProgressBar) findViewById(R.id.loading_img))
                        .execute(imgUrl);
            }
            setTitle((String) bd.get("name"));
            infoDesc.setText(Html.fromHtml("<b><big>" + "What? "  + "</big></b>" + (String) bd.get("desc")));
            infoTime.setText(Html.fromHtml("<b><big>" + "When? "  + "</big></b>" + (String) bd.get("time")));
            infoLoc.setText(Html.fromHtml("<b><big>" + "Where? "  + "</big></b>" + loc));
        }

        infoLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String map = "http://maps.google.co.in/maps?q=" + loc;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                startActivity(intent);
            }
        });
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        ProgressBar pb;

        public DownloadImageTask(ImageView bmImage, ProgressBar pb) {
            this.bmImage = bmImage;
            this.pb = pb;
        }

        protected void onPreExecute() {
            bmImage.setVisibility(View.GONE);
            pb.setVisibility(View.VISIBLE);
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
            pb.setVisibility(View.GONE);
            bmImage.setVisibility(View.VISIBLE);
        }
    }
}

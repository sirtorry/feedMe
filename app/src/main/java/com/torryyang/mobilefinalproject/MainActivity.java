package com.torryyang.mobilefinalproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.facebook.stetho.Stetho;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

//    private TextView tvData;
    SwipeRefreshLayout mSwipeRefreshLayout;
    int lastEventId = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());

//        Button testButton = (Button) findViewById(R.id.testButton);
//        testButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                new JSONTask().execute("http://plato.cs.virginia.edu/~psa5dg/");
//            }
//        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,NewEventActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ArrayList<Event> events = new ArrayList<Event>();
        SQLiteDatabase locDb = getBaseContext().openOrCreateDatabase("local-data.db",MODE_PRIVATE,null);
        locDb.execSQL("CREATE TABLE IF NOT EXISTS events(name TEXT, desc TEXT, eventTime TEXT, location TEXT, imageUrl TEXT, eventId TEXT);");
        Cursor query = locDb.rawQuery("SELECT * from events",null);
        Cursor mcursor = locDb.rawQuery("SELECT count(*) FROM events", null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if(icount > 0) {
            while (query.moveToNext()) {
                String name = query.getString(0);
                String desc = query.getString(1);
                String time = query.getString(2);
                String loc = query.getString(3);
                String img = query.getString(4);
                events.add(new Event(name,desc,time,loc,img));
            }
        }
        locDb.close();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        RecyclerView rvEvents = (RecyclerView) findViewById(R.id.rvEvents);
        rvEvents.addItemDecoration(new SimpleDividerItemDecoration(this));
        EventsAdapter adapter = new EventsAdapter(this,events);
        rvEvents.setAdapter(adapter);
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                SQLiteDatabase locDb = getBaseContext().openOrCreateDatabase("local-data.db",MODE_PRIVATE,null);
                locDb.execSQL("CREATE TABLE IF NOT EXISTS events(name TEXT, desc TEXT, eventTime TEXT, location TEXT, imageUrl TEXT, eventId TEXT);");
                Cursor query = locDb.rawQuery("SELECT * from events",null);
                query.moveToLast();
                lastEventId = Integer.valueOf(query.getString(5));
                new JSONTask().execute("http://plato.cs.virginia.edu/~psa5dg/created/"+String.valueOf(lastEventId));
            }
        });
    }

    void refreshItems() {
        ArrayList<Event> events = new ArrayList<Event>();
        SQLiteDatabase locDb = getBaseContext().openOrCreateDatabase("local-data.db",MODE_PRIVATE,null);
        locDb.execSQL("CREATE TABLE IF NOT EXISTS events(name TEXT, desc TEXT, eventTime TEXT, location TEXT, imageUrl TEXT, eventId TEXT);");
        Cursor query = locDb.rawQuery("SELECT * from events",null);
        if(query != null) {
            while (query.moveToNext()) {
                String name = query.getString(0);
                String desc = query.getString(1);
                String time = query.getString(2);
                String loc = query.getString(3);
                String img = query.getString(4);
                events.add(new Event(name,desc,time,loc,img));
            }
        }
        locDb.close();
        onItemsLoadComplete(events);
    }
    void onItemsLoadComplete(ArrayList<Event> events) {
        EventsAdapter adapter = new EventsAdapter(this,events);
        RecyclerView rvEvents = (RecyclerView) findViewById(R.id.rvEvents);
        rvEvents.setAdapter(adapter);
        rvEvents.invalidate();
        mSwipeRefreshLayout.setRefreshing(false);
    }


    public void onResume() {
        super.onResume();  // Always call the superclass method first
        SQLiteDatabase locDb = getBaseContext().openOrCreateDatabase("local-data.db",MODE_PRIVATE,null);
        locDb.execSQL("CREATE TABLE IF NOT EXISTS events(name TEXT, desc TEXT, eventTime TEXT, location TEXT, imageUrl TEXT, eventId TEXT);");
        Cursor query = locDb.rawQuery("SELECT * from events",null);
        query.moveToLast();
        lastEventId = Integer.valueOf(query.getString(5));
        new JSONTask().execute("http://plato.cs.virginia.edu/~psa5dg/created/"+String.valueOf(lastEventId));
//        tvData = (TextView)findViewById(R.id.tempShow);
//        ArrayList<Event> events = new ArrayList<Event>();
//        events.add(new Event("Birthday Party", "free cake", "13:54", "180 McCormick Rd, Charlottesville, VA 22903, USA", "http://www.seriouseats.com/recipes/assets_c/2013/08/20130624-257009-chicken-rice-set-edit-thumb-625xauto-343576.jpg"));
//        events.add(new Event("4th year don't care", "day drink!", "12:00", "Charlottesville, VA 22903, USA", "http://www.seriouseats.com/recipes/assets_c/2013/08/20130624-257009-chicken-rice-set-edit-thumb-625xauto-343576.jpg"));
//        events.add(new Event("ACM Party", "pancakes!", "17:30", "Rice Hall Information Technology Engineering Building, 85 Engineer's Way, Charlottesville, VA 22903, USA", "http://www.seriouseats.com/recipes/assets_c/2013/08/20130624-257009-chicken-rice-set-edit-thumb-625xauto-343576.jpg"));
//        events.add(new Event("card games w/ nerds", "soylent", "15:00", "Ash Tree Apartments, Madison Avenue, Charlottesville, VA","http://www.seriouseats.com/recipes/assets_c/2013/08/20130624-257009-chicken-rice-set-edit-thumb-625xauto-343576.jpg"));
//        events.add(new Event("music club", "jk day drink!", "16:00", "McIntire Department of Music, Cabell Drive, Charlottesville, VA","http://www.seriouseats.com/recipes/assets_c/2013/08/20130624-257009-chicken-rice-set-edit-thumb-625xauto-343576.jpg"));
//        events.add(new Event("really cool dude", "asian food", "11:30", "Uncommon Charlottesville, West Main Street, Charlottesville, VA","http://www.seriouseats.com/recipes/assets_c/2013/08/20130624-257009-chicken-rice-set-edit-thumb-625xauto-343576.jpg"));
//        events.add(new Event("psych club", "gatorade", "14:00", "Gilmer Hall, McCormick Road, Charlottesville, VA","http://www.seriouseats.com/recipes/assets_c/2013/08/20130624-257009-chicken-rice-set-edit-thumb-625xauto-343576.jpg"));
//        events.add(new Event("AFC partay", "juice", "13:00", "Aquatic & Fitness Center, Whitehead Road, Charlottesville, VA","http://www.seriouseats.com/recipes/assets_c/2013/08/20130624-257009-chicken-rice-set-edit-thumb-625xauto-343576.jpg"));

//        SQLiteDatabase locDb = getBaseContext().openOrCreateDatabase("local-data.db",MODE_PRIVATE,null);
//        locDb.execSQL("CREATE TABLE IF NOT EXISTS events(name TEXT, desc TEXT, eventTime TEXT, location TEXT, imageUrl TEXT);");

//        locDb.execSQL("INSERT INTO events VALUES('testEvent', 'this is a test event', 'nowhere', '13:54 06/11/16');");
//        locDb.execSQL("INSERT INTO events VALUES('Birthday Party', 'free cake', '13:54', '180 McCormick Rd, Charlottesville, VA 22903, USA');");
//        locDb.execSQL("INSERT INTO events VALUES('4th year don't care', 'day drink!', '12:00', 'Charlottesville, VA 22903, USA');");
//        locDb.execSQL("INSERT INTO events VALUES('ACM Party', 'pancakes!', '17:30', 'Rice Hall Information Technology Engineering Building, 85 Engineer's Way, Charlottesville, VA 22903, USA');");

//        String jsonString = callURL("http://plato.cs.virginia.edu/~psa5dg/created");
//        try {
//
//            JSONArray jsonArray = new JSONArray(jsonString);
//            int count = jsonArray.length();
//            for(int i=0 ; i< count; i++){   // iterate through jsonArray
//                JSONObject jsonObject = jsonArray.getJSONObject(i);  // get jsonObject @ i position
//                String name = jsonObject.getString("event_title");
//                String desc = jsonObject.getString("event_description");
//                String loc = jsonObject.getString("event_location");
//                String time = jsonObject.getString("event_post_time");
//                String imgUrl = jsonObject.getString("event_image_url");
//                locDb.execSQL("INSERT INTO events VALUES('" + name + "','" + desc + "','" + time + "','" + loc + "','" + imgUrl + "');");
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        Cursor query = locDb.rawQuery("SELECT * from events",null);
//        if(query != null) {
//            while (query.moveToNext()) {
//                String name = query.getString(0);
//                String desc = query.getString(1);
//                String loc = query.getString(2);
//                String time = query.getString(3);
//                String img = query.getString(4);
//                events.add(new Event(name,desc,time,loc,img));
//            }
//        }
//        locDb.close();
//            tvData.setText(curStored);

//        if(events.size() == 0) {
//        }

//        locDb.execSQL("INSERT INTO events VALUES('testEvent', 'this is a test event', 'nowhere', '13:54 06/11/16');");
//        Cursor query = sqliteDatabase.rawQuery("SELECT * from events",null);
//        if(query.moveToFirst()) {
//            String name = query.getString(0);
//            Toast.makeText(getBaseContext(),name,Toast.LENGTH_LONG).show();
//        } else {
//            Toast.makeText(getBaseContext(),"error",Toast.LENGTH_LONG).show();
//        }

//        RecyclerView rvEvents = (RecyclerView) findViewById(R.id.rvEvents);
//        rvEvents.addItemDecoration(new SimpleDividerItemDecoration(this));
//        EventsAdapter adapter = new EventsAdapter(this,events);
//        rvEvents.setAdapter(adapter);
//        rvEvents.setLayoutManager(new LinearLayoutManager(this));
    }

    public void getNew(int n) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String res = null;
        try {
            URL url = new URL("http://plato.cs.virginia.edu/~psa5dg/created/"+String.valueOf(n));
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();

            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            res = buffer.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
            try {
                if(reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(res != null){
            try {
                JSONArray jsonArray = new JSONArray(res);
                int count = jsonArray.length();
                SQLiteDatabase locDb = getBaseContext().openOrCreateDatabase("local-data.db",MODE_PRIVATE,null);
                for(int i=0 ; i< count; i++){   // iterate through jsonArray
                    JSONObject jsonObject = jsonArray.getJSONObject(i);  // get jsonObject @ i position
                    String name = jsonObject.getString("event_title");
                    String desc = jsonObject.getString("event_description");
                    String loc = jsonObject.getString("event_location");
                    String time = jsonObject.getString("event_post_time");
                    String imgUrl = jsonObject.getString("event_image_url");
                    lastEventId = Integer.parseInt(jsonObject.getString("event_id"));
                    locDb.execSQL("INSERT INTO events VALUES('" + name + "','" + desc + "','" + time + "','" + loc + "','" + imgUrl + "');");
                }
                refreshItems();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class JSONTask extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }
                try {
                    if(reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override


        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONArray jsonArray = new JSONArray(result);
                int count = jsonArray.length();
                SQLiteDatabase locDb = getBaseContext().openOrCreateDatabase("local-data.db",MODE_PRIVATE,null);
                for(int i=0 ; i< count; i++) {   // iterate through jsonArray
                    JSONObject jsonObject = jsonArray.getJSONObject(i);  // get jsonObject @ i position
                    String name = jsonObject.getString("event_title");
                    String desc = jsonObject.getString("event_description");
                    String loc = jsonObject.getString("event_location");
                    String time = jsonObject.getString("event_post_time");
                    String imgUrl = jsonObject.getString("event_image_url");
                    String eventId = jsonObject.getString("event_id");
                    locDb.execSQL("INSERT INTO events VALUES('" + name + "','" + desc + "','" + time + "','" + loc + "','" + imgUrl +  "','" + eventId + "');");
                }
                refreshItems();
            } catch(Exception e) {
                e.printStackTrace();
            }
//            Toast.makeText(getBaseContext(),Integer.toString(lastEventId),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    public static String callURL(String myURL) {
//        StringBuilder sb = new StringBuilder();
//        URLConnection urlConn = null;
//        InputStreamReader in = null;
//        try {
//            URL url = new URL(myURL);
//            urlConn = url.openConnection();
//            if (urlConn != null)
//                urlConn.setReadTimeout(60 * 1000);
//            if (urlConn != null && urlConn.getInputStream() != null) {
//                in = new InputStreamReader(urlConn.getInputStream(),
//                        Charset.defaultCharset());
//                BufferedReader bufferedReader = new BufferedReader(in);
//                if (bufferedReader != null) {
//                    int cp;
//                    while ((cp = bufferedReader.read()) != -1) {
//                        sb.append((char) cp);
//                    }
//                    bufferedReader.close();
//                }
//            }
//            in.close();
//        } catch (Exception e) {
//            throw new RuntimeException("Exception while calling URL:"+ myURL, e);
//        }
//
//        return sb.toString();
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

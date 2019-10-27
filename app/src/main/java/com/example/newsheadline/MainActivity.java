package com.example.newsheadline;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    String API_KEY = "a65a65ef7a2f4c4c89a76a64790c4af9";
    String NEWS_SOURCE = "abc-news";
    ListView listNews;
    ProgressBar loader;
    SharedPreferences prefs;
    String previous = "FileName";
    MyDatabaseOpenHelper mydb;
    int positionClicked = 0;


    ArrayList<HashMap<String, String>> dataList = new ArrayList<>();

    static final String KEY_AUTHOR = "author";
    static final String KEY_TITLE = "title";
    static final String KEY_DESCRIPTION = "description";
    static final String KEY_URL = "url";
    static final String KEY_URLTOIMAGE = "urlToImage";
    static final String KEY_PUBLISHEDAT = "publishedAt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listNews = findViewById(R.id.listNews);
        loader = findViewById(R.id.loader);
        listNews.setEmptyView(loader);
        prefs = getSharedPreferences(previous, MODE_PRIVATE);


        //get a database:
        mydb = new MyDatabaseOpenHelper(this);
        SQLiteDatabase db = mydb.getWritableDatabase();

        //query all the results from the database:
        String [] columns = {MyDatabaseOpenHelper.COL_ID, MyDatabaseOpenHelper.COL_TITLE, MyDatabaseOpenHelper.COL_DESCRIPTION, MyDatabaseOpenHelper.COL_URL};
        Cursor results = db.query(false, MyDatabaseOpenHelper.TABLE_NAME, columns, null, null, null, null, null, null);

        //find the column indices:
        int titleColumnIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_TITLE);
        int descriptionColIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_DESCRIPTION);
        int idColIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_ID);
        int urlColIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_URL);


        if (Downloader.isNetworkConnected(getApplicationContext())) {
            DownloadNews newsTask = new DownloadNews();
            newsTask.execute();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }

    }



    class DownloadNews extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {

            String xml = Downloader.excuteGet("https://newsapi.org/v2/top-headlines?sources=" + NEWS_SOURCE + "&apiKey=" + API_KEY);
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {

            if (xml.length() > 10) { // Just checking if not empty

                try {
                    JSONObject jsonResponse = new JSONObject(xml);
                    JSONArray jsonArray = jsonResponse.optJSONArray("articles");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<>();
                        //map.put(KEY_AUTHOR, jsonObject.optString(KEY_AUTHOR));
                        map.put(KEY_TITLE, jsonObject.optString(KEY_TITLE));
                        map.put(KEY_DESCRIPTION, jsonObject.optString(KEY_DESCRIPTION));
                        map.put(KEY_URL, jsonObject.optString(KEY_URL));
                        //map.put(KEY_URLTOIMAGE, jsonObject.optString(KEY_URLTOIMAGE));
                        //map.put(KEY_PUBLISHEDAT, jsonObject.optString(KEY_PUBLISHEDAT));
                        //dataList.add(jsonObject.optString(KEY_TITLE));
                        dataList.add(map);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Unexpected error", Toast.LENGTH_SHORT).show();
                }


                BaseAdapter myAdapter = new MyOwnAdapter();
                listNews.setAdapter(myAdapter);

                listNews.setOnItemClickListener((parent, view, position, id) -> {
                    Log.e("you clicked on :", "item " + position);
                    //save the position in case this object gets deleted or updated
                    positionClicked = position;

                    Intent i = new Intent(MainActivity.this, ArticleActivity.class);
                    i.putExtra("url", dataList.get(position).get(KEY_URL));
                    i.putExtra("title", dataList.get(position).get(KEY_TITLE));
                    i.putExtra("description", dataList.get(position).get(KEY_DESCRIPTION));
                    startActivity(i);

                });
            }

            else {
                Toast.makeText(getApplicationContext(), "No news found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class MyOwnAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dataList.size();
        }

        public Object getItem(int position) {
            return dataList.indexOf(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int i, View view, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();

            View newView = inflater.inflate(R.layout.title_row, parent, false);

            TextView rowTitle = (TextView) newView.findViewById(R.id.title);
            TextView rowDes = (TextView) newView.findViewById(R.id.description);

            HashMap<String, String> data = new HashMap<String, String>();
            data = dataList.get(i);

            rowTitle.setText(data.get(MainActivity.KEY_TITLE));
            rowDes.setText(data.get(MainActivity.KEY_DESCRIPTION));

            //return the row:
            return newView;


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_search:
                // do something
                return true;
            case R.id.action_saved:
                // do something
                return true;

            case R.id.action_help:
                Intent myIntent = new Intent(this, Help.class);
                this.startActivity(myIntent);
                return true;

                default:
                return super.onContextItemSelected(item);
        }
    }
}






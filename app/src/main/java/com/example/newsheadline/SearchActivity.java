package com.example.newsheadline;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.SearchView;

public class SearchActivity extends AppCompatActivity {
    ListView listNews;
    ProgressBar loader;
    SharedPreferences prefs;
    String previous = "FileName";
    SearchManager searchManager;
    SearchView search;
    static String xml=null;
    int positionClicked = 0;
    BaseAdapter myAdapter;
    String KEYWORD="null";
    String API_KEY = "a65a65ef7a2f4c4c89a76a64790c4af9";
    String weburl = "https://newsapi.org/v2/everything?q=" +KEYWORD+"&from=2019-10-01&sortBy=publishedAt&apiKey=" + API_KEY;


    ArrayList<HashMap<String, String>> dataList = new ArrayList<>();

    static final String KEY_TITLE = "title";
    static final String KEY_DESCRIPTION = "description";
    static final String KEY_URL = "url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        listNews = findViewById(R.id.listNews_search);
        loader = findViewById(R.id.loader_search);
        listNews.setEmptyView(loader);
        prefs = getSharedPreferences(previous, MODE_PRIVATE);

        Intent intent = getIntent();
        //if (Intent.ACTION_SEARCH.equals(intent.getAction()))
        KEYWORD = intent.getStringExtra("query");

        weburl = "https://newsapi.org/v2/everything?q=" +KEYWORD+"&from=2019-10-01&sortBy=publishedAt&apiKey=" + API_KEY;

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

            String xml = Downloader.excuteGet(weburl);
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {

            if (xml.length() > 10) {

                try {
                    JSONObject jsonResponse = new JSONObject(xml);
                    JSONArray jsonArray = jsonResponse.optJSONArray("articles");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<>();

                        map.put(KEY_TITLE, jsonObject.optString(KEY_TITLE));
                        map.put(KEY_DESCRIPTION, jsonObject.optString(KEY_DESCRIPTION));
                        map.put(KEY_URL, jsonObject.optString(KEY_URL));
                        //map.put(KEY_URLTOIMAGE, jsonObject.optString(KEY_URLTOIMAGE));
                        //map.put(KEY_PUBLISHEDAT, jsonObject.optString(KEY_PUBLISHEDAT));
                        //dataList.add(jsonObject.optString(KEY_TITLE));
                        //map.put(KEY_AUTHOR, jsonObject.optString(KEY_AUTHOR));
                        dataList.add(map);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Unexpected error", Toast.LENGTH_SHORT).show();
                }


                myAdapter = new MyOwnAdapter();
                listNews.setAdapter(myAdapter);

                listNews.setOnItemClickListener((parent, view, position, id) -> {
                    Log.e("you clicked on :", "item " + position);
                    //save the position in case this object gets deleted or updated
                    positionClicked = position;

                    Intent i = new Intent(SearchActivity.this, ArticleActivity.class);
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


}


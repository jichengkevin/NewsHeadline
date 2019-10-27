package com.example.newsheadline;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ArticleActivity extends AppCompatActivity {
    WebView webView;
    ProgressBar loader;
    String url = "";
    String title = "";
    String description = "";
    MyDatabaseOpenHelper dbOpener;
    //SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        dbOpener = new MyDatabaseOpenHelper(this);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
        description = intent.getStringExtra("description");

        loader = findViewById(R.id.loader_article);
        webView = findViewById(R.id.webView);


        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        //webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);


        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                loader.setVisibility(View.VISIBLE);
                view.loadUrl(url);

                return true;
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
                loader.setVisibility(View.GONE);
            }
        });

        webView.loadUrl(url);
    }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_article, menu);
            return super.onCreateOptionsMenu(menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.action_save:
                    addData(title, description, url);
                    //SavedActivity.myAdapter.notifyDataSetChanged();
                    return true;

                case R.id.action_delete:
                    deleteData(title);
                    return true;

                case R.id.title_saved:
                    Intent saveIntent = new Intent(this, SavedActivity.class);
                    this.startActivity(saveIntent);
                    return true;

                default:
                    return super.onContextItemSelected(item);
            }
        }

        public void addData(String title, String description, String url) {
            boolean insertData = dbOpener.addData(title, description, url);
            if(insertData) {
                Toast.makeText(getApplicationContext(), "Data saved", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }

    public void deleteData(String title) {
        boolean deleteData = dbOpener.deleteData(title);
        if(deleteData) {
            Toast.makeText(getApplicationContext(), "Data deleted", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    }


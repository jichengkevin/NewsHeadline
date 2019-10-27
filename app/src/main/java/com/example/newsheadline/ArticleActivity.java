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
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        dbOpener = new MyDatabaseOpenHelper(this);
        db = dbOpener.getWritableDatabase();

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

                    //add to the database and get the new ID
                    ContentValues newRowValues = new ContentValues();
                    newRowValues.put(MyDatabaseOpenHelper.COL_TITLE, title);
                    newRowValues.put(MyDatabaseOpenHelper.COL_DESCRIPTION, description);
                    newRowValues.put(MyDatabaseOpenHelper.COL_URL, url);
                    //insert in the database:
                    long newId = db.insert(MyDatabaseOpenHelper.TABLE_NAME, null, newRowValues);

                    Toast.makeText(getApplicationContext(), "Article saved", Toast.LENGTH_LONG).show();
                    return true;

                case R.id.action_delete:
                    // do something
                    return true;

                case R.id.action_help2:
                    Intent myIntent = new Intent(this, Help.class);
                    this.startActivity(myIntent);
                    return true;

                default:
                    return super.onContextItemSelected(item);
            }
        }

    }


package com.example.newsheadline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SavedActivity extends AppCompatActivity {
    MyDatabaseOpenHelper mydb;
    BaseAdapter myAdapter;
    ListView listNews;
    int positionClicked=0;
    ArrayList<HashMap<String, String>> articleList = new ArrayList<>();
    ArrayList<String> titleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        listNews = findViewById(R.id.listNews_saved);

        //get a database:
        mydb = new MyDatabaseOpenHelper(this);
        SQLiteDatabase db = mydb.getWritableDatabase();

        //query all the results from the database:
        String[] columns = {MyDatabaseOpenHelper.COL_ID, MyDatabaseOpenHelper.COL_TITLE, MyDatabaseOpenHelper.COL_DESCRIPTION, MyDatabaseOpenHelper.COL_URL};
        Cursor results = db.query(false, MyDatabaseOpenHelper.TABLE_NAME, columns, null, null, null, null, null, null);

        //find the column indices:
        int titleColIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_TITLE);
        int descriptionColIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_DESCRIPTION);
        int idColIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_ID);
        int urlColIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_URL);

        //iterate over the results, return true if there is a next item:
        while (results.moveToNext()) {
            String title = results.getString(titleColIndex);
            String description = results.getString(descriptionColIndex);
            String url = results.getString(urlColIndex);
            HashMap<String, String> map = new HashMap<>();
            //map.put(KEY_AUTHOR, jsonObject.optString(KEY_AUTHOR));
            map.put(MainActivity.KEY_TITLE, title);
            map.put(MainActivity.KEY_DESCRIPTION, description);
            map.put(MainActivity.KEY_URL, url);
            //String email = results.getString(emailColumnIndex);
            //long id = results.getLong(idColIndex);

            //add the new Contact to the array list:
            articleList.add(map);
        }

        //create an adapter object and send it to the listVIew
        myAdapter = new ArticleAdapter();
        listNews.setAdapter(myAdapter);

        listNews.setOnItemClickListener((parent, view, position, id) -> {
            Log.e("you clicked on :", "item " + position);
            //save the position in case this object gets deleted or updated
            positionClicked = position;
            Intent i = new Intent(this, ArticleActivity.class);
            i.putExtra("url", articleList.get(position).get(MainActivity.KEY_URL));
            i.putExtra("title", articleList.get(position).get(MainActivity.KEY_TITLE));
            i.putExtra("description", articleList.get(position).get(MainActivity.KEY_DESCRIPTION));
            startActivity(i);

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_saved, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {

            case R.id.action_deleteAll:
                MyDatabaseOpenHelper dbOpener = new MyDatabaseOpenHelper(this);
                SQLiteDatabase db = dbOpener.getWritableDatabase();
                db.execSQL("delete from "+ MyDatabaseOpenHelper.TABLE_NAME);
                Toast.makeText(getApplicationContext(), "All Articles Deleted", Toast.LENGTH_LONG).show();
                myAdapter.notifyDataSetChanged();
                return true;

            case R.id.action_help2:
                Intent myIntent = new Intent(this, Help.class);
                this.startActivity(myIntent);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    public class ArticleAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return articleList.size();
        }

        public Object getItem(int position) {
            return articleList.indexOf(position);
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
            data = articleList.get(i);

            rowTitle.setText(data.get(MainActivity.KEY_TITLE));
            rowDes.setText(data.get(MainActivity.KEY_DESCRIPTION));

            //return the row:
            return newView;


        }

    }
}
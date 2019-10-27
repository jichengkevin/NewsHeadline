package com.example.newsheadline;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import com.example.newsheadline.MainActivity.MyOwnAdapter;
import android.widget.Adapter;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SavedActivity extends AppCompatActivity {
    MyDatabaseOpenHelper mydb;
    BaseAdapter  myAdapter;
    ArrayList<HashMap<String, String>> dataList = new ArrayList<>();
    ArrayList<String> titleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

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

        //iterate over the results, return true if there is a next item:
        while(results.moveToNext())
        {
            String title = results.getString(titleColumnIndex);
            //String email = results.getString(emailColumnIndex);
            //long id = results.getLong(idColIndex);

            //add the new Contact to the array list:
            titleList.add(title);
        }

        //create an adapter object and send it to the listVIew
        //myAdapter = new MainActivity.MyOwnAdapter();
        //titleList.setAdapter(myAdapter);


    }
}

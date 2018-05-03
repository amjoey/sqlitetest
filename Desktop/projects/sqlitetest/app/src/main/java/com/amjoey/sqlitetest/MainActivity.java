package com.amjoey.sqlitetest;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ListActivity {

    private EditText searchText;

    private Cursor cursor;
    private SimpleCursorAdapter adapter;

    DatabaseHandler mydb ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mydb = new DatabaseHandler(this);

        cursor = mydb.getAllRecord();
        adapter = new MyCursorAdapter(
                this,
                R.layout.row_layout,
                cursor,
                new String[] {"_id", "name", "age"},
                new int[] {R.id.id, R.id.name, R.id.age},
                0);
        setListAdapter(adapter);

        searchText = (EditText) findViewById (R.id.searchText);

        //register ListView for context menu in ListActivity class
        getListView().setAdapter(adapter);
        registerForContextMenu(getListView());
    }

    public void search(View view) {
/*
        Intent editFriend = new Intent(this, EditFriend.class);

        startActivity(editFriend);

 */
        Log.i("db", searchText.getText().toString());

        String search = searchText.getText().toString();
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);    // hide virtual kbd

        cursor = mydb.getSearchedRecord(search);
        adapter = new SimpleCursorAdapter(
                this,
                R.layout.row_layout,
                cursor,
                new String[] {"_id", "name", "age"},
                new int[] {R.id.id, R.id.name, R.id.age},
                0);

        setListAdapter(adapter);

    }

    public void onListItemClick(ListView parent, View view, int position, long id) {
        Intent intent = new Intent(this, ViewRecordActivity.class);
        Cursor cursor = (Cursor) adapter.getItem(position);
        intent.putExtra("recID", cursor.getInt(cursor.getColumnIndex("_id")));
        startActivity(intent);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
        /*
        super.onCreateOptionsMenu(menu);
        menu.add(0,
                0,
                0,
                "new menu");
        return true;
         */


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add:

                Intent addFriend = new Intent(this, AddFriend.class);
                startActivity(addFriend);

                return true;

              //  Toast.makeText(MainActivity.this,"Data Inserted",Toast.LENGTH_LONG).show();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_context, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Long id = getListAdapter().getItemId(info.position);


        switch (item.getItemId()) {
            case R.id.context_edit:
                //search();
                Intent intent = new Intent(this, EditFriend.class);
                Cursor cursor = (Cursor) adapter.getItem(id.intValue()-1);
                intent.putExtra("recID", cursor.getInt(cursor.getColumnIndex("_id")));
                startActivity(intent);
                return true;
            case R.id.context_delete:
                boolean isDeleted = mydb.deleteRecord(id.intValue());
                if(isDeleted == true)
                    Toast.makeText(this,"Data Deleted",Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(this,"Data not Deleted",Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    private class MyCursorAdapter extends SimpleCursorAdapter{

        private Cursor c;
        private Context context;
        private Bundle bundle;

        public MyCursorAdapter(Context context, int layout, Cursor c,
                               String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
            this.c = c;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            /*
            if(convertView == null){
                convertView = View.inflate(context, R.layout.row_layout, null);
            }
            View row = convertView;
            */


            //get reference to the row
            View view = super.getView(position, convertView, parent);

            this.c.moveToPosition(position);
            String name = this.c.getString(this.c.getColumnIndex("name"));

            TextView nameTextView = (TextView)view.findViewById(R.id.name);
            nameTextView.setText(timeformat(Integer.parseInt(name)));

            //check for odd or even to set alternate colors to the row background
            if(position % 2 == 0){
                view.setBackgroundColor(Color.rgb(238, 233, 233));
            }
            else {
                view.setBackgroundColor(Color.rgb(255, 255, 255));
            }
            return view;
        }

    }
    public static String timeformat(int t){
        String intTime;
        intTime =String.valueOf(Integer.toHexString(t));
        String first = padding(Integer.parseInt(intTime.substring(0, intTime.length() / 2)));
        String second = padding(Integer.parseInt(intTime.substring(intTime.length() / 2)));
        return first+ ":"  +second;
    }

    public static int timetoint(String s){
        int setTime;
        String[] separated = s.split(":");

        String first = separated[0];
        String second =separated[1];
        setTime =Integer.parseInt(first+second,16);
        return setTime;
    }

    public static String padding(int c){
        if(c>=10)
            return String.valueOf(c);
        else
            return "0"+ String.valueOf(c);
    }
}




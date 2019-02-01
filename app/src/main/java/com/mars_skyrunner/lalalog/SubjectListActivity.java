package com.mars_skyrunner.lalalog;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.mars_skyrunner.lalalog.data.SubjectContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * An activity representing a list of Subjects. This activity On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link SubjectDetailActivity} representing
 * item details.
 */
public class SubjectListActivity extends AppCompatActivity  implements
        LoaderManager.LoaderCallbacks<Cursor> {


    //LOG_TAG
    private String LOG_TAG = SubjectListActivity.class.getSimpleName();

    //List of Record objects in Record SQLite Database.
    List<Subject> subjects;
    
    //Cursor adapter to populate list of subjects
    public static SubjectAdapter mSubjectAdapter;

    //RecyblerView with list of subjects.
    public static ListView subjectListView;
    private ArrayList<Subject> subjectsArrayList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.subject_list_activity_menu, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.search_icon:
                onSearchRequested();//TODO: FIX FOUND SUBJECT LAYOUT, BOTH "DELETE" AND "EDIT" APPEAR AT THE SAME TIME
                break;

            case R.id.add_subject_icon:

                Bundle arguments = new Bundle();
                arguments.putString(Constants.ARG_ITEM_ID, "null");//This sets SubjectDetailActivitys EditionMode
                Intent intent = new Intent(this,SubjectDetailActivity.class);
                intent.putExtra(Constants.SUBJECT_BUNDLE,arguments);
                intent.putExtra(Constants.SUBJECT_URI_STRING,"null");//Subject to edit Uri
                intent.putExtra(Constants.SUBJECT_DETAIL_MODE, Constants.NEW_SUBJECT);
                this.startActivity(intent);

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(subjectDeleteReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        subjectListView = (ListView) findViewById(R.id.subject_list);
        assert subjectListView != null;

        //Register broadcast receiver to Toast SubjectDeleteServices' result
        registerReceiver(subjectDeleteReceiver, new IntentFilter(Constants.DELETE_SUBJECT_SERVICE_RESULT));


        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        subjectListView.setEmptyView(emptyView);

        ArrayList<Subject> subjects = getSubjectsArrayList();

        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        // There is no pet data yet (until the loader finishes) so pass in null for the Cursor.
        mSubjectAdapter = new SubjectAdapter(this,  subjects);
        subjectListView.setAdapter(mSubjectAdapter);


    }


    public static ArrayList<Subject> getSubjectsArrayList() {

        ArrayList<Subject> answer = new ArrayList<>();

        //TreeMap is used to sort SUBJECT_MAP hashmap keyset in order
        Map<String, Subject> treeMap = new TreeMap<String, Subject>(Constants.SUBJECT_MAP);

        Iterator iterator =  treeMap.values().iterator();

        while (iterator.hasNext()) {

            Subject subject = (Subject) iterator.next();

            answer.add(subject);

        }

        return answer;
    }

    //BroadcastReceiver to Toast SubjectDeleteServices' result
    private BroadcastReceiver subjectDeleteReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.v(LOG_TAG, "subjectDeleteReceiver: onReceive ");

            String result = intent.getStringExtra(Constants.RESULT);

            Log.v(LOG_TAG, "result: " + result);

            Toast.makeText(SubjectListActivity.this,result,Toast.LENGTH_SHORT).show();

            // Kick off the record loader
            getLoaderManager().restartLoader(Constants.SUBJECT_LOADER, null, SubjectListActivity.this);

        }


    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {

        switch (id){

            case Constants.SUBJECT_LOADER:
                // Define a projection that specifies the columns from the table we care about.
                String[] projection2 = {
                        SubjectContract.SubjectEntry._ID,    // Contract class constant for the _ID column name
                        SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME,
                        SubjectContract.SubjectEntry.COLUMN_SUBJECT_LASTNAME1,
                        SubjectContract.SubjectEntry.COLUMN_SUBJECT_LASTNAME2,
                        SubjectContract.SubjectEntry.COLUMN_UNIQUE_ID,
                        SubjectContract.SubjectEntry.COLUMN_SUBJECT_BIRTHDATE,
                        SubjectContract.SubjectEntry.COLUMN_SUBJECT_GROUP};

                String sortOrder2 = SubjectContract.SubjectEntry._ID;

                // This loader will execute the ContentProvider's query method on a background thread

                return new CursorLoader(this,   // Parent activity context
                        SubjectContract.SubjectEntry.CONTENT_URI,   // Provider content URI to query
                        projection2,             // Columns to include in the resulting Cursor
                        null,                   //  selection clause
                        null,                   //  selection arguments
                        sortOrder2);

        }


        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        switch (loader.getId()){
            case Constants.SUBJECT_LOADER:

                Constants.SUBJECT_MAP = new HashMap<String, Subject>();

//                // Proceed with moving to the first row of the cursor and reading data from it
//
                if (cursor.moveToFirst()) {

                    do {

                        // Find the columns of subject attributes that we're interested in
                        int idColumnIndex = cursor.getColumnIndex(SubjectContract.SubjectEntry._ID);
                        int uniqueIdColumnIndex = cursor.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_UNIQUE_ID);
                        int nameColumnIndex = cursor.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME);
                        int lastname1ColumnIndex = cursor.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_SUBJECT_LASTNAME1);
                        int lastname2ColumnIndex = cursor.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_SUBJECT_LASTNAME2);
                        int birthdateColumnIndex = cursor.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_SUBJECT_BIRTHDATE);
                        int groupColumnIndex = cursor.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_SUBJECT_GROUP);

                        // Extract out the value from the Cursor for the given column index
                        String name = cursor.getString(nameColumnIndex);
                        String lastname1 = cursor.getString(lastname1ColumnIndex);
                        String lastname2 = cursor.getString(lastname2ColumnIndex);
                        String birthdate = cursor.getString(birthdateColumnIndex);
                        String groupid = cursor.getString(groupColumnIndex);
                        String uniqueId = cursor.getString(uniqueIdColumnIndex);
                        String sId = cursor.getString(idColumnIndex);

                        Subject subject = new Subject(sId, name, lastname1, lastname2, birthdate, groupid);
                        subject.setSubjectUniqueID(Integer.parseInt(uniqueId.trim()));

                        Constants.SUBJECT_MAP.put(sId, subject);

                        Log.v(LOG_TAG, " SUBJECT_MAP size: " + Constants.SUBJECT_MAP.size());
                        Log.v(LOG_TAG, " SUBJECT_MAP.get(sId).toString(): " + Constants.SUBJECT_MAP.get(sId).toString());


                    } while (cursor.moveToNext());

                }

                ArrayList<Subject> subjects = getSubjectsArrayList();

                // Setup an Adapter to create a list item for each row of subject data in the Cursor.
                // There is no subject data yet (until the loader finishes) so pass in null for the Cursor.
                mSubjectAdapter = new SubjectAdapter(this,  subjects);
                subjectListView.setAdapter(mSubjectAdapter);

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

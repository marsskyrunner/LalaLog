package com.mars_skyrunner.lalalog;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.mars_skyrunner.lalalog.data.RecordContract;
import com.mars_skyrunner.lalalog.data.RecordContract.RecordEntry;
import com.mars_skyrunner.lalalog.data.SubjectContract.SubjectEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An activity representing a list of Records. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecordDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecordListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {


    //LOG_TAG
    private String LOG_TAG = RecordListActivity.class.getSimpleName();
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    public boolean mTwoPane;

    //List of Record objects in Record SQLite Database.
    List<Record> records;

    //Cursor adapter to populate list of records
    RecordCursorAdapter mRecordCursorAdapter;

    //RecyblerView with list of records.
    ListView recordListView;

    //Loader Views
    LinearLayout progressBar;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(recordDeleteReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.record_list_activity_menu, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.search_icon:

                onSearchRequested();
                break;

            case R.id.subject_icon:

                Intent intent = new Intent(this, SubjectListActivity.class);
                startActivity(intent);

                break;

        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.AppTheme_NoActionBar);

        setContentView(R.layout.activity_record_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.record_list_title));


        //Register broadcast receiver to Toast RecordDeleteServices' result

        registerReceiver(recordDeleteReceiver, new IntentFilter(Constants.DELETE_SERVICE_RESULT));

        progressBar = (LinearLayout) findViewById(R.id.progress_bar);
        recordListView = (ListView) findViewById(R.id.record_list);




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            // Create new intent to go to {@link RecordDetailActivity}
            Intent intent = new Intent(RecordListActivity.this, RecordDetailActivity.class);

            @Override
            public void onClick(View view) {

                Bundle arguments = new Bundle();
                arguments.putString(Constants.ARG_ITEM_ID, "null");


                if (mTwoPane) {
                    RecordDetailFragment fragment = new RecordDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.record_detail_container, fragment)
                            .commit();
                } else {
                    intent.putExtra(Constants.RECORD_BUNDLE, arguments);
                    intent.putExtra(Constants.RECORD_DETAIL_MODE, Constants.NEW_RECORD);
                    startActivity(intent);
                }
            }
        });


        assert recordListView != null;

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        recordListView.setEmptyView(emptyView);


        if (findViewById(R.id.record_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }


        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        // There is no pet data yet (until the loader finishes) so pass in null for the Cursor.
        mRecordCursorAdapter = new RecordCursorAdapter(this, null);
        recordListView.setAdapter(mRecordCursorAdapter);

        // Kick off the record loader
        getLoaderManager().initLoader(Constants.SUBJECT_LOADER, null, this);

    }



    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        showLoaderViews();

        switch (i) {

            case Constants.RECORD_LOADER:

                Log.v(LOG_TAG,"onCreateLoader RECORD_LOADER");

                // Define a projection that specifies the columns from the table we care about.
                String[] projection = {
                        RecordEntry._ID,
                        RecordEntry.COLUMN_SUBJECT_DISPLAY_NAME,
                        RecordEntry.COLUMN_SUBJECT_ID,
                        RecordEntry.COLUMN_SUBJECT_GROUP_ID,
                        RecordEntry.COLUMN_RECORD_TIME,
                        RecordEntry.COLUMN_RECORD_REFERENCE_ID,
                        RecordEntry.COLUMN_RECORD_TYPE,
                        RecordEntry.COLUMN_RECORD_DATE,
                        RecordEntry.COLUMN_RECORD_TEXT};

                // the selection will be "type=?" and the selection argument will be a
                // String array containing RecordEntry.CURRENT value in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.

                String selection = RecordEntry.COLUMN_RECORD_TYPE + "=?";
                String[] selectionArgs = new String[]{("" + RecordEntry.CURRENT)};
                String sortOrder = RecordEntry._ID;

                // This loader will execute the ContentProvider's query method on a background thread

                return new CursorLoader(this,   // Parent activity context
                        RecordEntry.CONTENT_URI,   // Provider content URI to query
                        projection,             // Columns to include in the resulting Cursor
                        selection,                   //  selection clause
                        selectionArgs,                   //  selection arguments
                        sortOrder);                  //  sort order


            case Constants.SUBJECT_LOADER:
                // Define a projection that specifies the columns from the table we care about.

                Log.v(LOG_TAG,"onCreateLoader SUBJECT_LOADER");

                String[] projection2 = {
                        SubjectEntry._ID,    // Contract class constant for the _ID column name
                        SubjectEntry.COLUMN_SUBJECT_NAME,
                        SubjectEntry.COLUMN_SUBJECT_LASTNAME1,
                        SubjectEntry.COLUMN_SUBJECT_LASTNAME2,
                        SubjectEntry.COLUMN_UNIQUE_ID,
                        SubjectEntry.COLUMN_SUBJECT_BIRTHDATE,
                        SubjectEntry.COLUMN_SUBJECT_GROUP};

                String sortOrder2 = SubjectEntry._ID;

                // This loader will execute the ContentProvider's query method on a background thread

                return new CursorLoader(this,   // Parent activity context
                        SubjectEntry.CONTENT_URI,   // Provider content URI to query
                        projection2,             // Columns to include in the resulting Cursor
                        null,                   //  selection clause
                        null,                   //  selection arguments
                        sortOrder2);                  //  sort order


        }

        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        switch (loader.getId()) {

            case Constants.SUBJECT_LOADER:

                Log.v(LOG_TAG,"onLoadFinished SUBJECT_LOADER");


                Constants.SUBJECT_MAP = new HashMap<String, Subject>();

//                // Proceed with moving to the first row of the cursor and reading data from it
//
                if (cursor.moveToFirst()) {

                    do {

                        // Find the columns of subject attributes that we're interested in
                        int idColumnIndex = cursor.getColumnIndex(SubjectEntry._ID);
                        int uniqueIdColumnIndex = cursor.getColumnIndex(SubjectEntry.COLUMN_UNIQUE_ID);
                        int nameColumnIndex = cursor.getColumnIndex(SubjectEntry.COLUMN_SUBJECT_NAME);
                        int lastname1ColumnIndex = cursor.getColumnIndex(SubjectEntry.COLUMN_SUBJECT_LASTNAME1);
                        int lastname2ColumnIndex = cursor.getColumnIndex(SubjectEntry.COLUMN_SUBJECT_LASTNAME2);
                        int birthdateColumnIndex = cursor.getColumnIndex(SubjectEntry.COLUMN_SUBJECT_BIRTHDATE);
                        int groupColumnIndex = cursor.getColumnIndex(SubjectEntry.COLUMN_SUBJECT_GROUP);

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


                // Kick off the record loader
                getLoaderManager().initLoader(Constants.RECORD_LOADER, null, this);

                break;


            case Constants.RECORD_LOADER:

                Log.v(LOG_TAG,"onLoadFinished RECORD_LOADER");

                hideLoaderViews();

                // Update {@link RecordCursorAdapter} with this new cursor containing updated record data
                mRecordCursorAdapter.swapCursor(cursor);

                Constants.RECORD_MAP = new HashMap<String, Record>();

//                // Proceed with moving to the first row of the cursor and reading data from it
//
                if (cursor.moveToFirst()) {

                    do {

                        // Find the columns of subject attributes that we're interested in
                        int idColumnIndex = cursor.getColumnIndex(RecordEntry._ID);
                        int textColumnIndex = cursor.getColumnIndex(RecordEntry.COLUMN_RECORD_TEXT);
                        int timeColumnIndex = cursor.getColumnIndex(RecordEntry.COLUMN_RECORD_TIME);
                        int dateColumnIndex = cursor.getColumnIndex(RecordEntry.COLUMN_RECORD_DATE);
                        int refColumnIndex = cursor.getColumnIndex(RecordEntry.COLUMN_RECORD_REFERENCE_ID);
                        int subjectIDColumnIndex = cursor.getColumnIndex(RecordEntry.COLUMN_SUBJECT_ID);

                        // Extract out the value from the Cursor for the given column index
                        String recordID = cursor.getString(idColumnIndex);
                        String ref = cursor.getString(refColumnIndex);
                        String text = cursor.getString(textColumnIndex);
                        String date = cursor.getString(dateColumnIndex);
                        String time = cursor.getString(timeColumnIndex);
                        String subjectID = cursor.getString(subjectIDColumnIndex);

                        Subject subject = getSubject(subjectID);

                        Record record = new Record(recordID, date, time, text, subject);
                        record.setRecordReference(ref);

                        Constants.RECORD_MAP.put(recordID, record);

                        Log.v(LOG_TAG, " Constants.RECORD_MAP size: " + Constants.RECORD_MAP.size());
                        Log.v(LOG_TAG, " Constants.RECORD_MAP.get(recordID).toString(): " + Constants.RECORD_MAP.get(recordID).toString());


                    } while (cursor.moveToNext());

                }


                break;
        }


    }


    private void showLoaderViews() {

        progressBar.setVisibility(View.VISIBLE);
        recordListView.setVisibility(View.GONE);

    }


    private void hideLoaderViews() {

        progressBar.setVisibility(View.GONE);
        recordListView.setVisibility(View.VISIBLE);

    }


    private Subject getSubject(String subjectID) {

        Subject subject = Constants.SUBJECT_MAP.get(subjectID);

        Log.v(LOG_TAG, "getSubject : " + subject.getSubjectName());

        return subject;

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mRecordCursorAdapter.swapCursor(null);
    }

    //BroadcastReceiver to Toast RecordDeleteServices' result
    private BroadcastReceiver recordDeleteReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.v(LOG_TAG, "recordDeleteReceiver: onReceive ");

            String result = intent.getStringExtra(Constants.RESULT);

            Log.v(LOG_TAG, "result: " + result);

            Toast.makeText(RecordListActivity.this,result,Toast.LENGTH_SHORT).show();

            // Kick off the record loader
            getLoaderManager().restartLoader(Constants.RECORD_LOADER, null, RecordListActivity.this);

        }


    };

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(LOG_TAG,"onResume()");
        // Kick off the record loader
        getLoaderManager().initLoader(Constants.SUBJECT_LOADER, null, this);
    }
}

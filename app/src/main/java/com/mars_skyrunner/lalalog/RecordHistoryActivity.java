package com.mars_skyrunner.lalalog;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.mars_skyrunner.lalalog.data.RecordContract;
import com.mars_skyrunner.lalalog.data.RecordContract.RecordEntry;
import com.mars_skyrunner.lalalog.data.SubjectContract.SubjectEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An activity representing a list of History Records. 
 */
public class RecordHistoryActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {


    //LOG_TAG
    private String LOG_TAG = RecordHistoryActivity.class.getSimpleName();

    //Cursor adapter to populate list of records
    RecordHistoryCursorAdapter mRecordHistoryCursorAdapter;

    //RecyblerView with list of records.
    ListView recordListView;

    //Record id from which history will be searched from
    String mRecordUri;

    //Record reference id
    String mRecordReferenceId;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpTo(this, new Intent(this, RecordDetailActivity.class));
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_history);

        recordListView = (ListView) findViewById(R.id.record_list);
        assert recordListView != null;

        mRecordUri = getIntent().getStringExtra(Constants.RECORD_URI);
        Log.v(LOG_TAG,"mRecordUri: " + mRecordUri);


        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        recordListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        // There is no pet data yet (until the loader finishes) so pass in null for the Cursor.
        mRecordHistoryCursorAdapter = new RecordHistoryCursorAdapter(this, null);
        recordListView.setAdapter(mRecordHistoryCursorAdapter);

        // Kick off the record loader
        getLoaderManager().initLoader(Constants.GET_RECORD_REF_LOADER, null, this);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {



        switch (i) {

            case Constants.RECORD_LOADER:
                // Define a projection that specifies the columns from the table we care about.
                String[] projection = {
                        RecordEntry._ID,
                        RecordEntry.COLUMN_SUBJECT_DISPLAY_NAME,
                        RecordEntry.COLUMN_SUBJECT_ID,
                        RecordEntry.COLUMN_SUBJECT_GROUP_ID,
                        RecordEntry.COLUMN_RECORD_TIME,
                        RecordEntry.COLUMN_RECORD_TYPE,
                        RecordEntry.COLUMN_RECORD_DATE,
                        RecordEntry.COLUMN_RECORD_TEXT};

                // the selection will be "type=?" and "record_reference = ?"and the selection argument will be a
                // String array containing RecordEntry.CURRENT value for the type query and the record ID taken from the record Uri .
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.

                String selection = RecordEntry.COLUMN_RECORD_REFERENCE_ID + " = " + mRecordReferenceId ;

                Log.v(LOG_TAG,"selection: " + selection);

                //Order query results by id
                String sortOrder = RecordEntry._ID;

                // This loader will execute the ContentProvider's query method on a background thread

                return new CursorLoader(this,   // Parent activity context
                        RecordEntry.CONTENT_URI,   // Provider content URI to query
                        projection,             // Columns to include in the resulting Cursor
                        selection,                   //  selection clause
                        null,                   //  no selection arguments
                        sortOrder);                  //  sort order

            case Constants.GET_RECORD_REF_LOADER:
                // Define a projection that specifies the columns from the table we care about.
                String[] projection2 = {
                        RecordEntry.COLUMN_RECORD_REFERENCE_ID};

                // the selection will be "type=?" and "record_reference = ?"and the selection argument will be a
                // String array containing RecordEntry.CURRENT value for the type query and the record ID taken from the record Uri .
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.

                // This loader will execute the ContentProvider's query method on a background thread

                return new CursorLoader(this,   // Parent activity context
                        Uri.parse(mRecordUri),   // Provider content URI to query
                        projection2,             // Columns to include in the resulting Cursor
                        null,                   //  selection clause
                        null,                   //  no selection arguments
                        null);                  //  sort order

        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        switch (loader.getId()) {

            case Constants.GET_RECORD_REF_LOADER:

                mRecordReferenceId = getReferenceIdFromCursor(cursor);

                // Kick off the record loader
                getLoaderManager().initLoader(Constants.RECORD_LOADER, null, this);


                break;

            case Constants.RECORD_LOADER:

                // Update {@link RecordHistoryCursorAdapter} with this new cursor containing updated record data
                mRecordHistoryCursorAdapter.swapCursor(cursor);

                break;
        }


    }

    private String getReferenceIdFromCursor(Cursor cursor) {
        String referenceID = "";

        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return "invalid cursor";
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)

        if (cursor.moveToFirst()) {
            // Find the columns of record reference id attribute that we're interested in
            int refIdColumnIndex = cursor.getColumnIndex(RecordEntry.COLUMN_RECORD_REFERENCE_ID);

            // Extract out the value from the Cursor for the given column index
        referenceID = cursor.getString(refIdColumnIndex);

        }

        return referenceID;
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mRecordHistoryCursorAdapter.swapCursor(null);
    }

}

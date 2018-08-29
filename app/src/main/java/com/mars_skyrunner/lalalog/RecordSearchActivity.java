package com.mars_skyrunner.lalalog;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.mars_skyrunner.lalalog.data.RecordContract;
import com.mars_skyrunner.lalalog.data.SubjectContract;

import java.text.NumberFormat;
import java.text.ParsePosition;

import static com.mars_skyrunner.lalalog.data.RecordContract.RecordEntry.COLUMN_RECORD_DATE;
import static com.mars_skyrunner.lalalog.data.RecordContract.RecordEntry.COLUMN_RECORD_REFERENCE_ID;
import static com.mars_skyrunner.lalalog.data.RecordContract.RecordEntry.COLUMN_RECORD_TEXT;
import static com.mars_skyrunner.lalalog.data.RecordContract.RecordEntry.COLUMN_RECORD_TIME;
import static com.mars_skyrunner.lalalog.data.RecordContract.RecordEntry.COLUMN_RECORD_TYPE;
import static com.mars_skyrunner.lalalog.data.RecordContract.RecordEntry.COLUMN_SUBJECT_DISPLAY_NAME;
import static com.mars_skyrunner.lalalog.data.RecordContract.RecordEntry.COLUMN_SUBJECT_GROUP_ID;
import static com.mars_skyrunner.lalalog.data.RecordContract.RecordEntry.COLUMN_SUBJECT_ID;
import static com.mars_skyrunner.lalalog.data.RecordContract.RecordEntry._ID;

public class RecordSearchActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private String LOG_TAG = RecordSearchActivity.class.getSimpleName();
    private final int DATE_QUERY = 100;
    private final int NAME_QUERY = 200;

    //query performed by user
    String searchQuery;

    //RecyblerView with list of records.
    ListView recordListView;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    //Cursor adapter to populate list of records
    RecordCursorAdapter mRecordCursorAdapter;

    //Loader Views
    LinearLayout progressBar;

    //Subject to search on query
    String searchSubjectID = "";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpTo(this, new Intent(this, RecordListActivity.class));
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        setTheme(R.style.AppTheme_NoActionBar);

        //Hides fab and toolbar
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressBar = (LinearLayout) findViewById(R.id.progress_bar);
        recordListView = (ListView) findViewById(R.id.record_list);

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

        // Setup the item click listener
        recordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // Create new intent to go to {@link RecordDetailActivity}
                Intent intent = new Intent(RecordSearchActivity.this, RecordDetailActivity.class);

                // Form the content URI that represents the specific record that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link RecordEntry#CONTENT_URI}.
                Uri currentRecordUri = ContentUris.withAppendedId(RecordContract.RecordEntry.CONTENT_URI, id);
                Bundle arguments = new Bundle();
                arguments.putString(Constants.ARG_ITEM_ID, currentRecordUri.toString());

                if (mTwoPane) {
                    RecordDetailFragment fragment = new RecordDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.record_detail_container, fragment)
                            .commit();
                } else {

                    intent.putExtra(Constants.RECORD_BUNDLE, arguments);
                    startActivity(intent);
                }

                // Set the URI on the data field of the intent
                intent.setData(currentRecordUri);

                // Launch the {@link EditorActivity} to display the data for the current pet.
                startActivity(intent);
            }
        });


        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchQuery = intent.getStringExtra(SearchManager.QUERY);

            // Kick off the record loader
            getLoaderManager().initLoader(Constants.SUBJECT_LOADER, null, this);
        }
    }


    private int getQueryFormat() {
        int answer = 0;

        //Date format required : DD / MMMMMM / YYYY
        //if the first and last character of query are numbers, then is a DATE_QUERY, else is a NAME_QUERY

        Log.v(LOG_TAG, "isNumeric(\"\" + query.charAt(0)): " + isNumeric("" + searchQuery.charAt(0)));
        Log.v(LOG_TAG, "isNumeric(\"\" + query.charAt(query.length() -1)): " + isNumeric("" + searchQuery.charAt(searchQuery.length() - 1)));

        if (isNumeric("" + searchQuery.charAt(0)) && isNumeric("" + searchQuery.charAt(searchQuery.length() - 1))) {
            answer = DATE_QUERY;
        } else {
            answer = NAME_QUERY;
        }

        return answer;
    }

    private boolean isNumeric(String str)

    {
        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(str, pos);

        return str.length() == pos.getIndex();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        showLoaderViews();

        String[] selectionArgs = null;

        switch (i) {

            case Constants.SUBJECT_LOADER:

                String selection = "";
                int queryFormat = getQueryFormat();


                switch (queryFormat) {
                    case DATE_QUERY:
                        Log.v(LOG_TAG, "DATE_QUERY");

                        selection =
                                SubjectContract.SubjectEntry.COLUMN_UNIQUE_ID + "=?";
                        selectionArgs = new String[]{
                                searchQuery};

                        break;

                    case NAME_QUERY:
                        Log.v(LOG_TAG, "NAME_QUERY");

                        selection =
                                SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME + "=?" + " or "  +
                                        SubjectContract.SubjectEntry.COLUMN_SUBJECT_LASTNAME1 + "=?" + " or "  +
                                        SubjectContract.SubjectEntry.COLUMN_SUBJECT_LASTNAME2 + "=?" ;

                        Log.v(LOG_TAG, "selection: " + selection);


                        selectionArgs = new String[]{
                                searchQuery,
                                searchQuery,
                                searchQuery};

                        break;

                }

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
                        selection,                   //  selection clause
                        selectionArgs,                   //  selection arguments
                        sortOrder2);                  //  sort order



            case Constants.RECORD_LOADER:

                // Define a projection that specifies the columns from the table we care about.
                String[] projection = {
                        _ID,
                        COLUMN_SUBJECT_DISPLAY_NAME,
                        COLUMN_SUBJECT_ID,
                        COLUMN_SUBJECT_GROUP_ID,
                        COLUMN_RECORD_TIME,
                        COLUMN_RECORD_REFERENCE_ID,
                        COLUMN_RECORD_TYPE,
                        COLUMN_RECORD_DATE,
                        COLUMN_RECORD_TEXT};

                // the selection will be "type=?" and the selection argument will be a
                // String array containing RecordEntry.CURRENT value in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.

                String sortOrder = _ID;
                String selection2 = COLUMN_SUBJECT_ID + "=?" + " and " + COLUMN_RECORD_TYPE + "=?";


                Log.v(LOG_TAG, "RECORD_LOADER selection2: " + selection2);

                selectionArgs = new String[]{
                        searchSubjectID, ("" + RecordContract.RecordEntry.CURRENT)};

                // This loader will execute the ContentProvider's query method on a background thread

                return new CursorLoader(this,   // Parent activity context
                        RecordContract.RecordEntry.CONTENT_URI,   // Provider content URI to query
                        projection,             // Columns to include in the resulting Cursor
                        selection2,                   //  selection clause
                        selectionArgs,                   //  selection arguments
                        sortOrder);                  //  sort order


        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {


//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


        hideLoaderViews();

        switch (loader.getId()) {

            case Constants.SUBJECT_LOADER:

//                // Proceed with moving to the first row of the cursor and reading data from it
//

                if (cursor.moveToFirst()) {

                    do {

                        // Find the columns of subject attributes that we're interested in
                        int idColumnIndex = cursor.getColumnIndex(SubjectContract.SubjectEntry._ID);

                        // Extract out the value from the Cursor for the given column index
                        searchSubjectID = cursor.getString(idColumnIndex);

                        Log.v(LOG_TAG,"SUBJECT_LOADER onLoadFinished searchSubjectID: " + searchSubjectID);

                    } while (cursor.moveToNext());

                }


                // Kick off the record loader
                getLoaderManager().initLoader(Constants.RECORD_LOADER, null, this);

                break;

            case Constants.RECORD_LOADER:

                // Update {@link RecordCursorAdapter} with this new cursor containing updated record data
                mRecordCursorAdapter.swapCursor(cursor);

                break;
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mRecordCursorAdapter.swapCursor(null);
    }

    private void showLoaderViews() {

        recordListView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

    }

    private void hideLoaderViews() {

        recordListView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

    }


}

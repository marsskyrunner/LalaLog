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

public class SubjectSearchActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private String LOG_TAG = SubjectSearchActivity.class.getSimpleName();
    private final int UNIQUE_ID_QUERY = 100;
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
    SubjectCursorAdapter mSubjectCursorAdapter;

    //Loader Views
    LinearLayout progressBar;

    //Subject to search on query
    String searchSubjectID = "";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:

                NavUtils.navigateUpTo(this, new Intent(this, SubjectListActivity.class));
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
        mSubjectCursorAdapter = new SubjectCursorAdapter(this, null);

        recordListView.setAdapter(mSubjectCursorAdapter);

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
        //if the first and last character of query are numbers, then is a UNIQUE_ID_QUERY, else is a NAME_QUERY

        Log.v(LOG_TAG, "isNumeric(\"\" + query.charAt(0)): " + isNumeric("" + searchQuery.charAt(0)));
        Log.v(LOG_TAG, "isNumeric(\"\" + query.charAt(query.length() -1)): " + isNumeric("" + searchQuery.charAt(searchQuery.length() - 1)));

        if (isNumeric("" + searchQuery.charAt(0)) && isNumeric("" + searchQuery.charAt(searchQuery.length() - 1))) {
            answer = UNIQUE_ID_QUERY;
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
                    case UNIQUE_ID_QUERY:
                        Log.v(LOG_TAG, "UNIQUE_ID_QUERY");

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


        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        hideLoaderViews();

        switch (loader.getId()) {

            case Constants.SUBJECT_LOADER:

                mSubjectCursorAdapter.swapCursor(cursor);

                break;

        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mSubjectCursorAdapter.swapCursor(null);
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

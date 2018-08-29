package com.mars_skyrunner.lalalog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import static com.mars_skyrunner.lalalog.RecordDetailFragment.mBirthdateDaySpinner;
import static com.mars_skyrunner.lalalog.RecordDetailFragment.mBirthdateMonthSpinner;
import static com.mars_skyrunner.lalalog.RecordDetailFragment.mBirthdateYearSpinner;
import static com.mars_skyrunner.lalalog.RecordDetailFragment.mGroupSpinner;
import static com.mars_skyrunner.lalalog.RecordDetailFragment.mUniqueIDAutoComplete;
import static com.mars_skyrunner.lalalog.RecordDetailFragment.mRecordEditText;
import static com.mars_skyrunner.lalalog.RecordDetailFragment.mNameEditText;
import static com.mars_skyrunner.lalalog.RecordDetailFragment.mLastName1EditText;
import static com.mars_skyrunner.lalalog.RecordDetailFragment.mLastName2EditText;




/**
 * An activity representing a single Record detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecordListActivity}.
 */
public class RecordDetailActivity extends AppCompatActivity {

    //Log tag
    private String LOG_TAG = RecordDetailActivity.class.getSimpleName();

    // recordUri received from RecordListActivitys' intent
    String recordUriStr ;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.record_detail_activity_menu, menu);

        return true;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);

        Log.w(LOG_TAG,"onCreate");


        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        Bundle args = getIntent().getBundleExtra(Constants.RECORD_BUNDLE);

        final FloatingActionButton editionFab = (FloatingActionButton) findViewById(R.id.edition_fab);
        final FloatingActionButton okFab = (FloatingActionButton) findViewById(R.id.ok_fab);

        editionFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editionFab.setVisibility(View.GONE);
                okFab.setVisibility(View.VISIBLE);

                Intent intent = new Intent(Constants.EDIT_RECORD);
                sendBroadcast(intent);

            }
        });

        okFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Constants.SAVE_RECORD);
                sendBroadcast(intent);

            }
        });

        recordUriStr = args.getString(Constants.ARG_ITEM_ID);
        Log.w(LOG_TAG,"recordUriStr: " + recordUriStr);

        if(!recordUriStr.equals("null")){//Existing Record selected
            editionFab.setVisibility(View.VISIBLE);
            okFab.setVisibility(View.GONE);
        }else{//New Record option selected
            invalidateOptionsMenu();
            editionFab.setVisibility(View.GONE);
            okFab.setVisibility(View.VISIBLE);
        }



        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            RecordDetailFragment fragment = new RecordDetailFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.record_detail_container, fragment)
                    .commit();
        }
    }


    @Override
    public void onBackPressed() {

        if(navigateUp()){
            super.onBackPressed();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:

                navigateUp();

                return true;

            case R.id.record_history:
                Intent intent = new Intent(this,RecordHistoryActivity.class);
                intent.putExtra(Constants.RECORD_URI,recordUriStr);
                startActivity(intent);
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    private boolean navigateUp() {

        // If the record hasn't changed, continue with navigating up to parent activity
        // which is the {@link CatalogActivity}.
        if (!checkEditionStatus()) {
           NavUtils.navigateUpTo(this, new Intent(this, RecordListActivity.class));
            return true;
        }


        // Show a dialog that notifies the user they have unsaved changes
        showUnsavedChangesDialog();
        return false;

    }

    private boolean checkEditionStatus() {

        String uniqueID = mUniqueIDAutoComplete.getText().toString();
        String name  = mNameEditText.getText().toString();
        String lastname1  = mLastName1EditText.getText().toString();
        String lastname2  = mLastName2EditText.getText().toString();
        String groupID  = getGroupID(mGroupSpinner.getSelectedItem().toString());
        String birthdateDay  = mBirthdateDaySpinner.getSelectedItem().toString();
        String birthdateMonth  = mBirthdateMonthSpinner.getSelectedItem().toString();
        String birthdateYear  = mBirthdateYearSpinner.getSelectedItem().toString();
        String birthdate = birthdateDay + " / " + birthdateMonth + " / " + birthdateYear;
        String recordText = mRecordEditText.getText().toString();

        Log.v(LOG_TAG,"checkEditionStatus()");

        Log.v(LOG_TAG,"groupID: " + groupID);
        Log.v(LOG_TAG,"birthdate: " + birthdate);

        Log.v(LOG_TAG,"TextUtils.isEmpty(uniqueID): " + TextUtils.isEmpty(uniqueID));
        Log.v(LOG_TAG,"TextUtils.isEmpty(name): " + TextUtils.isEmpty(name));
        Log.v(LOG_TAG,"TextUtils.isEmpty(lastname1): " + TextUtils.isEmpty(lastname1));
        Log.v(LOG_TAG,"TextUtils.isEmpty(lastname2): " + TextUtils.isEmpty(lastname2));
        Log.v(LOG_TAG,"TextUtils.isEmpty(recordText): " + TextUtils.isEmpty(recordText));
        Log.v(LOG_TAG,"groupID.equals(0): " + groupID.equals("0"));
        Log.v(LOG_TAG,"birthdate.equals(\"1 / Enero / 2000\"): " + (birthdate.equals("1 / Enero / 2000")));


        if (TextUtils.isEmpty(uniqueID)
                && TextUtils.isEmpty(name)
                && TextUtils.isEmpty(lastname1)
                && TextUtils.isEmpty(lastname2)
                && TextUtils.isEmpty(recordText)
                && groupID.equals("0")
                && birthdate.equals("1 / Enero / 2000")) {
            // Since no fields were modified, we can return early without creating a new pet.
            // No need to create ContentValues and no need to do any ContentProvider operations.


            Log.v(LOG_TAG,"return false");
            return false;

        }else{
            Log.v(LOG_TAG,"return true");
            return true;
        }

    }


    private String getGroupID(String groupString) {
        String groupID = "0";


        Log.v(LOG_TAG,"getGroupID: groupString: " + groupString);


        switch (groupString) {
            case "A":
                groupID = "0";
                break;

            case "B":
                groupID = "1";
                break;

            case "C":
                groupID = "2";
                break;

        }


        Log.v(LOG_TAG,"getGroupID: groupID: " + groupID);


        return groupID;
    }


    private void showUnsavedChangesDialog() {


        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that
        // changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, navigate to parent activity.
                        NavUtils.navigateUpTo(RecordDetailActivity.this, new Intent(RecordDetailActivity.this, RecordListActivity.class));
                    }
                };

        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

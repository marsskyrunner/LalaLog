package com.mars_skyrunner.lalalog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.mars_skyrunner.lalalog.data.RecordContract;

import java.util.Calendar;
import java.util.Date;

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
    String recordUriStr;

    //Current Date variables
    public static int currentYear,currentMonth,currentDay;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        String detailMode = getIntent().getStringExtra(Constants.RECORD_DETAIL_MODE);
        Log.v(LOG_TAG, "Record detailMode: " + detailMode);

        if(detailMode.equals(Constants.REVIEW_RECORD)){

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.record_detail_activity_menu, menu);

        }



        return true;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);

        Log.w(LOG_TAG, "onCreate");

        updateDate();


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
        Log.w(LOG_TAG, "recordUriStr: " + recordUriStr);


        if (!recordUriStr.equals("null")) {//Existing Record selected


            editionFab.setVisibility(View.VISIBLE);
            okFab.setVisibility(View.GONE);
        } else {//New Record option selected
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


    private void updateDate() {

        Date currentTime = Calendar.getInstance().getTime();
        String day = (String) DateFormat.format("dd", currentTime);
        String monthNumber = (String) DateFormat.format("MM", currentTime);
        String year = (String) DateFormat.format("yyyy", currentTime);

        currentYear = Integer.parseInt(year.trim());
        currentMonth = Integer.parseInt(monthNumber.trim());
        currentDay = Integer.parseInt(day.trim());

        Log.v(LOG_TAG, "currenYear: " + currentYear);
        Log.v(LOG_TAG, "currentMonth: " + currentMonth);
        Log.v(LOG_TAG, "currentDay: " + currentDay);

    }


    @Override
    public void onBackPressed() {

        if (navigateUp()) {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:

                navigateUp();

                return true;


            case R.id.record_history:
                Intent intent = new Intent(this, RecordHistoryActivity.class);
                intent.putExtra(Constants.RECORD_URI, recordUriStr);
                startActivity(intent);
                break;


            case R.id.delete_record:

                showDeleteConfirmation();


                break;


        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmation() {


        Log.v(LOG_TAG, "");

        // Create a click listener to handle the user confirming that
        // record should be deleted

        Log.v(LOG_TAG, "currentRecordUri: " + recordUriStr.toString());

        DialogInterface.OnClickListener deleteButtonListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "accept" button, navigate to parent activity.

                        Log.v(LOG_TAG, "deleteButtonListener");

                        //Kicks off RecordDeleteService
                        Intent deleteRecordIntent = new Intent(RecordDetailActivity.this, RecordDeleteService.class);
                        deleteRecordIntent.putExtra(Constants.DELETE_SERVICE_EXTRA, recordUriStr.toString());
                        startService(deleteRecordIntent);

                        Intent recordListIntent =  new Intent(RecordDetailActivity.this, RecordListActivity.class);
                        NavUtils.navigateUpTo(RecordDetailActivity.this,recordListIntent);



                    }
                };

        // Show a dialog that confirms user decision to delete record
        showDeleteConfirmDialog(deleteButtonListener);

    }

    private void showDeleteConfirmDialog(DialogInterface.OnClickListener deleteButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.

        AlertDialog.Builder builder = new AlertDialog.Builder(RecordDetailActivity.this);
        builder.setMessage(R.string.delete_record_dialog_msg);
        builder.setPositiveButton(R.string.accept, deleteButtonClickListener);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.

                Log.v(LOG_TAG, "setNegativeButton");

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

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
        String name = mNameEditText.getText().toString();
        String lastname1 = mLastName1EditText.getText().toString();
        String lastname2 = mLastName2EditText.getText().toString();
        String groupID = getGroupID(mGroupSpinner.getSelectedItem().toString());
        String birthdateDay = mBirthdateDaySpinner.getSelectedItem().toString();
        String birthdateMonth = mBirthdateMonthSpinner.getSelectedItem().toString();
        String birthdateYear = mBirthdateYearSpinner.getSelectedItem().toString();
        String birthdate = birthdateDay + " / " + birthdateMonth + " / " + birthdateYear;
        String recordText = mRecordEditText.getText().toString();

        Log.v(LOG_TAG, "checkEditionStatus()");

        Log.v(LOG_TAG, "groupID: " + groupID);
        Log.v(LOG_TAG, "birthdate: " + birthdate);

        Log.v(LOG_TAG, "TextUtils.isEmpty(uniqueID): " + TextUtils.isEmpty(uniqueID));
        Log.v(LOG_TAG, "TextUtils.isEmpty(name): " + TextUtils.isEmpty(name));
        Log.v(LOG_TAG, "TextUtils.isEmpty(lastname1): " + TextUtils.isEmpty(lastname1));
        Log.v(LOG_TAG, "TextUtils.isEmpty(lastname2): " + TextUtils.isEmpty(lastname2));
        Log.v(LOG_TAG, "TextUtils.isEmpty(recordText): " + TextUtils.isEmpty(recordText));
        Log.v(LOG_TAG, "groupID.equals(0): " + groupID.equals("0"));

        String minBirthdateYear = "1 / Enero / " + (currentYear - 18);
        Log.v(LOG_TAG, "minBirthdateYear: " + minBirthdateYear);




        if (TextUtils.isEmpty(uniqueID)
                && TextUtils.isEmpty(name)
                && TextUtils.isEmpty(lastname1)
                && TextUtils.isEmpty(lastname2)
                && TextUtils.isEmpty(recordText)
                && groupID.equals("0")
                && birthdate.equals(minBirthdateYear)) {
            // Since no fields were modified, we can return early without creating a new pet.
            // No need to create ContentValues and no need to do any ContentProvider operations.


            Log.v(LOG_TAG, "return false");
            return false;

        } else {
            Log.v(LOG_TAG, "return true");
            return true;
        }

    }


    private String getGroupID(String groupString) {
        String groupID = "0";


        Log.v(LOG_TAG, "getGroupID: groupString: " + groupString);


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


        Log.v(LOG_TAG, "getGroupID: groupID: " + groupID);


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

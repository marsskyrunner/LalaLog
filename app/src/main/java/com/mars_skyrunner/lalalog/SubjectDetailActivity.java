package com.mars_skyrunner.lalalog;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import static com.mars_skyrunner.lalalog.SubjectDetailFragment.mUniqueIDAutoComplete;
import static com.mars_skyrunner.lalalog.SubjectDetailFragment.mNameEditText;
import static com.mars_skyrunner.lalalog.SubjectDetailFragment.mLastName1EditText;
import static com.mars_skyrunner.lalalog.SubjectDetailFragment.mLastName2EditText;
import static com.mars_skyrunner.lalalog.SubjectDetailFragment.mBirthdateDaySpinner;
import static com.mars_skyrunner.lalalog.SubjectDetailFragment.mBirthdateMonthSpinner;
import static com.mars_skyrunner.lalalog.SubjectDetailFragment.mBirthdateYearSpinner;
import static com.mars_skyrunner.lalalog.SubjectDetailFragment.mGroupSpinner;

public class SubjectDetailActivity extends AppCompatActivity {


    private String LOG_TAG =  SubjectDetailActivity.class.getSimpleName();
    public static MenuItem addSubjectMenuItem,deleteSubjectMenuItem;


    public boolean onCreateOptionsMenu(Menu menu) {

        Bundle subjectBundle = getIntent().getBundleExtra(Constants.SUBJECT_BUNDLE);
        String subjectUriStr = subjectBundle.getString(Constants.ARG_ITEM_ID);



        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.subject_detail_activity_menu, menu);
        addSubjectMenuItem = menu.findItem(R.id.add_subject_icon);
        deleteSubjectMenuItem = menu.findItem(R.id.delete_subject);

        if(subjectUriStr.equals("null")){
            addSubjectMenuItem.setVisible(true);
            deleteSubjectMenuItem.setVisible(false);

        }else{
            addSubjectMenuItem.setVisible(false);
            deleteSubjectMenuItem.setVisible(true);
        }





        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:

                navigateUp();

                return true;

            case R.id.add_subject_icon:
                Intent intent = new Intent(Constants.SAVE_SUBJECT);
                sendBroadcast(intent);
                break;

            case R.id.delete_subject:

                //TODO : PROGRAM THIS
                Toast.makeText(this,"delete_subject",Toast.LENGTH_SHORT).show();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle subjectBundle = intent.getBundleExtra(Constants.SUBJECT_BUNDLE);
        String subjectToEditUri = intent.getStringExtra(Constants.SUBJECT_URI_STRING);


        String detailMode  = getIntent().getStringExtra(Constants.SUBJECT_DETAIL_MODE);
        Log.v(LOG_TAG,"detailMode: " + detailMode);

        switch (detailMode){

            case "com.mars_skyrunner.lalalog.NEW_SUBJECT":
                setTitle(getString(R.string.title_new_subject));
                break;

            case "com.mars_skyrunner.lalalog.EDIT_SUBJECT":
                setTitle(getString(R.string.title_edit_subject));
                break;


        }


        Log.v(LOG_TAG,"subjectToEditUri: " + subjectToEditUri);

        subjectBundle.putString(Constants.SUBJECT_URI_STRING,subjectToEditUri);

        // Create the detail fragment and add it to the activity
        // using a fragment transaction.
        SubjectDetailFragment fragment = new SubjectDetailFragment();
        fragment.setArguments(subjectBundle);
        this.getSupportFragmentManager().beginTransaction()
                .add(R.id.subject_detail_container, fragment)
                .commit();

    }

    private boolean navigateUp() {

        Log.v(LOG_TAG,"navigateUp()");
        Log.v(LOG_TAG,"!checkEditionStatus(): " + !checkEditionStatus());

        // If the record hasn't changed, continue with navigating up to parent activity
        // which is the {@link CatalogActivity}.
        if (!checkEditionStatus()) {

            Log.v(LOG_TAG,"NAVIGATE UP");
            NavUtils.navigateUpTo(this, new Intent(this, SubjectListActivity.class));
            return true;
        }else {
            Log.v(LOG_TAG,"STAY");
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that
        // changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, navigate to parent activity.
                        NavUtils.navigateUpTo(SubjectDetailActivity.this, new Intent(SubjectDetailActivity.this, SubjectListActivity.class));

                    }
                };

        // Show a dialog that notifies the user they have unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);

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


        Log.v(LOG_TAG,"checkEditionStatus()");

        Log.v(LOG_TAG,"groupID: " + groupID);
        Log.v(LOG_TAG,"birthdate: " + birthdate);

        Log.v(LOG_TAG,"TextUtils.isEmpty(uniqueID): " + TextUtils.isEmpty(uniqueID));
        Log.v(LOG_TAG,"TextUtils.isEmpty(name): " + TextUtils.isEmpty(name));
        Log.v(LOG_TAG,"TextUtils.isEmpty(lastname1): " + TextUtils.isEmpty(lastname1));
        Log.v(LOG_TAG,"TextUtils.isEmpty(lastname2): " + TextUtils.isEmpty(lastname2));
        Log.v(LOG_TAG,"groupID.equals(0): " + groupID.equals("0"));
        Log.v(LOG_TAG,"birthdate.equals(\"1 / Enero / 2000\"): " + (birthdate.equals("1 / Enero / 2000")));


        if (TextUtils.isEmpty(uniqueID)
                && TextUtils.isEmpty(name)
                && TextUtils.isEmpty(lastname1)
                && TextUtils.isEmpty(lastname2)
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

    private void showUnsavedChangesDialog(

            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.

        Log.v(LOG_TAG,"showUnsavedChangesDialog");

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


    @Override
    public void onBackPressed() {

        if(navigateUp()){
            super.onBackPressed();
        }

    }

}

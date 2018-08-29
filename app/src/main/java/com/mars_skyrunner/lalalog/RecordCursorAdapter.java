package com.mars_skyrunner.lalalog;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.transition.Scene;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.transition.TransitionManager;
import com.mars_skyrunner.lalalog.data.RecordContract;


/**
 * {@link RecordCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of record data as its data source. This adapter knows
 * how to create list items for each row of record data in the {@link Cursor}.
 */
public class RecordCursorAdapter extends CursorAdapter {

    private String LOG_TAG = RecordCursorAdapter.class.getSimpleName();
    Context mContext ;
    Subject mSubject;

    /**
     * Constructs a new {@link RecordCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */

    public RecordCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
        mContext = context;
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent ) {

        return LayoutInflater.from(context).inflate(R.layout.record_list_item, parent, false);
    }

    /**
     * This method binds the record data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current record can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        final Context mContext = context;

        // Find fields to populate in inflated template
        TextView mSubjectNameTextView = (TextView) view.findViewById(R.id.subject_name);
        ImageView mGroupImageView = (ImageView) view.findViewById(R.id.subject_group_imageview);
        TextView mRecordDate = (TextView) view.findViewById(R.id.record_date);
        TextView mRecordTime = (TextView) view.findViewById(R.id.record_time);
        final ImageButton mDeleteButton = (ImageButton) view.findViewById(R.id.delete_imagebtn);
        LinearLayout textLayout = view.findViewById(R.id.subject_info_layout);

        //Gets Column index of the name and breed of the record
        int idColumnIndex = cursor.getColumnIndex(RecordContract.RecordEntry._ID);
        int dateColumnIndex = cursor.getColumnIndex(RecordContract.RecordEntry.COLUMN_RECORD_DATE);
        int timeColumnIndex = cursor.getColumnIndex(RecordContract.RecordEntry.COLUMN_RECORD_TIME);
        int subjectGroupIDColumnIndex = cursor.getColumnIndex(RecordContract.RecordEntry.COLUMN_SUBJECT_GROUP_ID);
        int subjectIDColumnIndex = cursor.getColumnIndex(RecordContract.RecordEntry.COLUMN_SUBJECT_ID);

//        Log.v(LOG_TAG, "dateColumnIndex: " + dateColumnIndex);
//        Log.v(LOG_TAG, "timeColumnIndex: " + timeColumnIndex);
//        Log.v(LOG_TAG, "subjectGroupIDColumnIndex: " + subjectGroupIDColumnIndex);
//        Log.v(LOG_TAG, "subjectIDColumnIndex " + subjectIDColumnIndex);

        // Extract properties from cursoridColumnIndex
        final String recordID = cursor.getString(idColumnIndex);
        String recordDate = cursor.getString(dateColumnIndex);
        String recordTime = cursor.getString(timeColumnIndex);
        String subjectID = cursor.getString(subjectIDColumnIndex);
        String subjectGroupID = cursor.getString(subjectGroupIDColumnIndex);


        Log.v(LOG_TAG, "recordID: " + recordID);
        Log.v(LOG_TAG, "recordDate: " + recordDate);
        Log.v(LOG_TAG, "recordTime: " + recordTime);
        Log.v(LOG_TAG, "subjectGroupID: " + subjectGroupID);
        Log.v(LOG_TAG, "subjectID: " + subjectID);

        Log.v(LOG_TAG, "SUBJECT_MAP.size(): " + Constants.SUBJECT_MAP.size());

        mSubject = Constants.SUBJECT_MAP.get(subjectID);

        String subjectDisplayName = mSubject.getSubjectName() + " " + mSubject.getSubjectLastName1() + " " + mSubject.getSubjectLastName2();
        Log.v(LOG_TAG, "subjectDisplayName: " +subjectDisplayName);
        // If the record date is empty string or null, then use some default text
        // that says "00 / 00 / 00", so the TextView isn't blank.

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.v(LOG_TAG,"mDeleteButton.setOnClickListener");

                // Create a click listener to handle the user confirming that
                // record should be deleted

                Log.v(LOG_TAG,"deleteButtonListener");

                final Uri currentRecordUri = ContentUris.withAppendedId(RecordContract.RecordEntry.CONTENT_URI, Long.parseLong(recordID));

                Log.v(LOG_TAG,"currentRecordUri: " + currentRecordUri.toString());

                DialogInterface.OnClickListener deleteButtonListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "accept" button, navigate to parent activity.

                                Log.v(LOG_TAG,"deleteButtonListener");

                                //Kicks off RecordDeleteService
                                Intent deleteRecordIntent = new Intent(mContext, RecordDeleteService.class);
                                deleteRecordIntent.putExtra(Constants.DELETE_SERVICE_EXTRA, currentRecordUri.toString());
                                mContext.startService(deleteRecordIntent);

                            }
                        };

                // Show a dialog that confirms user decision to delete record
                showDeleteConfirmDialog(deleteButtonListener);


            }
        });


        // Setup the item click listener


        textLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.v(LOG_TAG,"onItemClick");

                // Create new intent to go to {@link RecordDetailActivity}
                Intent intent = new Intent(mContext, RecordDetailActivity.class);

                // Form the content URI that represents the specific record that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link RecordEntry#CONTENT_URI}.

                Uri currentRecordUri = ContentUris.withAppendedId(RecordContract.RecordEntry.CONTENT_URI, Long.parseLong(recordID));
                Bundle arguments = new Bundle();
                arguments.putString(Constants.ARG_ITEM_ID, currentRecordUri.toString());
                intent.putExtra(Constants.RECORD_BUNDLE, arguments);
                // Set the URI on the data field of the intent
                intent.setData(currentRecordUri);

                // Launch the {@link RecordDetailActivity} to display the data for the current subject.
                mContext.startActivity(intent);

               /*
                if (mTwoPane) {
                    RecordDetailFragment fragment = new RecordDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.record_detail_container, fragment)
                            .commit();
                } else {

                    intent.putExtra(Constants.RECORD_BUNDLE, arguments);
                    mContext.startActivity(intent);
                }
*/

            }
        });

        final CardView cardView = (CardView) view.findViewById(R.id.main_cardview);

        mGroupImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

                Log.v(LOG_TAG, "SDK_INT: " + Build.VERSION.SDK_INT);

                if (Build.VERSION.SDK_INT >= 26) {

                    vibrator.vibrate(( VibrationEffect.createOneShot(120,VibrationEffect.DEFAULT_AMPLITUDE) ));

                }else{

                    if (vibrator.hasVibrator()) {
                        vibrator.vibrate(120);
                    }

                }

                if(mDeleteButton.getVisibility() == View.GONE){
                    goScene2(cardView, mDeleteButton);
                }else{
                    goScene1(cardView, mDeleteButton);
                }

                return false;

            }
        });

        if (TextUtils.isEmpty(recordDate)) {
            recordDate = context.getString(R.string.unknown_date);
        }

        // If the record time is empty string or null, then use some default text
        // that says "00 hrs", so the TextView isn't blank.

        if (TextUtils.isEmpty(recordTime)) {
            recordTime = context.getString(R.string.unknown_time);
        }


        // Populate fields with extracted properties
        mRecordDate.setText(recordDate);
        mRecordTime.setText(recordTime);
        mSubjectNameTextView.setText(subjectDisplayName);
        mGroupImageView.setImageResource(getGroupResourceID(subjectGroupID));
    }

    public int getGroupResourceID(String subjectGroupID){
        int resourceID = 0;

        switch (subjectGroupID){
            case "0":
                resourceID = R.drawable.subject_group_1;
                break;

            case "1":
                resourceID = R.drawable.subject_group_2;
                break;

            case "2":
                resourceID = R.drawable.subject_group_3;
                break;
        }


        return  resourceID;
    }

    private void  goScene2(CardView cardView , ImageButton deleteButton) {


        if (Build.VERSION.SDK_INT >= 19) {

            Log.v(LOG_TAG,"goScene2: Build.VERSION.SDK_INT >= 19");

            TransitionManager.beginDelayedTransition(cardView);

        }else{
            Log.v(LOG_TAG,"goScene2: Build.VERSION.SDK_INT < 19");
        }

        deleteButton.setVisibility(View.VISIBLE);
        cardView.findViewById(R.id.record_time).setVisibility(View.GONE);

    }

    private void goScene1(CardView cardView , ImageButton deleteButton) {


        Log.v(LOG_TAG,"cambio");
        
        if (Build.VERSION.SDK_INT >= 19) {

            Log.v(LOG_TAG,"goScene1: Build.VERSION.SDK_INT >= 19");

            TransitionManager.beginDelayedTransition(cardView);

        }else{

            Log.v(LOG_TAG,"goScene1: Build.VERSION.SDK_INT < 19");

        }


        deleteButton.setVisibility(View.GONE);
        cardView.findViewById(R.id.record_time).setVisibility(View.VISIBLE);

    }

    /**
     * Perform the deletion of the Record in the database.
     */
    private void deleteRecord() {



    }

    private void showDeleteConfirmDialog(

            DialogInterface.OnClickListener deleteButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.delete_record_dialog_msg);
        builder.setPositiveButton(R.string.accept, deleteButtonClickListener);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.

                Log.v(LOG_TAG,"setNegativeButton");

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
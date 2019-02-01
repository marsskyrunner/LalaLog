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
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mars_skyrunner.lalalog.data.RecordContract;
import com.mars_skyrunner.lalalog.data.SubjectContract;

import static com.mars_skyrunner.lalalog.data.SubjectContract.SubjectEntry.COLUMN_SUBJECT_GROUP;
import static com.mars_skyrunner.lalalog.data.SubjectContract.SubjectEntry.COLUMN_SUBJECT_LASTNAME1;
import static com.mars_skyrunner.lalalog.data.SubjectContract.SubjectEntry.COLUMN_SUBJECT_LASTNAME2;
import static com.mars_skyrunner.lalalog.data.SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME;
import static com.mars_skyrunner.lalalog.data.SubjectContract.SubjectEntry.COLUMN_UNIQUE_ID;
import static com.mars_skyrunner.lalalog.data.SubjectContract.SubjectEntry._ID;

/**
 * {@link SubjectCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of record data as its data source. This adapter knows
 * how to create list items for each row of record data in the {@link Cursor}.
 */
public class SubjectCursorAdapter extends CursorAdapter {

    private String LOG_TAG = SubjectCursorAdapter.class.getSimpleName();

    /**
     * Constructs a new {@link SubjectCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */

    Context mContext;
    View listItemView;

    public SubjectCursorAdapter(Context context, Cursor c) {
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

        listItemView =LayoutInflater.from(context).inflate(R.layout.subject_list_item, parent, false);

        return listItemView;
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

        // Find fields to populate in inflated template
        TextView mSubjectNameTextView = (TextView) listItemView.findViewById(R.id.subject_name);
        ImageView mGroupImageView = (ImageView) listItemView.findViewById(R.id.subject_group_imageview);
        TextView mSubjectIdTextView = (TextView) listItemView.findViewById(R.id.subject_unique_id);
        final ImageButton mEditButton = (ImageButton) listItemView.findViewById(R.id.edit_button);
        final ImageButton mDeleteButton = (ImageButton) listItemView.findViewById(R.id.delete_imagebtn);
        LinearLayout infoLayout = (LinearLayout) listItemView.findViewById(R.id.subject_info_layout);
        final LinearLayout container = (LinearLayout) listItemView.findViewById(R.id.container);


        //Gets Column index of the name and breed of the record
        int nameColumnIndex = cursor.getColumnIndex(COLUMN_SUBJECT_NAME);
        int lastname1ColumnIndex = cursor.getColumnIndex(COLUMN_SUBJECT_LASTNAME1);
        int lastname2ColumnIndex = cursor.getColumnIndex(COLUMN_SUBJECT_LASTNAME2);
        int uniqueIDColumnIndex = cursor.getColumnIndex(COLUMN_UNIQUE_ID);
        int subjectIDColumnIndex = cursor.getColumnIndex(_ID);
        int subjectGroupIDColumnIndex = cursor.getColumnIndex(COLUMN_SUBJECT_GROUP);


        Log.v(LOG_TAG, "nameColumnIndex: " + nameColumnIndex);
        Log.v(LOG_TAG, "lastname1ColumnIndex: " + lastname1ColumnIndex);
        Log.v(LOG_TAG, "lastname2ColumnIndex: " + lastname2ColumnIndex);
        Log.v(LOG_TAG, "uniqueIDColumnIndex " + uniqueIDColumnIndex);
        Log.v(LOG_TAG, "subjectIDColumnIndex " + subjectIDColumnIndex);
        Log.v(LOG_TAG, "subjectGroupIDColumnIndex " + subjectGroupIDColumnIndex);


        // Extract properties from cursor
        String name = cursor.getString(nameColumnIndex);
        String lastname1 = cursor.getString(lastname1ColumnIndex);
        String lastname2 = cursor.getString(lastname2ColumnIndex);
        String uniqueID = cursor.getString(uniqueIDColumnIndex);
        String subjectID = cursor.getString(subjectIDColumnIndex);
        String subjectGroupID = cursor.getString(subjectGroupIDColumnIndex);


        Log.v(LOG_TAG, "name: " + name);
        Log.v(LOG_TAG, "lastname1: " + lastname1);
        Log.v(LOG_TAG, "lastname2: " + lastname2);
        Log.v(LOG_TAG, "uniqueID: " + uniqueID);
        Log.v(LOG_TAG, "subjectID: " + subjectID);
        Log.v(LOG_TAG, "subjectGroupID: " + subjectGroupID);

        Subject currentSubject = Constants.SUBJECT_MAP.get(subjectID);

        //Populate fields
        String displayName = currentSubject.getSubjectName() + " " + currentSubject.getSubjectLastName1() + " " + currentSubject.getSubjectLastName2();

        Uri currentSubjectUri = ContentUris.withAppendedId(SubjectContract.SubjectEntry.CONTENT_URI, Long.parseLong(currentSubject.getSubjectID().trim()));
        final String subjectUriStr = currentSubjectUri.toString();
        Log.v(LOG_TAG, "subjectUriStr: " + subjectUriStr);


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
                    goScene2(container, mDeleteButton, mEditButton);
                }else{
                    goScene1(container, mDeleteButton, mEditButton);
                }

                return false;
            }
        });


        mDeleteButton.setVisibility(View.GONE);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.v(LOG_TAG,"deleteButtonListener");

                DialogInterface.OnClickListener deleteButtonListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "accept" button, navigate to parent activity.

                                Log.v(LOG_TAG,"deleteButtonListener");


                                //Kicks off SubjectDeleteService
                                Intent deleteSubjectIntent = new Intent(mContext, SubjectDeleteService.class);
                                deleteSubjectIntent.putExtra(Constants.DELETE_SERVICE_EXTRA, subjectUriStr);
                                mContext.startService(deleteSubjectIntent);;

                            }
                        };

                // Show a dialog that confirms user decision to delete record
                showDeleteConfirmDialog(deleteButtonListener);

            }
        });

        mSubjectNameTextView.setText(displayName);
        mGroupImageView.setImageResource(currentSubject.getGroupResourceID());
        mSubjectIdTextView.setText("" + currentSubject.getSubjectUniqueID());
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.v(LOG_TAG, "mEditButton.setOnClickListener");

                Bundle arguments = new Bundle();
                arguments.putString(Constants.ARG_ITEM_ID, "null");//This sets SubjectDetailActivitys EditionMode

                Intent intent = new Intent(mContext,SubjectDetailActivity.class);
                intent.putExtra(Constants.SUBJECT_BUNDLE,arguments);
                intent.putExtra(Constants.SUBJECT_URI_STRING,subjectUriStr);//Subject to edit Uri
                intent.putExtra(Constants.SUBJECT_DETAIL_MODE, Constants.EDIT_SUBJECT);
                mContext.startActivity(intent);

            }
        });



        infoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.v(LOG_TAG, "mainLayout.setOnClickListener");

                Bundle arguments = new Bundle();
                arguments.putString(Constants.ARG_ITEM_ID, subjectUriStr);//This sets SubjectDetailActivitys ReviewMode

                Intent intent = new Intent(mContext,SubjectDetailActivity.class);
                intent.putExtra(Constants.SUBJECT_BUNDLE,arguments);
                intent.putExtra(Constants.SUBJECT_URI_STRING,subjectUriStr);
                intent.putExtra(Constants.SUBJECT_DETAIL_MODE, Constants.REVIEW_SUBJECT);
                mContext.startActivity(intent);

            }
        });



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


    private void  goScene2(LinearLayout container , ImageButton deleteButton , ImageButton editButton) {


        if (Build.VERSION.SDK_INT >= 19) {

            Log.v(LOG_TAG,"goScene2: Build.VERSION.SDK_INT >= 19");

            TransitionManager.beginDelayedTransition(container);

        }else{
            Log.v(LOG_TAG,"goScene2: Build.VERSION.SDK_INT < 19");
        }

        editButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.VISIBLE);


    }

    private void goScene1(LinearLayout container , ImageButton deleteButton , ImageButton editButton) {


        if (Build.VERSION.SDK_INT >= 19) {

            Log.v(LOG_TAG,"goScene1: Build.VERSION.SDK_INT >= 19");

            TransitionManager.beginDelayedTransition(container);

        }else{

            Log.v(LOG_TAG,"goScene1: Build.VERSION.SDK_INT < 19");

        }


        deleteButton.setVisibility(View.GONE);
        editButton.setVisibility(View.VISIBLE);

    }


    private void showDeleteConfirmDialog(

            DialogInterface.OnClickListener deleteButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.delete_subject_dialog_msg);
        builder.setPositiveButton(R.string.accept, deleteButtonClickListener);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                Log.v(LOG_TAG,"setNegativeButton");

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();

        Log.v(LOG_TAG,"builder.create()");

        alertDialog.show();

        Log.v(LOG_TAG,"alertDialog.show()");
    }


}

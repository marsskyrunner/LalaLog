package com.mars_skyrunner.lalalog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mars_skyrunner.lalalog.data.SubjectContract;

import java.util.ArrayList;

/**
 * Created by marcial on 26/07/2017.
 */

public class SubjectAdapter extends ArrayAdapter<Subject> {

    /*
     * {@link SubjectAdapter} is an {@link ArrayAdapter} that can provide the laprofilePic_blob
     * */

    private String LOG_TAG = SubjectAdapter.class.getSimpleName();

    Activity mActivity;
    Bundle mArgs;
    Context mContext;


    public SubjectAdapter(Activity activity, ArrayList<Subject> subjects) {

        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.

        super(activity, 0, subjects);

        mActivity = activity;
        mContext =  mActivity.getBaseContext();

    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position The position in the list of data that should be displayed in the
     *                 list item view.
     * @param convertView The recycled view to populate.
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view

        View listItemView = convertView;

        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.subject_list_item, parent, false);
        }

        // Get the {@link currentSubject} object located at this position in the list
        final Subject currentSubject = getItem(position);

        // Find fields to populate in inflated template
        TextView mSubjectNameTextView = (TextView) listItemView.findViewById(R.id.subject_name);
        ImageView mGroupImageView = (ImageView) listItemView.findViewById(R.id.subject_group_imageview);
        TextView mSubjectIdTextView = (TextView) listItemView.findViewById(R.id.subject_unique_id);
        final ImageButton mEditButton = (ImageButton) listItemView.findViewById(R.id.edit_button);
        final ImageButton mDeleteButton = (ImageButton) listItemView.findViewById(R.id.delete_imagebtn);
        LinearLayout infoLayout = (LinearLayout) listItemView.findViewById(R.id.subject_info_layout);
        final LinearLayout container = (LinearLayout) listItemView.findViewById(R.id.container);


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

                Intent intent = new Intent(mActivity,SubjectDetailActivity.class);
                intent.putExtra(Constants.SUBJECT_BUNDLE,arguments);
                intent.putExtra(Constants.SUBJECT_URI_STRING,subjectUriStr);//Subject to edit Uri
                intent.putExtra(Constants.SUBJECT_DETAIL_MODE, Constants.EDIT_SUBJECT);
                mActivity.startActivity(intent);

            }
        });



        infoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.v(LOG_TAG, "mainLayout.setOnClickListener");

                Bundle arguments = new Bundle();
                arguments.putString(Constants.ARG_ITEM_ID, subjectUriStr);//This sets SubjectDetailActivitys ReviewMode

                Intent intent = new Intent(mActivity,SubjectDetailActivity.class);
                intent.putExtra(Constants.SUBJECT_BUNDLE,arguments);
                intent.putExtra(Constants.SUBJECT_URI_STRING,"null");
                intent.putExtra(Constants.SUBJECT_DETAIL_MODE, Constants.REVIEW_SUBJECT);
                mActivity.startActivity(intent);

            }
        });

        // Return the whole list item layout (containing 2 TextViews and an ImageView)
        // so that it can be shown in the ListView
        return listItemView;
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

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
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

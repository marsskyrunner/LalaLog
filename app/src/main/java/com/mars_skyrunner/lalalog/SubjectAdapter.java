package com.mars_skyrunner.lalalog;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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


    public SubjectAdapter(Activity activity, ArrayList<Subject> subjects) {

        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.

        super(activity, 0, subjects);

        mActivity = activity;

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
        ImageButton mEditButton = (ImageButton) listItemView.findViewById(R.id.edit_button);

        //Populate fields
        String displayName = currentSubject.getSubjectName() + " " + currentSubject.getSubjectLastName1() + " " + currentSubject.getSubjectLastName2();

        Uri currentSubjectUri = ContentUris.withAppendedId(SubjectContract.SubjectEntry.CONTENT_URI, Long.parseLong(currentSubject.getSubjectID().trim()));
        final String subjectUriStr = currentSubjectUri.toString();
        Log.v(LOG_TAG, "subjectUriStr: " + subjectUriStr);

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

                mActivity.startActivity(intent);

            }
        });



        CardView mainLayout = (CardView) listItemView.findViewById(R.id.main_cardview);

        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.v(LOG_TAG, "mainLayout.setOnClickListener");

                Bundle arguments = new Bundle();
                arguments.putString(Constants.ARG_ITEM_ID, subjectUriStr);//This sets SubjectDetailActivitys ReviewMode

                Intent intent = new Intent(mActivity,SubjectDetailActivity.class);
                intent.putExtra(Constants.SUBJECT_BUNDLE,arguments);
                intent.putExtra(Constants.SUBJECT_URI_STRING,"null");

                mActivity.startActivity(intent);

            }
        });

        // Return the whole list item layout (containing 2 TextViews and an ImageView)
        // so that it can be shown in the ListView
        return listItemView;
    }


}

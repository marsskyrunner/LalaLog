package com.mars_skyrunner.lalalog;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mars_skyrunner.lalalog.data.RecordContract;

/**
 * {@link RecordHistoryCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of record data as its data source. This adapter knows
 * how to create list items for each row of record data in the {@link Cursor}.
 */
public class RecordHistoryCursorAdapter extends CursorAdapter {

    private String LOG_TAG = RecordHistoryCursorAdapter.class.getSimpleName();

    /**
     * Constructs a new {@link RecordHistoryCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */

    public RecordHistoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);

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

        return LayoutInflater.from(context).inflate(R.layout.record_history_list_item, parent, false);
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

        Log.v(LOG_TAG,"cursor.getPosition(): " + cursor.getPosition());
        Log.v(LOG_TAG,"(cursor.getCount() - 1): " + (cursor.getCount() - 1));

        //If it is the last row element in cursor, set background background color to PrimaryColorLight

        if(cursor.getPosition() == (cursor.getCount() - 1)){

            Log.v(LOG_TAG,"Last cursor row element ");
            ((CardView) view.findViewById(R.id.main_cardview)).setBackgroundColor(context.getResources().getColor(R.color.colorPrimarySuperLight));

        }else{

            ((CardView) view.findViewById(R.id.main_cardview)).setBackgroundColor(context.getResources().getColor(R.color.white));

        }

        // Find fields to populate in inflated template
        TextView mRecordText = (TextView) view.findViewById(R.id.record_text);
        TextView mRecordDate = (TextView) view.findViewById(R.id.record_date);
        TextView mRecordTime = (TextView) view.findViewById(R.id.record_time);

        //Gets Column index of the name and breed of the record
        int dateColumnIndex = cursor.getColumnIndex(RecordContract.RecordEntry.COLUMN_RECORD_DATE);
        int timeColumnIndex = cursor.getColumnIndex(RecordContract.RecordEntry.COLUMN_RECORD_TIME);
        int recordTextColumnIndex = cursor.getColumnIndex(RecordContract.RecordEntry.COLUMN_RECORD_TEXT);

        // Extract properties from cursor
        String recordDate = cursor.getString(dateColumnIndex);
        String recordTime = cursor.getString(timeColumnIndex);
        String recordText = cursor.getString(recordTextColumnIndex);

        Log.v(LOG_TAG,"recordText: " + recordText);

        // If the record date is empty string or null, then use some default text
        // that says "00 / 00 / 00", so the TextView isn't blank.

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
        mRecordText.setText(recordText);
    }

}
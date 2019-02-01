package com.mars_skyrunner.lalalog;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.mars_skyrunner.lalalog.data.RecordContract;

public class SubjectDeleteService extends IntentService {


    private final String LOG_TAG = SubjectDeleteService.class.getSimpleName();
    Context mContext = SubjectDeleteService.this;

    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */

    int extra = 0 ;

    public SubjectDeleteService() {
        super("SubjectDeleteService");

        Log.v(LOG_TAG,"SubjectDeleteService() constructor");
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        Log.v(LOG_TAG,"onHandleIntent"  );

        String subjectUriStr = intent.getStringExtra(Constants.DELETE_SERVICE_EXTRA);
        Uri mCurrentSubjectUri = Uri.parse(subjectUriStr);
        String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(mCurrentSubjectUri))};

        Log.v(LOG_TAG,"mCurrentSubjectUri: " + mCurrentSubjectUri.toString());
        Log.v(LOG_TAG,"selectionArgs: " + selectionArgs.toString());

        // Defines a variable to contain the number of rows deleted
        int mRowsDeleted = 0;

        //BORRA REGISTROS DEL ALUMNO

        // Defines a variable to contain the number of rows deleted
        mRowsDeleted = 0;

        // Deletes the pets that match the selection criteria
        mRowsDeleted = mContext.getContentResolver().delete(
                RecordContract.RecordEntry.CONTENT_URI ,   // the pet content URI
                RecordContract.RecordEntry.COLUMN_SUBJECT_ID + "=?",                    // the column to select on
                selectionArgs                      // the value to compare to
        );


        String deleteResult = "NULL";

        Log.v(LOG_TAG,"mRowsDeleted: " + mRowsDeleted  );


        // Show a toast message depending on whether or not the deleting was successful.
        if (mRowsDeleted == 0) {
            // If no rows were affected, then there was an error with the deleting.
            deleteResult = getString(R.string.delete_record_failed);
        } else {
            // Otherwise, the deleting was successful and we can display a toast.
            deleteResult = getString(R.string.delete_record_succes);
        }


        Log.v(LOG_TAG,"mRowsDeleted: " + mRowsDeleted  );

        Log.v(LOG_TAG,"deleteResult RECORDS: " + deleteResult  );
        //BORRA ALUMNO


        // Deletes the pets that match the selection criteria
        mRowsDeleted = mContext.getContentResolver().delete(
                mCurrentSubjectUri,   // the pet content URI
                null,                    // the column to select on
                null                      // the value to compare to
        );

        deleteResult = "NULL";

        // Show a toast message depending on whether or not the deleting was successful.
        if (mRowsDeleted == 0) {
            // If no rows were affected, then there was an error with the deleting.
            deleteResult = getString(R.string.delete_subject_failed);
        } else {
            // Otherwise, the deleting was successful and we can display a toast.
            deleteResult = getString(R.string.delete_subject_succes);
        }
        Log.v(LOG_TAG,"deleteResult SUBJECT: " + deleteResult  );



        Intent resultIntent = new Intent(Constants.DELETE_SUBJECT_SERVICE_RESULT);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        resultIntent.putExtra(Constants.RESULT,deleteResult);
        mContext.sendBroadcast(resultIntent);

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "service " + extra + " starting", Toast.LENGTH_SHORT).show();
        Log.v(LOG_TAG,"onStartCommand"  );

        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "service" + extra + " destroy", Toast.LENGTH_SHORT).show();
        Log.v(LOG_TAG,"onDestroy"  );

    }
}

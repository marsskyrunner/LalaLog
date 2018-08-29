package com.mars_skyrunner.lalalog;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class RecordDeleteService extends IntentService {


    private final String LOG_TAG = RecordDeleteService.class.getSimpleName();
    Context mContext = RecordDeleteService.this;

    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */

    int extra = 0 ;

    public RecordDeleteService() {
        super("RecordDeleteService");

        Log.v(LOG_TAG,"RecordDeleteService() constructor");
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        Log.v(LOG_TAG,"onHandleIntent"  );

        String recordUriStr = intent.getStringExtra(Constants.DELETE_SERVICE_EXTRA);
        Uri mCurrentRecordUri = Uri.parse(recordUriStr);

        Log.v(LOG_TAG,"mCurrentRecordUri: " + mCurrentRecordUri.toString());

        // Defines a variable to contain the number of rows deleted
        int mRowsDeleted = 0;

        // Deletes the pets that match the selection criteria
        mRowsDeleted = mContext.getContentResolver().delete(
                mCurrentRecordUri,   // the pet content URI
                null,                    // the column to select on
                null                      // the value to compare to
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
        Log.v(LOG_TAG,"deleteResult: " + deleteResult  );

        Intent resultIntent = new Intent(Constants.DELETE_SERVICE_RESULT);
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

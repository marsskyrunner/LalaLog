package com.mars_skyrunner.lalalog.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mars_skyrunner.lalalog.data.RecordContract.RecordEntry;

/**
 * Database helper for LalaLog app. Manages record database creation and version management.
 */
public class SchoolDbHelper extends SQLiteOpenHelper {

    public final String LOG_TAG = SchoolDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "school.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link SchoolDbHelper}.
     *
     * @param context of the app
     */
    
    public SchoolDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the records table
        String SQL_CREATE_RECORDS_TABLE =  "CREATE TABLE " + RecordEntry.TABLE_NAME + " ("
                + RecordEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RecordEntry.COLUMN_RECORD_DATE + " TEXT NOT NULL, "
                + RecordEntry.COLUMN_SUBJECT_DISPLAY_NAME + " TEXT, "
                + RecordEntry.COLUMN_SUBJECT_GROUP_ID + " TEXT, "
                + RecordEntry.COLUMN_RECORD_TIME + " TEXT, "
                + RecordEntry.COLUMN_RECORD_TYPE + " TEXT, "
                + RecordEntry.COLUMN_RECORD_REFERENCE_ID + " TEXT, "
                + RecordEntry.COLUMN_RECORD_TEXT + " TEXT, "
                + RecordEntry.COLUMN_SUBJECT_ID + " TEXT )";

        Log.w(LOG_TAG,"SQL_CREATE_RECORDS_TABLE: " + SQL_CREATE_RECORDS_TABLE);

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_RECORDS_TABLE);

        // Create a String that contains the SQL statement to create the records table
        String SQL_CREATE_SUBJECTS_TABLE =  "CREATE TABLE " + SubjectContract.SubjectEntry.TABLE_NAME + " ("
                + SubjectContract.SubjectEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SubjectContract.SubjectEntry.COLUMN_UNIQUE_ID + " TEXT NOT NULL, "
                + SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME + " TEXT COLLATE NOCASE, "
                + SubjectContract.SubjectEntry.COLUMN_SUBJECT_LASTNAME1 + " TEXT COLLATE NOCASE, "
                + SubjectContract.SubjectEntry.COLUMN_SUBJECT_LASTNAME2 + " TEXT COLLATE NOCASE, "
                + SubjectContract.SubjectEntry.COLUMN_SUBJECT_GROUP + " TEXT, "
                + SubjectContract.SubjectEntry.COLUMN_SUBJECT_BIRTHDATE + " TEXT )";

        Log.w(LOG_TAG,"SQL_CREATE_SUBJECTS_TABLE: " + SQL_CREATE_SUBJECTS_TABLE);

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_SUBJECTS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }

}

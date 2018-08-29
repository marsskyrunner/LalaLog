package com.mars_skyrunner.lalalog.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 * API Contract for Record Database for the Lala Log app.
 */
public final class RecordContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private RecordContract() {}

    /**
     * Content Provider name
     */
    public static final String CONTENT_AUTHORITY = "com.mars_skyrunner.lalalog";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.mars_skyrunner.lalalog.record/records/ is a valid path for
     * looking at Record data. content://com.mars_skyrunner.lalalog/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */

    public static final String PATH_RECORDS = "records";

    /**
     * Inner class that defines constant values for the Records database table.
     * Each entry in the table represents a single Record.
     */
    public static final class RecordEntry implements BaseColumns {

        /** The content URI to access the record data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_RECORDS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of records.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECORDS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single record.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECORDS;



        /** Name of database table for records */
        public final static String TABLE_NAME = "records";

        /**
         * Unique ID number for the Record (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;


        /**
         * Date of the Record.
         *
         * Type: TEXT
         */
        public final static String COLUMN_RECORD_DATE ="date";


        /**
         * Time of the Record.
         *
         * Type: TEXT
         */
        public final static String COLUMN_RECORD_TIME = "time";

        /**
         * Text of the Record.
         *
         * Type: TEXT
         */
        public final static String COLUMN_RECORD_TEXT = "text";

        /**
         * Current Record Reference.
         *
         * Type: TEXT
         */
        public final static String COLUMN_RECORD_REFERENCE_ID = "reference_id";



        /**
         * Subject unique id,  author of the Record.
         *
         * Type: TEXT
         */
        public final static String COLUMN_SUBJECT_ID = "subject_id";


        /**
         * Subject display name,  author of the Record.
         *
         * Type: TEXT
         */
        public final static String COLUMN_SUBJECT_DISPLAY_NAME = "subject_display_name";

        /**
         * Subject group resource id,  author of the Record.
         *
         * Type: TEXT
         */
        public final static String COLUMN_SUBJECT_GROUP_ID = "subject_group_id";

        /**
         * Type of Record Object
         *
         * The only possible values are {@link #CURRENT} or {@link #HISTORY}
         * Type: TEXT
         */

        public final static String COLUMN_RECORD_TYPE = "type";


        /**
         * Possible values for the group of the subject.
         */
        public static final int CURRENT = 0;
        public static final int  HISTORY = 1;

        /**
         * Returns whether or not the given type is {@link #CURRENT} or {@link #HISTORY}
         */
        public static boolean isValidType(int group) {
            if (group == CURRENT || group == HISTORY ) {
                return true;
            }
            return false;
        }




        public static boolean isValidSubjectID(String subjectID){
            return true;
        }

    }



}

package com.mars_skyrunner.lalalog.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for Subject Database for the Lala Log app.
 */
public final class SubjectContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private SubjectContract() {}

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
     * For instance, content://com.mars_skyrunner.lalalog/subjects/ is a valid path for
     * looking at subject data. content://com.mars_skyrunner.lalalog/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */

    public static final String PATH_SUBJECTS = "subjects";

    /**
     * Inner class that defines constant values for the subjects database table.
     * Each entry in the table represents a single subject.
     */
    public static final class SubjectEntry implements BaseColumns {

        /** The content URI to access the subject data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SUBJECTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of subjects.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBJECTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single subject.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBJECTS;



        /** Name of database table for pets */
        public final static String TABLE_NAME = "subjects";

        /**
         * Unique ID number for the subject (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the subject.
         *
         * Type: TEXT
         */
        public final static String COLUMN_SUBJECT_NAME ="name";



        /**
         * Unique id of the subject.
         *
         * Type: TEXT
         */
        public final static String COLUMN_UNIQUE_ID ="unique_id";


        /**
         * lastname1 of the subject.
         *
         * Type: TEXT
         */
        public final static String COLUMN_SUBJECT_LASTNAME1 = "lastname1";

        /**
         * lastname2 of the subject.
         *
         * Type: TEXT
         */
        public final static String COLUMN_SUBJECT_LASTNAME2 = "lastname2";

        /**
         * Birthdate of the subject.
         *
         * Type: TEXT
         */
        public final static String COLUMN_SUBJECT_BIRTHDATE = "birthdate";



        /**
         * Group id where the subject belongs to.
         *
         * The only possible values are {@link #GROUPA}, {@link #GROUPB},
         * or {@link #GROUPC}.
         *
         * Type: TEXT
         */
        public final static String COLUMN_SUBJECT_GROUP = "subject_group";


        /**
         * Possible values for the group of the subject.
         */
        public static final int GROUPA = 0;
        public static final int GROUPB = 1;
        public static final int GROUPC = 2;

        /**
         * Returns whether or not the given gender is {@link #GROUPA}, {@link #GROUPB},
         * or {@link #GROUPC}.
         */
        public static boolean isValidGroup(int group) {
            if (group == GROUPA || group == GROUPB || group == GROUPC) {
                return true;
            }
            return false;
        }


    }

}

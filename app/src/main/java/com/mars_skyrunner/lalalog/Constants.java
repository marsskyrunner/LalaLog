package com.mars_skyrunner.lalalog;

import java.util.HashMap;
import java.util.Map;

public class Constants {


    //Save fab button read broadcast receiver
    private static String packageName =  "com.mars_skyrunner.lalalog";
    public static final String RESULT = packageName + ".RESULT";
    public static final String SAVE_RECORD = "com.mars_skyrunner.lalalog.SAVE_RECORD";
    public static final int RECORD_LOADER = 100;
    public static final int SUBJECT_LOADER = 200;
    public static final int GET_RECORD_REF_LOADER = 300;
    public static final String RECORD_BUNDLE = "com.mars_skyrunner.lalalog.RECORD_BUNDLE";
    public static final String EDIT_RECORD = "com.mars_skyrunner.lalalog.EDIT_RECORD";
    public static final String SAVE_SUBJECT = "com.mars_skyrunner.lalalog.SAVE_SUBJECT";
    public static final String SUBJECT_URI_STRING = "com.mars_skyrunner.lalalog.SUBJECT_URI_STRING";
    public static final String SUBJECT_BUNDLE =  "com.mars_skyrunner.lalalog.SUBJECT_BUNDLE";
    public static final String DELETE_SERVICE_RESULT = packageName + ".DELETE_SERVICE_RESULT";
    public static final String DELETE_SERVICE_EXTRA = packageName + ".DELETE_SERVICE_EXTRA";

    public static String RECORD_URI = "com.mars_skyrunner.lalalog.RECORD_URI";

    //Map Record objects in Subject SQlite database with its Subject ID
    public static Map<String, Subject> SUBJECT_MAP = new HashMap<String, Subject>();

    //Map Record objects in Record SQlite database with its Record ID
    public static Map<String, Record> RECORD_MAP = new HashMap<String, Record>();

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

}

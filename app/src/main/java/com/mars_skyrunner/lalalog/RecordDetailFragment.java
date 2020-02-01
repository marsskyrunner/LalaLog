package com.mars_skyrunner.lalalog;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Array;
import java.util.Calendar;


import com.mars_skyrunner.lalalog.data.RecordContract.RecordEntry;
import com.mars_skyrunner.lalalog.data.SubjectContract.SubjectEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import static com.mars_skyrunner.lalalog.RecordDetailActivity.currentDay;
import static com.mars_skyrunner.lalalog.RecordDetailActivity.currentMonth;
import static com.mars_skyrunner.lalalog.RecordDetailActivity.currentYear;
import static com.mars_skyrunner.lalalog.RecordDetailActivity.detailMode;
import static com.mars_skyrunner.lalalog.SubjectListActivity.getSubjectsArrayList;
import static com.mars_skyrunner.lalalog.SubjectListActivity.mSubjectAdapter;

/**
 * A fragment representing a single Record detail screen.
 * This fragment is either contained in a {@link RecordListActivity}
 * in two-pane mode (on tablets) or a {@link RecordDetailActivity}
 * on handsets.
 */
public class RecordDetailFragment extends Fragment {


    /*Default birthdate year when edition mode is enabled*/
    private final int MIN_AGE = 11;
    private final int MAX_AGE = 18;

    //Log tag
    private String LOG_TAG = RecordDetailFragment.class.getSimpleName();

    /**
     * The dummy content this fragment is presenting.
     */
    private Record mItem;


    //Months in a year
    ArrayList<String> months;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */



    //Edition Elements

    public static EditText mNameEditText;
    public static EditText mLastName1EditText;
    public static EditText mLastName2EditText;
    public static EditText mRecordEditText;
    public static Spinner mGroupSpinner, mBirthdateDaySpinner, mBirthdateMonthSpinner, mBirthdateYearSpinner;
    public static AutoCompleteTextView mUniqueIDAutoComplete;

    CardView mLastName1CardView;
    CardView mLastName2CardView;
    String  prevRecordText;

    //Readable elements
    TextView mNameTextView;
    TextView mGroupTextView;
    public static TextView mBirthdateTextView;
    TextView mRecordTextView;
    TextView mUniqueIDTextView;

    boolean recordEditionFlag = false;
    Subject subjectSelected = null;

    Uri recordUri;
    private ArrayList<String> yearArrayList;

    public static Bundle newRecordBundle = new Bundle();


    View rootView;

    public RecordDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.w(LOG_TAG,"onCreate()");

        //Register broadcast receiver to read save or edit fab button
        getActivity().registerReceiver(saveReceiver, new IntentFilter(Constants.SAVE_RECORD));
        getActivity().registerReceiver(editReceiver, new IntentFilter(Constants.EDIT_RECORD));


        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);


        final Bundle args = getArguments();
        String recordUriStr = args.getString(Constants.ARG_ITEM_ID);

        Log.w(LOG_TAG,"recordUriStr: " + recordUriStr);

        if(!recordUriStr.equals("null")){

            Log.w(LOG_TAG,"recordUriStr not null ");

            recordUri = Uri.parse(recordUriStr);
            String recordIDStr = String.valueOf(ContentUris.parseId(recordUri));

            Log.v(LOG_TAG,"recordIDStr: " + recordIDStr);

            mItem = Constants.RECORD_MAP.get(recordIDStr);

            Log.v(LOG_TAG,"mItem.getTime(): " + mItem.getTime());

            prevRecordText = mItem.getRecordText();
            Log.v(LOG_TAG,"prevRecordText: " + prevRecordText);

            if (appBarLayout != null) {

                subjectSelected = mItem.getSubject();
                String labelText = subjectSelected.getSubjectName() + " " + subjectSelected.getSubjectLastName1();
                appBarLayout.setTitle(labelText);
            }
        }else{

            Log.w(LOG_TAG,"recordUriStr null ");
            // "Add new Record" Button has been clicked
            mItem = null;

            if (appBarLayout != null) {
                String labelText = getResources().getString(R.string.new_record);
                appBarLayout.setTitle(labelText);
            }
        }


    }


    private String getRecordTime() {
        String time = "";

        Date currentTime = Calendar.getInstance().getTime();
        time = (String) DateFormat.format("h:mm a", currentTime);

        return time;
    }

    private String getRecordDate() {

        String date = currentDay + " / " + months.get(currentMonth - 1) + " / " + currentYear;

        return date;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.record_detail, container, false);

        initEditionElements();

        // Show the record text content as text in a TextView.
        if (mItem != null) {

            hideEditionMode();
            fillEditionModeTextViews();

        } else {
            showEditionMode();
            ((TextView) rootView.findViewById(R.id.record_date)).setVisibility(View.GONE);
            ((TextView) rootView.findViewById(R.id.record_time)).setVisibility(View.GONE);
        }

        return rootView;
    }

    private void fillEditionModeTextViews() {

        String displayName = mItem.getSubject().getSubjectName() + " " + mItem.getSubject().getSubjectLastName1() + " " + mItem.getSubject().getSubjectLastName2();
        Log.v(LOG_TAG, "fillEditionModeTextViews: displayName: " + displayName);

        ((TextView) rootView.findViewById(R.id.record_detail)).setText(mItem.getRecordText());
        ((TextView) rootView.findViewById(R.id.subject_name)).setText(displayName);
        ((TextView) rootView.findViewById(R.id.subject_group)).setText(mItem.getSubject().getGroupDisplay());
        ((TextView) rootView.findViewById(R.id.subject_birthdate)).setText(mItem.getSubject().getSubjectBirthdate());
        ((TextView) rootView.findViewById(R.id.subject_unique_id)).setText("" + mItem.getSubject().getSubjectUniqueID());
        ((TextView) rootView.findViewById(R.id.record_date)).setText(mItem.getDate());
        ((TextView) rootView.findViewById(R.id.record_time)).setText(mItem.getTime());
    }

    private void initGroupSpinner() {

        ArrayList<String> options = new ArrayList<>();
        options.add(getResources().getString(R.string.group_1));
        options.add(getResources().getString(R.string.group_2));
        options.add(getResources().getString(R.string.group_3));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, R.id.text1, options);

        mGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                Log.v(LOG_TAG, "mGroupSpinner: onItemSelected:   " + mGroupSpinner.getSelectedItem().toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        mGroupSpinner.setAdapter(dataAdapter);

    }


    private void initBirthdateDaySpinner(int month, int year) {


        Log.v(LOG_TAG, "initBirthdateDaySpinner");
        ArrayList<String> options = getMonthDays(month, year);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, R.id.text1, options);

        Log.v(LOG_TAG, "initBirthdateDaySpinner: options.size(): " + options.size());


        mBirthdateDaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                Log.v(LOG_TAG, "mBirthdateDaySpinner: onItemSelected:   " + mBirthdateDaySpinner.getSelectedItem().toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });


        mBirthdateDaySpinner.setAdapter(dataAdapter);

    }


    private void initBirthdateMonthSpinner() {

        Log.v(LOG_TAG, "initBirthdateMonthSpinner");

        ArrayList<String> options = getMonthArrayList();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, R.id.text1, options);

        Log.v(LOG_TAG, "initBirthdateMonthSpinner: options.size(): " + options.size());

        mBirthdateMonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                Log.v(LOG_TAG, "mBirthdateMonthSpinner: onItemSelected:   " + mBirthdateMonthSpinner.getSelectedItem().toString());

                int currentYearInSpinner = (int )Double.parseDouble(mBirthdateYearSpinner.getSelectedItem().toString().trim());

                Log.v(LOG_TAG, "mBirthdateMonthSpinner: currentYearInSpinner:   " + currentYearInSpinner);

                mBirthdateDaySpinner.setEnabled(true);
                initBirthdateDaySpinner(arg2, currentYearInSpinner);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {


            }
        });

        mBirthdateMonthSpinner.setAdapter(dataAdapter);

    }


    private void hideEditionMode() {

        mLastName1CardView.setVisibility(View.GONE);
        mLastName2CardView.setVisibility(View.GONE);
        mNameEditText.setVisibility(View.GONE);
        mRecordEditText.setVisibility(View.GONE);
        mGroupSpinner.setVisibility(View.GONE);
        mBirthdateDaySpinner.setVisibility(View.GONE);
        mBirthdateMonthSpinner.setVisibility(View.GONE);
        mBirthdateYearSpinner.setVisibility(View.GONE);
        mUniqueIDAutoComplete.setVisibility(View.GONE);

        mNameTextView.setVisibility(View.VISIBLE);
        mGroupTextView.setVisibility(View.VISIBLE);
        mRecordTextView.setVisibility(View.VISIBLE);
        mBirthdateTextView.setVisibility(View.VISIBLE);
        mUniqueIDTextView.setVisibility(View.VISIBLE);
    }

    private void showEditionMode() {

        mLastName1CardView.setVisibility(View.VISIBLE);
        mLastName2CardView.setVisibility(View.VISIBLE);
        mNameEditText.setVisibility(View.VISIBLE);
        mRecordEditText.setVisibility(View.VISIBLE);
        mGroupSpinner.setVisibility(View.VISIBLE);
        mBirthdateDaySpinner.setVisibility(View.VISIBLE);
        mBirthdateMonthSpinner.setVisibility(View.VISIBLE);
        mBirthdateYearSpinner.setVisibility(View.VISIBLE);
        mUniqueIDAutoComplete.setVisibility(View.VISIBLE);

        mNameTextView.setVisibility(View.GONE);
        mGroupTextView.setVisibility(View.GONE);
        mRecordTextView.setVisibility(View.GONE);
        mBirthdateTextView.setVisibility(View.GONE);
        mUniqueIDTextView.setVisibility(View.GONE);

    }

    private void initEditionElements() {

        //Edition elements
        mLastName1CardView = rootView.findViewById(R.id.lastname1_cardview);
        mLastName2CardView = rootView.findViewById(R.id.lastname2_cardview);
        mNameEditText = rootView.findViewById(R.id.name_edittext);
        mRecordEditText = rootView.findViewById(R.id.record_edittext);
        mLastName1EditText = rootView.findViewById(R.id.lastname1_edittext);
        mLastName2EditText = rootView.findViewById(R.id.lastname2_edittext);


        mGroupSpinner = rootView.findViewById(R.id.group_spinner);
        initGroupSpinner();

        mBirthdateDaySpinner = rootView.findViewById(R.id.birthdate_day_spinner);
        mBirthdateDaySpinner.setEnabled(false);
        initBirthdateDaySpinner(currentMonth, currentYear);
        mBirthdateYearSpinner= rootView.findViewById(R.id.birthdate_year_spinner);
        initBirthdateYearSpinner();

        mBirthdateMonthSpinner = rootView.findViewById(R.id.birthdate_month_spinner);
        initBirthdateMonthSpinner();

        mUniqueIDAutoComplete = rootView.findViewById(R.id.unique_id_auto);
        mUniqueIDAutoComplete.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});//Sets unique ids' input maximum characters to 4
        initUniqueIDAutoComplete();

        //Readable elements
        mNameTextView = rootView.findViewById(R.id.subject_name);
        mGroupTextView = rootView.findViewById(R.id.subject_group);
        mBirthdateTextView = rootView.findViewById(R.id.subject_birthdate);
        mRecordTextView = rootView.findViewById(R.id.record_detail);
        mUniqueIDTextView = rootView.findViewById(R.id.subject_unique_id);


    }

    private void initBirthdateYearSpinner() {

        Log.v(LOG_TAG, "initBirthdateYearSpinner");

        ArrayList<String> options = getYearArrayList();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, R.id.text1, options);

        Log.v(LOG_TAG, "initBirthdateYearSpinner: options.size(): " + options.size());

        mBirthdateYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                Log.v(LOG_TAG, "initBirthdateYearSpinner: onItemSelected:   " + mBirthdateYearSpinner.getSelectedItem().toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {


            }
        });

        mBirthdateYearSpinner.setAdapter(dataAdapter);


    }

    private void initUniqueIDAutoComplete() {

        ArrayList<String> options = getAutoCompleteOptions();

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, R.id.text1, options);

        mUniqueIDAutoComplete.setAdapter(dataAdapter);

        mUniqueIDAutoComplete.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                Log.v(LOG_TAG, "addTextChangedListener: onTextChanged: " + charSequence);

                subjectSelected = uniqueIdExists(charSequence);

                if (subjectSelected != null) {

                    Log.v(LOG_TAG, "initUniqueIDAutoComplete: subjectSelected != null");
                    fillTextViews(subjectSelected);
                }else{
                    emptyEditTexts();
                }

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }

    private void emptyEditTexts() {
        //Fill editable elements with subjects info

        showEditionMode();

        mNameEditText.setText("");
        mLastName1EditText.setText("");
        mLastName2EditText.setText("");
        mGroupSpinner.setSelection(0);

        int defaultBirthdate = currentYear - MAX_AGE;

        initBirthdateDaySpinner(0,defaultBirthdate);

        mBirthdateDaySpinner.setSelection(0);
        mBirthdateMonthSpinner.setSelection(0);
        mBirthdateYearSpinner.setSelection(0);
    }

    private void fillTextViews(Subject subject) {

        Log.v(LOG_TAG, "fillTextViews: subject.getSubjectID(): " + subject.getSubjectID());
        Log.v(LOG_TAG, "fillTextViews: subject.getSubjectName(): " + subject.getSubjectName());
        Log.v(LOG_TAG, "fillTextViews: subject.getGroupID(): " + subject.getGroupID());
        Log.v(LOG_TAG, "fillTextViews: subject.getSubjectBirthdate(): " + subject.getSubjectBirthdate());

        hideEditionMode();

        //Keep Subjects unique id AutoFillTextView and new records EditText VISIBLE

        mUniqueIDTextView.setVisibility(View.GONE);
        mUniqueIDAutoComplete.setVisibility(View.VISIBLE);

        mRecordEditText.setVisibility(View.VISIBLE);
        mRecordTextView.setVisibility(View.GONE);

        //Fill editable elements with subjects info


        mNameTextView.setText(subject.getSubjectName());
        mGroupTextView.setText(subject.getGroupDisplay());
        mBirthdateTextView.setText(subject.getSubjectBirthdate());


    }

    private int getBirthdateDay(String subjectBirthdate) {
        String[] parts = subjectBirthdate.split("/");

        Log.v(LOG_TAG,"getBirthdateDay: " + Integer.parseInt(parts[0].trim()));

        return Integer.parseInt(parts[0].trim());
    }

    private int getBirthdateMonth(String subjectBirthdate) {

        String[] parts = subjectBirthdate.split("/");

        int birthdateMonthID = months.indexOf(parts[1].trim());

        Log.v(LOG_TAG,"getBirthdateMonth: birthdateMonthID: " + birthdateMonthID);

        return birthdateMonthID;

    }

    private int getBirthdateYear(String subjectBirthdate) {

        String[] parts = subjectBirthdate.split("/");

        Log.v(LOG_TAG,"getBirthdateMonth: getBirthdateYear: " + Integer.parseInt(parts[2].trim()));

        return Integer.parseInt(parts[2].trim());

    }

    private Subject uniqueIdExists(CharSequence charSequence) {

        Subject answer = null;

        Iterator it = Constants.SUBJECT_MAP.values().iterator();

        String uniqueIdQuery = "" + charSequence;

        while(it.hasNext()) {
            Subject subject = (Subject) it.next();
            String subjectID = String.valueOf(subject.getSubjectUniqueID());

            if(uniqueIdQuery.equals(subjectID)){
                Log.v(LOG_TAG,"uniqueIdExists: "  + uniqueIdQuery + " = " + subjectID );
                answer = subject;
                break;
            }else{
                Log.v(LOG_TAG,"uniqueIdExists: "  + uniqueIdQuery + " != " + subjectID );
            }
        }

        return  answer;
    }

    public ArrayList<String> getMonthDays(int month, int year) {

        ArrayList<String> monthDays = new ArrayList<>();

        Log.v(LOG_TAG, "getMonthDays: year: " + year);

        // Create a calendar object and set year and month
        Calendar mycal = new GregorianCalendar(year, month, 1);
        // Get the number of days in that month

        int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

        Log.v(LOG_TAG, "getMonthDays: " + daysInMonth);

        for (int i = 0; i < daysInMonth; i++) {
            monthDays.add("" + (i + 1));
        }

        Log.v(LOG_TAG, "getMonthDays: monthDays.size(): " + monthDays.size());

        return monthDays;
    }


    public ArrayList<String> getMonthArrayList() {

        months = new ArrayList<>();

        months.add(getResources().getString(R.string.january));
        months.add(getResources().getString(R.string.february));
        months.add(getResources().getString(R.string.march));
        months.add(getResources().getString(R.string.april));
        months.add(getResources().getString(R.string.may));
        months.add(getResources().getString(R.string.june));
        months.add(getResources().getString(R.string.july));
        months.add(getResources().getString(R.string.august));
        months.add(getResources().getString(R.string.september));
        months.add(getResources().getString(R.string.october));
        months.add(getResources().getString(R.string.november));
        months.add(getResources().getString(R.string.december));


        return months;
    }

    private BroadcastReceiver editReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            recordEditionFlag = true;

            if(subjectSelected == null){
                Log.v(LOG_TAG,"editReceiver: subjectSelected == null");
            }else{
                Log.v(LOG_TAG,"editReceiver: subjectSelected != null");
            }

            //Enable Record text edition
            mRecordTextView.setVisibility(View.GONE);

            mRecordEditText.setVisibility(View.VISIBLE);
            mRecordEditText.setText(mItem.getRecordText());


        }
    };

    private BroadcastReceiver saveReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.v(LOG_TAG,"saveReceiver onReceive");

            String uniqueID = "";
            String name  = "";
            String lastname1  = "";
            String lastname2  = "";
            String groupID  = "";
            String birthdateDay  = "";
            String birthdateMonth  = "";
            String birthdateYear  = "";
            String birthdate  = "";
            String subjectID  = "";
            String recordReference = "";
            String recordText = mRecordEditText.getText().toString();

            if(subjectSelected == null){//New Record has been saved

                Log.v(LOG_TAG,"New Record has been saved");

                uniqueID = mUniqueIDAutoComplete.getText().toString();
                name = mNameEditText.getText().toString();
                lastname1 = mLastName1EditText.getText().toString();
                lastname2 = mLastName2EditText.getText().toString();
                groupID = getGroupID(mGroupSpinner.getSelectedItem().toString());
                birthdateDay = mBirthdateDaySpinner.getSelectedItem().toString();
                birthdateMonth = mBirthdateMonthSpinner.getSelectedItem().toString();
                birthdateYear = mBirthdateYearSpinner.getSelectedItem().toString();

                birthdate = birthdateDay + " / " + birthdateMonth + " / " + birthdateYear;

                if(!TextUtils.isEmpty(uniqueID)){

                    subjectID = getSubjectID(uniqueID, name, lastname1, lastname2, groupID, birthdate);
                    recordReference = subjectID;
                }


            }else{ //Record review/edition has been selected

                Log.v(LOG_TAG, "Record review/edition has been selected");

                if(recordEditionFlag){

                    recordReference = mItem.getRecordReference();
                    Log.v(LOG_TAG, "recordReference: " + recordReference);

                    uniqueID = subjectSelected.getSubjectID();

                }else{
                    uniqueID = mUniqueIDTextView.getText().toString();
                }

                name = subjectSelected.getSubjectName();
                lastname1 = subjectSelected.getSubjectLastName1();
                lastname2 = subjectSelected.getSubjectLastName2();
                groupID = subjectSelected.getGroupID();
                birthdate = mBirthdateTextView.getText().toString();
                subjectID = subjectSelected.getSubjectID();

            }


            Log.v(LOG_TAG, "uniqueID: " + uniqueID);
            Log.v(LOG_TAG, "subjectID: " + uniqueID);
            Log.v(LOG_TAG, "name: " + name);
            Log.v(LOG_TAG, "lastname1: " + lastname1);
            Log.v(LOG_TAG, "lastname2: " + lastname2);
            Log.v(LOG_TAG, "groupID: " + groupID);
            Log.v(LOG_TAG, "birthdate: " + birthdate);
            Log.v(LOG_TAG, "recordText: " + recordText);

            // Check if this is supposed to be a new record
            // and check if all the fields in the editor are blank


            Log.v(LOG_TAG, "TextUtils.isEmpty(uniqueID): " + TextUtils.isEmpty(uniqueID));
            Log.v(LOG_TAG, "TextUtils.isEmpty(name): " + TextUtils.isEmpty(name));
            Log.v(LOG_TAG, "TextUtils.isEmpty(lastname1): " + TextUtils.isEmpty(lastname1));
            Log.v(LOG_TAG, "TextUtils.isEmpty(lastname2): " + TextUtils.isEmpty(lastname2));
            Log.v(LOG_TAG, "TextUtils.isEmpty(recordText): " + TextUtils.isEmpty(recordText));
            Log.v(LOG_TAG, "groupID: " + groupID);
            Log.v(LOG_TAG, "birthdate: " + birthdate);


            if (TextUtils.isEmpty(uniqueID)
                    && TextUtils.isEmpty(name)
                    && TextUtils.isEmpty(lastname1)
                    && TextUtils.isEmpty(lastname2)
                    && TextUtils.isEmpty(recordText)
                    && groupID.equals("0")
                    && birthdate.equals("1 / Enero / " + (currentYear - 18))) {
                // Since no fields were modified, we can return early without creating a new record.
                // No need to create ContentValues and no need to do any ContentProvider operations.

                Log.v(LOG_TAG,"saveReceiver :no fields were modified");
                Toast.makeText(getActivity(),getString(R.string.no_changes_made),Toast.LENGTH_SHORT).show();

                if(detailMode.equals(Constants.NEW_RECORD)){

                    getActivity().finish();

                }else{

                    hideEditionMode();
                    mRecordEditText.getText().clear();

                    ((TextView) rootView.findViewById(R.id.record_detail)).setText(recordText);

                }

                return;

            }else {

                if(recordEditionFlag && recordText.equals(prevRecordText)){


                    hideEditionMode();
                    mRecordEditText.getText().clear();
                    ((TextView) rootView.findViewById(R.id.record_detail)).setText(recordText);

                    Log.v(LOG_TAG,"record edition :no fields were modified");
                    Toast.makeText(getActivity(),getString(R.string.no_changes_made),Toast.LENGTH_SHORT).show();
                    //After saving changes, go back to RecordListActivity
                    //NavUtils.navigateUpTo(getActivity(), new Intent(getActivity(), RecordListActivity.class));

                }else{

                    // Create a ContentValues object where column names are the keys,
                    // and pet attributes from the editor are the values.
                    ContentValues values = new ContentValues();
                    values.put(RecordEntry.COLUMN_SUBJECT_ID,subjectID );
                    values.put(RecordEntry.COLUMN_SUBJECT_GROUP_ID, groupID);
                    values.put(RecordEntry.COLUMN_RECORD_TYPE, RecordEntry.CURRENT);
                    values.put(RecordEntry.COLUMN_RECORD_REFERENCE_ID, recordReference);
                    values.put(RecordEntry.COLUMN_RECORD_TEXT, recordText);
                    values.put(RecordEntry.COLUMN_RECORD_TIME, getRecordTime());
                    values.put(RecordEntry.COLUMN_RECORD_DATE, getRecordDate());

                    Uri newUri;

                    // This is a NEW record, so insert a new record into the provider,
                    // returning the content URI for the new record.
                    newUri = getActivity().getContentResolver().insert(RecordEntry.CONTENT_URI, values);

                    // Show a toast message depending on whether or not the insertion was successful.
                    if (newUri == null) {
                        // If the new content URI is null, then there was an error with insertion.
                        Toast.makeText(getActivity(), getString(R.string.editor_record_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        // Otherwise, the insertion was successful and we can display a toast.
                        Toast.makeText(getActivity(), getString(R.string.editor_record_success), Toast.LENGTH_SHORT).show();

                    }

                    if(recordEditionFlag ){

                        mRecordEditText.getText().clear();
                        hideEditionMode();

                        ((TextView) rootView.findViewById(R.id.record_detail)).setText(recordText);
                        prevRecordText = recordText;

                        updateRecordType(recordReference);
                    }else{
                        updateRecordReference(newUri);
                        //After saving changes, go back to RecordListActivity
                        //NavUtils.navigateUpTo(getActivity(), new Intent(getActivity(), RecordListActivity.class));
                        //NavUtils.navigateUpTo(getActivity(), new Intent(getActivity(), RecordListActivity.class));
                        getActivity().finish();

                    }


                }
            }


        }

        private void updateRecordType(String recordReference) {

            ContentValues values = new ContentValues();
            values.put(RecordEntry.COLUMN_RECORD_TYPE, RecordEntry.HISTORY);
            values.put(RecordEntry.COLUMN_RECORD_REFERENCE_ID, recordReference);

            // Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.

            int rowsAffected = getActivity().getContentResolver().update(recordUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.

                Log.v(LOG_TAG,"there was an error with the update of the record type.");

            } else {

                // Otherwise, the update was successful and we can display a toast.
                Log.v(LOG_TAG,"record type update successful.");

            }

        }


    };


    private void updateRecordReference(Uri newUri) {

        Log.v(LOG_TAG,"updateRecordReference: newUri: " + newUri.toString());

        String recordReferenceId = String.valueOf(ContentUris.parseId(newUri));

        ContentValues values = new ContentValues();
        values.put(RecordEntry.COLUMN_RECORD_REFERENCE_ID, recordReferenceId );

        // Pass in null for the selection and selection args
        // because mCurrentPetUri will already identify the correct row in the database that
        // we want to modify.

        int rowsAffected = getActivity().getContentResolver().update(newUri, values, null, null);

        // Show a toast message depending on whether or not the update was successful.
        if (rowsAffected == 0) {
            // If no rows were affected, then there was an error with the update.

            Log.v(LOG_TAG,"there was an error with the update of the record type.");

        } else {

            // Otherwise, the update was successful and we can display a toast.
            Log.v(LOG_TAG,"record type update successful.");

        }

    }

    private String getSubjectID(String uniqueID, String name, String lastname1, String lastname2, String groupID, String birthdate) {

        String subjectID = "";

        // Create a ContentValues object where column names are the keys,
        // and subject attributes from the editor are the values.
        ContentValues values = new ContentValues();

        values.put(SubjectEntry.COLUMN_UNIQUE_ID, uniqueID);
        values.put(SubjectEntry.COLUMN_SUBJECT_NAME, name);
        values.put(SubjectEntry.COLUMN_SUBJECT_LASTNAME1, lastname1);
        values.put(SubjectEntry.COLUMN_SUBJECT_LASTNAME2, lastname2);
        values.put(SubjectEntry.COLUMN_SUBJECT_GROUP, groupID);
        values.put(SubjectEntry.COLUMN_SUBJECT_BIRTHDATE, birthdate);

        Log.v(LOG_TAG,"getSubjectID: SubjectEntry.CONTENT_URI: " + SubjectEntry.CONTENT_URI);

        Log.v(LOG_TAG,"getSubjectID: values.toString(): " + values.toString());

        // This is a NEW subject, so insert a new subject into the provider,
        // returning the content URI for the new subject.
        Uri newUri = getActivity().getContentResolver().insert(SubjectEntry.CONTENT_URI, values);

        Log.v(LOG_TAG,"getSubjectID: newUri.toString(): " + newUri.toString());

        // Show a toast message depending on whether or not the insertion was successful.
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(getActivity(), getString(R.string.editor_subject_failed), Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(getActivity(), getString(R.string.editor_subject_success), Toast.LENGTH_SHORT).show();
        }


        // A "projection" defines the columns that will be returned for each row
        String[] mProjection =
                {
                        SubjectEntry._ID    // Contract class constant for the _ID column name
                };


        Cursor newSujectCursor = getActivity().getContentResolver().query(newUri, mProjection, null, null, null);


        subjectID = "";

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (newSujectCursor.moveToFirst()) {

            // Find the columns of subject attributes that we're interested in
            int idColumnIndex = newSujectCursor.getColumnIndex(SubjectEntry._ID);

            // Extract out the value from the Cursor for the given column index
            subjectID = newSujectCursor.getString(idColumnIndex);

        }

        Log.v(LOG_TAG,"getSubjectID: subjectID: " + subjectID);


        //Add new Subject to SUBJECT_MAP hashmap
        Subject subject = new Subject(subjectID, name, lastname1, lastname2, birthdate, groupID);
        subject.setSubjectUniqueID(Integer.parseInt(uniqueID.trim()));
        Constants.SUBJECT_MAP.put(subjectID, subject);

        return subjectID;
    }




    private String getGroupID(String groupString) {
        String groupID = "0";


        Log.v(LOG_TAG,"getGroupID: groupString: " + groupString);


        switch (groupString) {
            case "A":
                groupID = "0";
                break;

            case "B":
                groupID = "1";
                break;

            case "C":
                groupID = "2";
                break;

        }


        Log.v(LOG_TAG,"getGroupID: groupID: " + groupID);


        return groupID;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.w(LOG_TAG,"onDestroy()");


        try {
            getActivity().unregisterReceiver(saveReceiver);
            getActivity().unregisterReceiver(editReceiver);

        } catch (IllegalArgumentException ex) {
            Log.e(LOG_TAG, "onDestroy IllegalArgumentException: " + ex.toString());
        }
    }

    public ArrayList<String> getAutoCompleteOptions() {

        ArrayList<String> autoCompleteOptions = new ArrayList<>();

        Iterator it = Constants.RECORD_MAP.values().iterator();

        while(it.hasNext()) {
            Record obj = (Record) it.next();
            String subjectID =  String.valueOf(obj.getSubject().getSubjectUniqueID());
            Log.v(LOG_TAG,"getAutoCompleteOptions: subjectID: " + subjectID);
            autoCompleteOptions.add(subjectID);
        }


        Log.v(LOG_TAG,"getAutoCompleteOptions: autoCompleteOptions.size(): " + autoCompleteOptions.size());
        return autoCompleteOptions;
    }

    public ArrayList<String> getYearArrayList() {
        ArrayList<String> answer = new ArrayList<>();

        for(int i = MAX_AGE ; i >= MIN_AGE ; i--){
            answer.add("" + (currentYear - i));
            Log.v(LOG_TAG,"getYearArrayList: ADD " + (currentYear - i));
        }

        return answer;
    }
}

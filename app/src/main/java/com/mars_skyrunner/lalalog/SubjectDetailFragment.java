package com.mars_skyrunner.lalalog;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import static com.mars_skyrunner.lalalog.SubjectDetailActivity.currentDay;
import static com.mars_skyrunner.lalalog.SubjectDetailActivity.currentMonth;
import static com.mars_skyrunner.lalalog.SubjectDetailActivity.currentYear;
import static com.mars_skyrunner.lalalog.SubjectListActivity.getSubjectsArrayList;
import static com.mars_skyrunner.lalalog.SubjectListActivity.mSubjectAdapter;

/**
 * A fragment representing a single Record detail screen.
 * This fragment is either contained in a {@link SubjectListActivity}
 * in two-pane mode (on tablets) or a {@link SubjectDetailActivity}
 * on handsets.
 */
public class SubjectDetailFragment extends Fragment {




    /*Default birthdate year when edition mode is enabled*/
    private  CharSequence DEFAULT_BIRTHDATE_YEAR ;

    //Log tag
    private String LOG_TAG = SubjectDetailFragment.class.getSimpleName();

    /**
     * The dummy content this fragment is presenting.
     */
    private Subject mItem;


    //Months in a year
    ArrayList<String> months;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */



    //Edition Elements

    /*
    *             uniqueID = mUniqueIDAutoComplete.getText().toString();
            name = mNameEditText.getText().toString();
            lastname1 = mLastName1EditText.getText().toString();
            lastname2 = mLastName2EditText.getText().toString();
            groupID = getGroupID(mGroupSpinner.getSelectedItem().toString());
            birthdateDay = mBirthdateDaySpinner.getSelectedItem().toString();
            birthdateMonth = mBirthdateMonthSpinner.getSelectedItem().toString();
            birthdateYear =  mBirthdateYearSpinner.getSelectedItem().toString();
    * */

    public static EditText mNameEditText;
    public static EditText mLastName1EditText;
    public static EditText mLastName2EditText;
    public static Spinner mGroupSpinner, mBirthdateDaySpinner, mBirthdateMonthSpinner, mBirthdateYearSpinner;
    public static AutoCompleteTextView mUniqueIDAutoComplete;
    public static LinearLayout mBirthdateLayout;

    CardView mNameCardView;
    CardView mLastName1CardView;
    CardView mLastName2CardView;

    //Readable elements
    TextView mNameTextView;
    TextView mGroupTextView;
    public static TextView mBirthdateTextView;
    TextView mUniqueIDTextView;

    boolean subjectEditionFlag = false;

    Uri subjectUri;
    private String subjectID;
    private String subjectToEditUri;

    /*Default birthdate year when edition mode is enabled*/
    private final int MIN_AGE = 11;
    private final int MAX_AGE = 18;
    String detailMode;

    public SubjectDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.w(LOG_TAG,"onCreate()");

        //Register broadcast receiver to read save or edit fab button
        getActivity().registerReceiver(saveReceiver, new IntentFilter(Constants.SAVE_SUBJECT));

        updateDate();
        DEFAULT_BIRTHDATE_YEAR = "" + (currentYear - 18);

        final Bundle subjectBundle = getArguments();
        String subjectUriStr = subjectBundle.getString(Constants.ARG_ITEM_ID);
        detailMode = subjectBundle.getString(Constants.SUBJECT_DETAIL_MODE);
        Log.v(LOG_TAG,"detailMode: " + detailMode);

        switch (detailMode){

            case "com.mars_skyrunner.lalalog.NEW_SUBJECT":
                //break;
            case "com.mars_skyrunner.lalalog.EDIT_SUBJECT":
                subjectToEditUri = subjectBundle.getString(Constants.SUBJECT_URI_STRING);
                mItem = null;
                break;

            case "com.mars_skyrunner.lalalog.REVIEW_SUBJECT":

                subjectUri = Uri.parse(subjectUriStr);
                String subjectIDStr = String.valueOf(ContentUris.parseId(subjectUri));
                Log.v(LOG_TAG,"subjectUriStr: " + subjectUriStr);

                Log.w(LOG_TAG,"subjectIDStr: " + subjectIDStr);

                mItem = Constants.SUBJECT_MAP.get(subjectIDStr);

                Log.w(LOG_TAG,"mItem.getSubjectName(): " + mItem.getSubjectName());

                String labelText = mItem.getSubjectName() + " " + mItem.getSubjectLastName1();
                getActivity().setTitle(labelText);

                break;

        }


    }

    private void fillEditTexts() {

        Log.v(LOG_TAG,"fillEditTexts()");

        String subjectIDStr = String.valueOf(ContentUris.parseId(Uri.parse(subjectToEditUri)));

        Log.v(LOG_TAG,"fillEditTexts subjectIDStr: " + subjectIDStr);

        Subject subject = Constants.SUBJECT_MAP.get(subjectIDStr);

        //fill Edition elements


        Log.v(LOG_TAG,"ubject.getSubjectName(): " + subject.getSubjectName());
        Log.v(LOG_TAG,"subject.getSubjectLastName1(): " + subject.getSubjectLastName1());
        Log.v(LOG_TAG,"subject.getSubjectLastName2(): " + subject.getSubjectLastName2());


        mNameEditText.setText(subject.getSubjectName());
        mLastName1EditText.setText(subject.getSubjectLastName1());
        mLastName2EditText.setText(subject.getSubjectLastName2());

        mUniqueIDAutoComplete.setText("" + subject.getSubjectUniqueID());


    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.subject_detail, container, false);
        initEditionElements(rootView);

        switch (detailMode){

            case "com.mars_skyrunner.lalalog.NEW_SUBJECT":

                Log.v(LOG_TAG,"NEW_SUBJECT");

                showEditionMode();
                Log.v(LOG_TAG,"subjectToEditUri: " + subjectToEditUri);

                break;

            case "com.mars_skyrunner.lalalog.EDIT_SUBJECT":

                Log.v(LOG_TAG,"EDIT_SUBJECT");

                showEditionMode();
                Log.v(LOG_TAG,"subjectToEditUri: " + subjectToEditUri);

                fillEditTexts();

                break;
            case "com.mars_skyrunner.lalalog.REVIEW_SUBJECT":

                Log.v(LOG_TAG,"REVIEW_SUBJECT");
                hideEditionMode();

                ((TextView) rootView.findViewById(R.id.subject_group)).setText(mItem.getGroupDisplay());
                ((TextView) rootView.findViewById(R.id.subject_birthdate)).setText(mItem.getSubjectBirthdate());
                ((TextView) rootView.findViewById(R.id.subject_unique_id)).setText("" + mItem.getSubjectUniqueID());

                break;

        }

        return rootView;
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

    public ArrayList<String> getYearArrayList() {
        ArrayList<String> answer = new ArrayList<>();

        for(int i = MAX_AGE ; i >= MIN_AGE ; i--){

            answer.add("" + (currentYear - i));
            Log.v(LOG_TAG,"getYearArrayList: ADD " + (currentYear - i));
        }

        return answer;
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

                int birthdateYear = Integer.parseInt(mBirthdateYearSpinner.getSelectedItem().toString());

                Log.v(LOG_TAG, "mBirthdateMonthSpinner: onItemSelected: birthdateYear:   " + birthdateYear);
                mBirthdateDaySpinner.setEnabled(true);
                initBirthdateDaySpinner(arg2, birthdateYear);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {


            }
        });

        mBirthdateMonthSpinner.setAdapter(dataAdapter);

    }


    private void hideEditionMode() {
        mNameCardView.setVisibility(View.GONE);
        mLastName1CardView.setVisibility(View.GONE);
        mLastName2CardView.setVisibility(View.GONE);
        mNameEditText.setVisibility(View.GONE);
        mGroupSpinner.setVisibility(View.GONE);
        mBirthdateLayout.setVisibility(View.GONE);
        mUniqueIDAutoComplete.setVisibility(View.GONE);

        mGroupTextView.setVisibility(View.VISIBLE);
        mBirthdateTextView.setVisibility(View.VISIBLE);
        mUniqueIDTextView.setVisibility(View.VISIBLE);
    }

    private void showEditionMode() {

        mNameCardView.setVisibility(View.VISIBLE);
        mLastName1CardView.setVisibility(View.VISIBLE);
        mLastName2CardView.setVisibility(View.VISIBLE);
        mNameEditText.setVisibility(View.VISIBLE);
        mGroupSpinner.setVisibility(View.VISIBLE);
        mBirthdateLayout.setVisibility(View.VISIBLE);
        mUniqueIDAutoComplete.setVisibility(View.VISIBLE);

        mNameTextView.setVisibility(View.GONE);
        mGroupTextView.setVisibility(View.GONE);
        mBirthdateTextView.setVisibility(View.GONE);
        mUniqueIDTextView.setVisibility(View.GONE);

    }

    private void initEditionElements(View rootView) {

        //Edition elements
        mNameCardView= rootView.findViewById(R.id.subject_name_cardview);
        mLastName1CardView = rootView.findViewById(R.id.lastname1_cardview);
        mLastName2CardView = rootView.findViewById(R.id.lastname2_cardview);
        mNameEditText = rootView.findViewById(R.id.name_edittext);
        mLastName1EditText = rootView.findViewById(R.id.lastname1_edittext);
        mLastName2EditText = rootView.findViewById(R.id.lastname2_edittext);
        mGroupSpinner = rootView.findViewById(R.id.group_spinner);
        mBirthdateLayout = rootView.findViewById(R.id.birthdate_spinner_layout);



        mGroupSpinner = rootView.findViewById(R.id.group_spinner);
        initGroupSpinner();

        mBirthdateDaySpinner = rootView.findViewById(R.id.birthdate_day_spinner);
        mBirthdateDaySpinner.setEnabled(false);
        initBirthdateDaySpinner(currentMonth, currentYear);

        mBirthdateMonthSpinner = rootView.findViewById(R.id.birthdate_month_spinner);
        initBirthdateMonthSpinner();

        mBirthdateYearSpinner= rootView.findViewById(R.id.birthdate_year_spinner);
        initBirthdateYearSpinner();

        mUniqueIDAutoComplete = rootView.findViewById(R.id.unique_id_auto);
        mUniqueIDAutoComplete.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});//Sets unique ids' input maximum characters to 4
        initUniqueIDAutoComplete();

        //Readable elements
        mNameTextView = rootView.findViewById(R.id.subject_name);
        mGroupTextView = rootView.findViewById(R.id.subject_group);
        mBirthdateTextView = rootView.findViewById(R.id.subject_birthdate);

        mUniqueIDTextView = rootView.findViewById(R.id.subject_unique_id);


    }

    private void initUniqueIDAutoComplete() {

        ArrayList<String> options = getAutoCompleteOptions();

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, R.id.text1, options);

        mUniqueIDAutoComplete.setAdapter(dataAdapter);

        mUniqueIDAutoComplete.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                if(subjectToEditUri.equals("null")){ //This is an Add-Subject request

                    Log.v(LOG_TAG, "addTextChangedListener: onTextChanged: " + charSequence);

                    //if the unique id typed already exists on SUBJECT_MAP, it return the its subject value, else it returns null
                    mItem = uniqueIdExists(charSequence);

                    if (mItem != null) {//This means a corresponding Subject value for that uniqueID key , has been found on SUBJECT_MAP hashmap

                        Log.v(LOG_TAG, "initUniqueIDAutoComplete: foundSubject != null");

                        //Notify user that a Subject has been found with unique id provided.
                        Toast.makeText(getActivity(),getResources().getString(R.string.existing_subject),Toast.LENGTH_SHORT).show();

                        //Hide save subject option from action bar
                        SubjectDetailActivity.addSubjectMenuItem.setVisible(false);

                        //Populate textviews to info visualization
                        fillTextViews(mItem);

                    }else{ //no value found , continue to the add subject process


                        //Show save subject option from action bar
                        SubjectDetailActivity.addSubjectMenuItem.setVisible(true);

                        //Show and clear edtitext to edition mode
                        emptyEditTexts();
                    }
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

        initBirthdateDaySpinner(0,Integer.parseInt(("" + DEFAULT_BIRTHDATE_YEAR).trim()));

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


        //Fill editable elements with subjects info
        mNameCardView.setVisibility(View.VISIBLE);
        mNameTextView.setVisibility(View.VISIBLE);

        String displayName = subject.getSubjectName() + " " + subject.getSubjectLastName1() + " " + subject.getSubjectLastName2();
        Log.v(LOG_TAG, "fillTextViews: displayName: " + displayName);

        mNameTextView.setText(displayName);
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
            String subjectID = subject.getSubjectID();

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


    private BroadcastReceiver saveReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String uniqueID = "";
            String name = "";
            String lastname1 = "";
            String lastname2 = "";
            String groupID = "";
            String birthdateDay = "";
            String birthdateMonth = "";
            String birthdateYear = "";
            String birthdate = "";

            uniqueID = mUniqueIDAutoComplete.getText().toString();
            name = mNameEditText.getText().toString();
            lastname1 = mLastName1EditText.getText().toString();
            lastname2 = mLastName2EditText.getText().toString();
            groupID = getGroupID(mGroupSpinner.getSelectedItem().toString());
            birthdateDay = mBirthdateDaySpinner.getSelectedItem().toString();
            birthdateMonth = mBirthdateMonthSpinner.getSelectedItem().toString();
            birthdateYear =  mBirthdateYearSpinner.getSelectedItem().toString();
            birthdate = birthdateDay + " / " + birthdateMonth + " / " + birthdateYear;

            Log.v(LOG_TAG, "uniqueID: " + uniqueID);
            Log.v(LOG_TAG, "subjectID: " + uniqueID);
            Log.v(LOG_TAG, "name: " + name);
            Log.v(LOG_TAG, "lastname1: " + lastname1);
            Log.v(LOG_TAG, "lastname2: " + lastname2);
            Log.v(LOG_TAG, "groupID: " + groupID);
            Log.v(LOG_TAG, "birthdate: " + birthdate);


            // Check if this is supposed to be a new pet
            // and check if all the fields in the editor are blank

            if (TextUtils.isEmpty(uniqueID)
                    && TextUtils.isEmpty(name)
                    && TextUtils.isEmpty(lastname1)
                    && TextUtils.isEmpty(lastname2)
                    && groupID.equals("0")
                    && birthdate.equals("1 / Enero / " + (currentYear - 18))) {
                // Since no fields were modified, we can return early without creating a new pet.
                // No need to create ContentValues and no need to do any ContentProvider operations.
                Log.v(LOG_TAG,"saveReceiver :no fields were modified");
                Toast.makeText(getActivity(),getString(R.string.no_changes_made),Toast.LENGTH_SHORT).show();

                getActivity().finish();
                return;

            }


            // Create a ContentValues object where column names are the keys,
            // and subject attributes from the editor are the values.
            ContentValues values = new ContentValues();

            values.put(SubjectEntry.COLUMN_UNIQUE_ID, uniqueID);
            values.put(SubjectEntry.COLUMN_SUBJECT_NAME, name);
            values.put(SubjectEntry.COLUMN_SUBJECT_LASTNAME1, lastname1);
            values.put(SubjectEntry.COLUMN_SUBJECT_LASTNAME2, lastname2);
            values.put(SubjectEntry.COLUMN_SUBJECT_GROUP, groupID);
            values.put(SubjectEntry.COLUMN_SUBJECT_BIRTHDATE, birthdate);


            String sId = "";


            if(!subjectToEditUri.equals("null")){ //This is a Subject Edition request

                int rowsAffected = getActivity().getContentResolver().update(Uri.parse(subjectToEditUri), values, null, null);

                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.

                    Toast.makeText(getActivity(), getString(R.string.detail_update_record_failed), Toast.LENGTH_SHORT).show();

                } else {

                    // Otherwise, the update was successful and we can display a toast.
                    Toast.makeText(getActivity(), getString(R.string.detail_update_record_successfull), Toast.LENGTH_SHORT).show();

                }

                sId = String.valueOf(ContentUris.parseId(Uri.parse(subjectToEditUri)));

            } else {
                // This is a NEW subject, so insert a new subject into the provider,
                // returning the content URI for the new subject.
                Uri newUri = getActivity().getContentResolver().insert(SubjectEntry.CONTENT_URI, values);

                Log.v(LOG_TAG, "receiver: newUri.toString(): " + newUri.toString());

                // Show a toast message depending on whether or not the insertion was successful.
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(getActivity(), getString(R.string.editor_subject_failed), Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(getActivity(), getString(R.string.editor_subject_success), Toast.LENGTH_SHORT).show();
                }

                sId = String.valueOf(ContentUris.parseId(newUri));

            }


            //Add new Subject to SUBJECT_MAP hashmap

            Log.v(LOG_TAG, "receiver sId: " +sId);


            Subject subject = new Subject(sId, name, lastname1, lastname2, birthdate, groupID);
            subject.setSubjectUniqueID(Integer.parseInt(uniqueID.trim()));

            //Add new subject to SubjectList on SubjectListActivity
            updateSubjectList(sId, subject);


            //After saving changes, go back to SubjectListActivity
            NavUtils.navigateUpTo(getActivity(), new Intent(getActivity(), SubjectListActivity.class));

        }



    };

    private void updateSubjectList(String sId, Subject subject) {
        //update SubjectListActivity List
        Constants.SUBJECT_MAP.put(sId, subject);
        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        // There is no pet data yet (until the loader finishes) so pass in null for the Cursor.


        mSubjectAdapter = new SubjectAdapter(getActivity(), getSubjectsArrayList());
        SubjectListActivity.subjectListView.setAdapter(mSubjectAdapter);
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

        } catch (IllegalArgumentException ex) {
            Log.e(LOG_TAG, "onDestroy IllegalArgumentException: " + ex.toString());
        }
    }

    public ArrayList<String> getAutoCompleteOptions() {

        ArrayList<String> autoCompleteOptions = new ArrayList<>();

        Iterator it = Constants.RECORD_MAP.values().iterator();

        while(it.hasNext()) {
            Record obj = (Record) it.next();
            String subjectID = obj.getSubject().getSubjectID();
            Log.v(LOG_TAG,"getAutoCompleteOptions: subjectID: " + subjectID);
            autoCompleteOptions.add(subjectID);
        }


        Log.v(LOG_TAG,"getAutoCompleteOptions: autoCompleteOptions.size(): " + autoCompleteOptions.size());
        return autoCompleteOptions;
    }



    private void updateDate() {

        Date currentTime = Calendar.getInstance().getTime();
        String day = (String) DateFormat.format("dd", currentTime);
        String monthNumber = (String) DateFormat.format("MM", currentTime);
        String year = (String) DateFormat.format("yyyy", currentTime);

        currentYear = Integer.parseInt(year.trim());
        currentMonth = Integer.parseInt(monthNumber.trim());
        currentDay = Integer.parseInt(day.trim());

        Log.v(LOG_TAG, "currenYear: " + currentYear);
        Log.v(LOG_TAG, "currentMonth: " + currentMonth);
        Log.v(LOG_TAG, "currentDay: " + currentDay);

    }
}

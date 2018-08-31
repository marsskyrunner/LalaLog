package com.mars_skyrunner.lalalog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * An activity representing a list of Subjects. This activity On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link SubjectDetailActivity} representing
 * item details.
 */
public class SubjectListActivity extends AppCompatActivity  {


    //LOG_TAG
    private String LOG_TAG = SubjectListActivity.class.getSimpleName();

    //List of Record objects in Record SQLite Database.
    List<Subject> subjects;
    
    //Cursor adapter to populate list of subjects
    public static SubjectAdapter mSubjectAdapter;

    //RecyblerView with list of subjects.
    public static ListView subjectListView;
    private ArrayList<Subject> subjectsArrayList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.subject_list_activity_menu, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.search_icon:
                onSearchRequested();
                break;

            case R.id.add_subject_icon:

                Bundle arguments = new Bundle();
                arguments.putString(Constants.ARG_ITEM_ID, "null");//This sets SubjectDetailActivitys EditionMode
                Intent intent = new Intent(this,SubjectDetailActivity.class);
                intent.putExtra(Constants.SUBJECT_BUNDLE,arguments);
                intent.putExtra(Constants.SUBJECT_URI_STRING,"null");//Subject to edit Uri
                this.startActivity(intent);

                break;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        subjectListView = (ListView) findViewById(R.id.subject_list);
        assert subjectListView != null;

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        subjectListView.setEmptyView(emptyView);

        ArrayList<Subject> subjects = getSubjectsArrayList();

        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        // There is no pet data yet (until the loader finishes) so pass in null for the Cursor.
        mSubjectAdapter = new SubjectAdapter(this,  subjects);
        subjectListView.setAdapter(mSubjectAdapter);


    }


    public static ArrayList<Subject> getSubjectsArrayList() {

        ArrayList<Subject> answer = new ArrayList<>();

        //TreeMap is used to sort SUBJECT_MAP hashmap keyset in order
        Map<String, Subject> treeMap = new TreeMap<String, Subject>(Constants.SUBJECT_MAP);

        Iterator iterator =  treeMap.values().iterator();

        while (iterator.hasNext()) {

            Subject subject = (Subject) iterator.next();

            answer.add(subject);

        }

        return answer;
    }
}

package com.mars_skyrunner.lalalog;


/*Creates an object called Record that represents a record on the logbook.
 * */


public class Record {


    private String mDate, mTime, mRecordText;
    private Subject mSubject;
    private String mRecordID;
    private String mRecordReference;



    /*
    * @param date : contains the date when the record was written
    * @param time : contains the time when the record was written
    * @param recordText : contains the text written by the Subject
    * @param subject : Subject object that contains the information of the author of the record.
    * */

    public Record(String date,String time, String recordText, Subject subject){
        mDate = date;
        mTime = time;
        mRecordText = recordText;
        mSubject = subject;
    }



    public Record(String recordId, String date, String time, String recordText, Subject subject){
        mDate = date;
        mTime = time;
        mRecordText = recordText;
        mSubject = subject;
        mRecordID = recordId;
    }

    
    

    public String getDate() {
        return mDate;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String mTime) {
        this.mTime = mTime;
    }

    public String getRecordText() {
        return mRecordText;
    }

    public void setRecordText(String mRecordText) {
        this.mRecordText = mRecordText;
    }

    public Subject getSubject() {
        return mSubject;
    }

    public void setSubject(Subject mSubject) {
        this.mSubject = mSubject;
    }


    public void setRecordID(String mRecordID) {
        this.mRecordID = mRecordID;
    }

    public String getRecordID(){
        return mRecordID;
    }

    public void setRecordReference(String mRecordReference) {
        this.mRecordReference = mRecordReference;
    }

    public String getRecordReference(){
        return mRecordReference;
    }

}
